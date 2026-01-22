package com.nano.islandMultiProfiles.service;

import static org.bukkit.Bukkit.*;

import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.IslandFlag;
import com.bgsoftware.superiorskyblock.api.world.algorithm.IslandCreationAlgorithm;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.nano.islandMultiProfiles.IslandMultiProfiles;
import com.nano.islandMultiProfiles.identity.policy.FakeIslandUUIdPolicy;
import com.nano.islandMultiProfiles.identity.policy.FakePlayerUUIDPolicy;
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
	public void createMainIsland(Player owner, String islandName){
		String defaultSchematic = "desert";
		create(owner, islandName, defaultSchematic, 1);
	}

	/**
	 * @param player 해당 땅의 실 주인 입니다.
	 * @param slot 서브섬 번호입니다. ( 1번은 메인섬 입니다. ) ( 2 ~
	 * @note 서브 섬 생성 메서드 입니다.
	 */
	public void createSubIsland(Player player, int slot){
		Island ownerIsland = getFakeIsland(player, 1);
		if ( ownerIsland == null ){
			player.sendMessage(" 1번섬이 생성되지 않았습니다. 1번섬을 생성해주세요. ");
			return;
		}
		String defaultSchematic = ownerIsland.getSchematicName();
		String islandName = ownerIsland.getName();
		create(player, islandName, defaultSchematic, slot);
	}

	public void create(Player player, String islandName, String schematicName, int slot){
		IslandFactory.createMultiProfileIsland(player, islandName, slot, schematicName)
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
	public void move(Player player, int slot){
		Island island = getFakeIsland(player, slot);
		player.teleport(island.getCenterPosition().toLocation(getWorld("SuperiorWorld")));
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
				if (subIsland == null) continue;

				for (IslandFlag flag : flags) {
					boolean target = enabled[flag.ordinal()];

					if (subIsland.hasSettingsEnabled(flag) == target) continue;

					if (target) subIsland.enableSettings(flag);
					else subIsland.disableSettings(flag);
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
	public void updateCoopToSubIsland(Island island){
		UUID ownerUuid = Objects.requireNonNull(island.getOwner().getUniqueId());
		for ( int i = 1; i <= MAX_ISLAND_SLOT; i++ ) {
			Island subIsland = getFakeIsland(ownerUuid, i);
			subIsland.getCoopPlayers().clear();
			island.getCoopPlayers().forEach(subIsland::addCoop);
		}
	}

	/**
	 * @note 가짜 섬의 정보를 가져오는 로직 입니다.
	 */
	private Island getFakeIsland(Player player, int slot){
		UUID fakePlayerUUID = FakePlayerUUIDPolicy.issue(Objects.requireNonNull(player.getUniqueId()), slot);
		UUID islandUUID = FakeIslandUUIdPolicy.issue(fakePlayerUUID, slot);
		return SuperiorSkyblockAPI.getIslandByUUID(islandUUID);
	}
	private Island getFakeIsland(UUID playerUUID, int slot){
		UUID fakePlayerUUID = FakePlayerUUIDPolicy.issue(Objects.requireNonNull(playerUUID), slot);
		UUID islandUUID = FakeIslandUUIdPolicy.issue(fakePlayerUUID, slot);
		return SuperiorSkyblockAPI.getIslandByUUID(islandUUID);
	}
}
