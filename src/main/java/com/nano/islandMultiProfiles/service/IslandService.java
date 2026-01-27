package com.nano.islandMultiProfiles.service;

import static org.bukkit.Bukkit.*;

import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.enums.MemberRemoveReason;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandFlag;
import com.bgsoftware.superiorskyblock.api.island.PlayerRole;
import com.bgsoftware.superiorskyblock.api.world.algorithm.IslandCreationAlgorithm;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.nano.islandMultiProfiles.IslandMultiProfiles;
import com.nano.islandMultiProfiles.api.MultiProfileAPI;
import com.nano.islandMultiProfiles.identity.policy.FakeIslandUUIdPolicy;
import com.nano.islandMultiProfiles.identity.policy.FakePlayerUUIDPolicy;
import com.nano.islandMultiProfiles.identity.policy.IslandNamePolicy;
import com.nano.islandMultiProfiles.util.factory.IslandFactory;

public class IslandService {
	private final IslandMultiProfiles plugin;
	private final int MAX_ISLAND_SLOT = 3;

	public IslandService(IslandMultiProfiles plugin) {
		this.plugin = plugin;
	}

	/**
	 * @param owner 해당 땅의 실 주인 입니다.
	 * @note 메인 섬 생성메서드 입니다.
	 */
	public void createMainIsland(Player owner, String islandName) {
		String defaultSchematic = "desert";
		create(owner, islandName, defaultSchematic, 1);
	}

	/**
	 * @param player 해당 땅의 실 주인 입니다.
	 * @param slot 서브섬 번호입니다. ( 1번은 메인섬 입니다. ) ( 2 ~
	 * @note 서브 섬 생성 메서드 입니다.
	 */
	public void createSubIsland(Player player, int slot) {
		Island ownerIsland = getFakeIsland(player, 1);
		if (ownerIsland == null) {
			player.sendMessage(" 1번섬이 생성되지 않았습니다. 1번섬을 생성해주세요. ");
			return;
		}
		String defaultSchematic = ownerIsland.getSchematicName();
		String islandName = ownerIsland.getName();
		create(player, islandName, defaultSchematic, slot);
	}

	public void create(Player player, String islandName, String schematicName, int slot) {
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
	 * @param player 해당 땅의 실 주인 입니다.
	 * @param slot 서브 섬 번호입니다.
	 * @note 서브섬으로 이동하는 메서드 입니다.
	 */
	public void move(Player player, int slot) {
		Island island = getFakeIsland(player, slot);
		switchMember(player, slot);
		player.teleport(island.getCenterPosition().toLocation(getWorld("SuperiorWorld")));
	}

	/**
	 * @param player 권한을 업데이트 하려는 대상 입니다.
	 * @param slot ( 2~ ) 번째 서브섬 번호 입니다.
	 */
	public void switchMember(Player player, int slot) {
		SuperiorPlayer target = SuperiorSkyblockAPI.getPlayer(player.getUniqueId());

		PlayerRole role = target.getPlayerRole();
		Island island = target.getIsland();

		String islandName = IslandNamePolicy.decode(island.getName());
		Island toIsland = SuperiorSkyblockAPI.getIsland(IslandNamePolicy.encode(islandName, slot));

		toIsland.addMember(target, role);
		target.setIsland(toIsland);
		target.setPlayerRole(role);
	}

	/**
	 * @param island 메인 섬 입니다.
	 * @note 메인 섬 설정 값에 따라 서브섬도 동일하게 업데이트 해주는 메서드 입니다.
	 */
	public void update(Island island) {
		final SuperiorPlayer owner = island.getOwner();
		final UUID ownerUuid = Objects.requireNonNull(owner.getUniqueId());

		final boolean[] enabled = new boolean[IslandFlag.values().size()];
		for (IslandFlag flag : IslandFlag.values()) {
			enabled[flag.ordinal()] = island.hasSettingsEnabled(flag);
		}

		Bukkit.getAsyncScheduler().runNow(plugin, task -> {
			Island[] islandUuids = new Island[MAX_ISLAND_SLOT + 1];
			for (int i = 1; i <= MAX_ISLAND_SLOT; i++) {
				Island subIsland = getFakeIsland(ownerUuid, i);
				islandUuids[i] = subIsland;
			}

			Bukkit.getScheduler().runTask(plugin, () -> {
				applyFlagsChunked(islandUuids, enabled);
			});
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
	 * @param island 메인 섬 입니다.
	 * @note 메인 섬에 알바생이 추가/제거되면 서브섬에도 메인과 같은 알바생이 추가/제거되는 로직 입니다.
	 */
	public void updateCoopToSubIsland(Island island) {
		UUID ownerUuid = Objects.requireNonNull(island.getOwner().getUniqueId());
		for (int i = 1; i <= MAX_ISLAND_SLOT; i++) {
			Island subIsland = getFakeIsland(ownerUuid, i);
			subIsland.getCoopPlayers().clear();
			island.getCoopPlayers().forEach(subIsland::addCoop);
		}
	}

	/**
	 * @param player 섬 주인 ( 섬 주인이 아닌 경우 이름변경 불가 ) or 오피
	 * @param newName 새로운 이름 ( 중복 X )
	 */
	public void rename(Player player, String newName) {
		Island island = getFakeIsland(player, 1);
		if (island != null) {
			island.setName(newName);

			Island subIsland1 = getFakeIsland(player, 2);
			Island subIsland2 = getFakeIsland(player, 3);
			if (subIsland1 != null)
				subIsland1.setName(IslandNamePolicy.encode(newName, 2));
			if (subIsland2 != null)
				subIsland2.setName(IslandNamePolicy.encode(newName, 3));
		}
	}

	/**
	 * @note 가짜 섬의 정보를 가져오는 로직 입니다.
	 */
	private Island getFakeIsland(Player player, int slot) {
		UUID fakePlayerUUID = FakePlayerUUIDPolicy.issue(Objects.requireNonNull(player.getUniqueId()), slot);
		UUID islandUUID = FakeIslandUUIdPolicy.issue(fakePlayerUUID, slot);
		return SuperiorSkyblockAPI.getIslandByUUID(islandUUID);
	}

	private Island getFakeIsland(UUID playerUUID, int slot) {
		UUID fakePlayerUUID = FakePlayerUUIDPolicy.issue(Objects.requireNonNull(playerUUID), slot);
		UUID islandUUID = FakeIslandUUIdPolicy.issue(fakePlayerUUID, slot);
		return SuperiorSkyblockAPI.getIslandByUUID(islandUUID);
	}

	/**
	 * @param sender 섬 초대장을 보내는 사람 입니다. ( 섬 주인 )
	 * @param target 섬 초대장을 받는 사람 입니다.
	 * @note 섬 초대장을 발송하면 캐시에 등록하고 30초 뒤 자동으로 초대장이 만료되는 메서드 입니다.
	 */
	public void invitePlayer(Player sender, Player target) {
		MultiProfileAPI.getInstance()
			.getCache()
			.put(target, getFakeIsland(sender, 1));

		target.sendMessage(" 섬 초대 완 30초 이내 수락 ㄱ ");
	}

	/**
	 * @param player 섬 초대장을 수락/거절 하는 플레이어 입니다.
	 */
	public void acceptInvite(Player player, boolean accept) {
		Island island = MultiProfileAPI.getInstance()
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
	 * @param player 섬장 입니다.
	 * @param target 강퇴하려는 섬원 입니다.
	 */
	public void kickPlayer(Player player, Player target) {
		SuperiorPlayer targetSp = SuperiorSkyblockAPI.getPlayer(target.getUniqueId());
		SuperiorPlayer playerSp = SuperiorSkyblockAPI.getPlayer(player.getUniqueId());

		Island island = playerSp.getIsland();
		Island targetIsland = targetSp.getIsland();

		if (island == null) {
			// 메세지
			return;
		}

		if (targetIsland == null) {
			//메세지
			return;
		}

		if (island != targetIsland) {
			//메세지
			return;
		}
		island.removeMember(targetSp, MemberRemoveReason.KICK);
		targetSp.setIsland(null);
		player.sendMessage("추방 완료 " + target.getName());
		target.sendMessage(" 추방되었음");
	}
}
