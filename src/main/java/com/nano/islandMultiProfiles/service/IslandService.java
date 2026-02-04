package com.nano.islandMultiProfiles.service;

import static org.bukkit.Bukkit.*;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.enums.MemberRemoveReason;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandFlag;
import com.bgsoftware.superiorskyblock.api.island.PlayerRole;
import com.bgsoftware.superiorskyblock.api.world.algorithm.IslandCreationAlgorithm;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.nano.islandMultiProfiles.IslandMultiProfiles;
import com.nano.islandMultiProfiles.api.ProfileProviderCore;
import com.nano.islandMultiProfiles.api.info.IslandInfoProvider;
import com.nano.islandMultiProfiles.exception.IslandException;
import com.nano.islandMultiProfiles.identity.policy.FakeIslandUUIdPolicy;
import com.nano.islandMultiProfiles.identity.policy.IslandNamePolicy;
import com.nano.islandMultiProfiles.util.factory.IslandFactory;

public class IslandService {
	private final IslandMultiProfiles plugin;
	private final int MAX_ISLAND_SLOT = 3;
	private final int MAIN_SLOT = 1;

	private final IslandInfoProvider islandInfo = ProfileProviderCore.getInstance().getInfoProvider();

	public IslandService(IslandMultiProfiles plugin) {
		this.plugin = plugin;
	}

	/**
	 * @param owner 메인 섬을 만들 플레이어
	 * @note 메인 섬 생성 진입점
	 */
	public void createMainIsland(Player owner, String islandName) {
		String defaultSchematic = "desert";
		createIslandSlot(owner, islandName, defaultSchematic, MAIN_SLOT);
	}

	/**
	 * @param player 서브 섬을 만들 플레이어
	 * @param slot 슬롯 번호 (2~)
	 * @note 메인 섬 정보를 복사해 서브 섬을 생성
	 */
	public void createSubIsland(Player player, int slot) {
		var island = islandInfo.findProfileIsland(player, MAIN_SLOT)
			.orElseThrow(() -> new IslandException("메인 섬이 존재하지 않습니다. 메인섬을 먼저 생성해 주세요."));

		String defaultSchematic = island.getSchematicName();
		String islandName = IslandNamePolicy.encode(island.getName(), slot);
		createIslandSlot(player, islandName, defaultSchematic, slot);
	}

	public void createIslandSlot(Player player, String islandName, String schematicName, int slot) {
		IslandFactory.createMultiProfileIsland(player, islandName, slot, schematicName, true)
			.thenAccept(result -> {
				if (result.getStatus() == IslandCreationAlgorithm.IslandCreationResult.Status.SUCCESS) {
					player.sendMessage("§b섬 생성 완료: §f" + result.getIsland().getName());
				} else {
					player.sendMessage("§c섬 생성 실패: " + result.getStatus().name());
				}
			});
	}

	/**
	 * @param player 이동 대상 플레이어
	 * @param slot 슬롯 번호
	 * @note 슬롯 섬으로 이동 (권한/역할 스위칭 포함)
	 */
	public void teleportToSlotIsland(Player player, String islandName, int slot) {
		Island island = islandInfo.getIsland(islandName, slot);
		switchMemberToSlot(player, slot);
		player.teleport(island.getCenterPosition().toLocation(getWorld("SuperiorWorld")));
	}

	/**
	 * @param player 권한을 옮길 플레이어
	 * @param slot 슬롯 번호
	 */
	public void switchMemberToSlot(Player player, int slot) {
		SuperiorPlayer target = SuperiorSkyblockAPI.getPlayer(player.getUniqueId());

		PlayerRole role = target.getPlayerRole();
		Island island = target.getIsland();

		String islandName = IslandNamePolicy.decode(island.getName());
		Island toIsland = islandInfo.findIsland(islandName,slot)
				.orElseThrow(()->new IslandException("섬을 찾을 수 없습니다."));

		toIsland.addMember(target, role);
		target.setIsland(toIsland);
		target.setPlayerRole(role);
	}

	/**
	 * @param island 메인 섬
	 * @note 메인 섬의 플래그 설정을 모든 슬롯 섬에 반영
	 */
	public void syncFlagsToSlots(Island island) {
		final SuperiorPlayer owner = island.getOwner();
		final UUID ownerUuid = Objects.requireNonNull(owner.getUniqueId());

		final boolean[] enabled = new boolean[IslandFlag.values().size()];
		for (IslandFlag flag : IslandFlag.values()) {
			enabled[flag.ordinal()] = island.hasSettingsEnabled(flag);
		}

		Bukkit.getAsyncScheduler().runNow(plugin, task -> {
			Island[] islandUuids = new Island[MAX_ISLAND_SLOT + 1];
			for (int i = 1; i <= MAX_ISLAND_SLOT; i++) {
				Optional<Island> islandOpt = islandInfo.findProfileIsland(ownerUuid, i);
				if( islandOpt.isPresent() ){
					islandUuids[i] = islandOpt.get();
				}
			}

			Bukkit.getScheduler().runTask(plugin, () -> applyFlagsChunked(islandUuids, enabled));
		});
	}

	private void applyFlagsChunked(Island[] islandUuids, boolean[] enabled) {
		final int islandsPerTick = 1;

		final IslandFlag[] flags = IslandFlag.values().toArray(new IslandFlag[0]);
		final int[] index = {1};

		Bukkit.getScheduler().runTaskTimer(plugin, task -> {
			int processed = 0;

			while (index[0] <= MAX_ISLAND_SLOT && processed < islandsPerTick) {
				Island subIsland = islandUuids[index[0]++];
				if (subIsland == null)
					continue;

				for (IslandFlag flag : flags) {
					boolean target = enabled[flag.ordinal()];

					if (subIsland.hasSettingsEnabled(flag) == target)
						continue;

					if (target)
						subIsland.enableSettings(flag);
					else
						subIsland.disableSettings(flag);
				}

				processed++;
			}

			if (index[0] > MAX_ISLAND_SLOT) {
				task.cancel();
			}
		}, 1L, 1L);
	}

	/**
	 * @param island 메인 섬
	 * @note 메인 섬의 코옵 목록을 모든 슬롯 섬에 동기화
	 */
	public void syncCoopToSlots(Island island) {
		UUID ownerUuid = Objects.requireNonNull(island.getOwner().getUniqueId());
		for (int i = 1; i <= MAX_ISLAND_SLOT; i++) {
			Island subIsland = islandInfo.findProfileIsland(ownerUuid, i)
					.orElseThrow(()->new IslandException("섬을 찾을 수 없습니다."));

			subIsland.getCoopPlayers().clear();
			island.getCoopPlayers().forEach(subIsland::addCoop);
		}
	}

	/**
	 * @param player 섬 주인 (주인이 아니면 변경 불가)
	 * @param newName 새 섬 이름 (중복 불가)
	 */
	public void renameIsland(Player player, String newName) {
		Island island = islandInfo.findProfileIsland(player, MAIN_SLOT)
			.orElseThrow(()->new IslandException("섬을 찾을 수 없습니다."));

		island.setName(newName);

		Optional<Island> subIslandOpt2 = islandInfo.findProfileIsland(player, 2);
		subIslandOpt2.ifPresent(subIsland -> subIsland.setName(IslandNamePolicy.encode(newName, 2)));

		Optional<Island> subIslandOpt3 = islandInfo.findProfileIsland(player, 3);
		subIslandOpt3.ifPresent(subIsland -> subIsland.setName(IslandNamePolicy.encode(newName, 3)));
	}

	/**
	 * @param sender 초대를 보내는 플레이어 (섬 주인)
	 * @param target 초대를 받는 플레이어
	 * @note 초대 캐시에 저장하고 만료는 캐시 정책에 따름
	 */
	public void invitePlayer(Player sender, Player target) {
		Island mainIsland = islandInfo.getMainIsland(sender);

		ProfileProviderCore.getInstance()
			.getCache()
			.put(target, mainIsland);

		target.sendMessage(" 섬 초대가 도착했습니다. 30초 내에 수락해 주세요.");
	}

	/**
	 * @param player 초대를 수락/거절할 플레이어
	 */
	public void respondToInvite(Player player, boolean accept) {
		Island island = ProfileProviderCore.getInstance()
			.getCache()
			.get(player);

		if (accept) {
			SuperiorPlayer sp = SuperiorSkyblockAPI.getPlayer(player.getUniqueId());
			PlayerRole role = SuperiorSkyblockAPI.getRoles().getPlayerRole("Member");
			island.addMember(sp, role);
			sp.setIsland(island);
			sp.setPlayerRole(role);
		}
	}

	/**
	 * @param player 섬 주인
	 * @param target 추방 대상
	 */
	public void kickMember(Player player, Player target) {
		SuperiorPlayer targetSp = SuperiorSkyblockAPI.getPlayer(target.getUniqueId());
		SuperiorPlayer playerSp = SuperiorSkyblockAPI.getPlayer(player.getUniqueId());

		Island island = playerSp.getIsland();
		Island targetIsland = targetSp.getIsland();

		if (island == null) {
			// TODO: 메시지 처리
			return;
		}

		if (targetIsland == null) {
			// TODO: 메시지 처리
			return;
		}

		if (island != targetIsland) {
			// TODO: 메시지 처리
			return;
		}
		island.removeMember(targetSp, MemberRemoveReason.LEAVE);
		targetSp.setIsland(null);
		player.sendMessage("추방 완료: " + target.getName());
		target.sendMessage("섬에서 추방되었습니다.");
	}

	/**
	 * @param player 섬장
	 * @note 섬을 삭제하는 명령어 입니다. ( slot main, sub ) 전부 삭제 됩니다.
	 */
	public void disbandIsland(Player player) {
		SuperiorPlayer sp = SuperiorSkyblockAPI.getPlayer(player.getUniqueId());
		if ( sp.getPlayerRole() != SuperiorSkyblockAPI.getRoles().getPlayerRole("ADMIN") ){
			// 메세지 : 섬장이 아님.
			return;
		}

		Island mainIsland = ProfileProviderCore.getInstance()
			.getInfoProvider()
			.getMainIsland(player);

		SuperiorPlayer fakeSp = mainIsland.getOwner();
		UUID fakePlayerUUID = fakeSp.getUniqueId();

		for ( int i = 1; i <= MAX_ISLAND_SLOT; i++ ) {
			UUID fakeIslandUUId = FakeIslandUUIdPolicy.issue(fakePlayerUUID,i);
			Island slotIsland = SuperiorSkyblockAPI.getIslandByUUID(fakeIslandUUId);
			SuperiorSkyblockAPI.deleteIsland(slotIsland);
			//메세지 : 섬 삭제 메세지
		}


	}

	/**
	 * @param player 섬장
	 * @note 섬 스폰 위치를 변경하는 메서드 입니다.
	 */
	public void setSpawn(Player player) {
		SuperiorPlayer sp = SuperiorSkyblockAPI.getPlayer(player.getUniqueId());
		if ( sp.getPlayerRole() != SuperiorSkyblockAPI.getRoles().getPlayerRole("ADMIN") ){
			// 메세지 : 섬장이 아님.
			return;
		}

		Location nowLoc = player.getLocation();
		Island nowIsland = SuperiorSkyblockAPI.getIslandAt(nowLoc);

		Island mainIsland = ProfileProviderCore.getInstance()
			.getInfoProvider()
			.getMainIsland(player);

		if ( nowIsland != mainIsland ) {
			// 메세지 : 두 섬이 다름 ( 메인섬이 아님 )
			return;
		}
		mainIsland.setIslandHome(nowLoc);
		// 메세지 : 스폰 설젖완료

	}

	/**
	 * @param flagName 플래그 이름
	 * @param b 플래그 설정 값 ( true/false )
	 */
	public void setting(Player sender, String flagName, boolean b) {
		IslandFlag islandFlag = IslandFlag.getByName(flagName);

		Island mainIsland = ProfileProviderCore.getInstance()
			.getInfoProvider()
			.getMainIsland(sender);

		if (b) {
			mainIsland.enableSettings(islandFlag);
			// 메세지 : 설정 활성화
		} else {
			mainIsland.disableSettings(islandFlag);
			// 메세지 : 설정 비활성화
		}

	}

	public void upgradeIsland(Player player) {
		SuperiorPlayer sp = SuperiorSkyblockAPI.getPlayer(player.getUniqueId());

		Island mainIsland = ProfileProviderCore.getInstance()
			.getInfoProvider()
			.getMainIsland(player);

		if (sp.getPlayerRole() != SuperiorSkyblockAPI.getRoles().getPlayerRole("ADMIN")){
			// 메세지 : 어드민 아님
			return;
		}

		mainIsland.getUpgrades();
		// TODO : 업그레이드  Map 뭔지 봐야함

	}

	public void upgradeIsland(Player player, String islandName) {

	}

	public void upgradeIsland(Player player, String islandName, int step) {
	}

	// ---- 공통 설명용 주석 ----
	// - 슬롯 기반 섬은 FakePlayerUUIDPolicy + FakeIslandUUIdPolicy 규칙으로 조회한다.
	// - MAIN_SLOT(1) 기준으로 메인 섬 정보를 복사/동기화한다.
	// - 플래그 동기화는 서버 스레드에 분산 적용한다.
}
