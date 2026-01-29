package com.nano.islandMultiProfiles.util.factory;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.schematic.Schematic;
import com.bgsoftware.superiorskyblock.api.world.algorithm.IslandCreationAlgorithm;
import com.bgsoftware.superiorskyblock.api.wrappers.BlockPosition;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.nano.islandMultiProfiles.identity.policy.FakeIslandUUIdPolicy;
import com.nano.islandMultiProfiles.identity.policy.FakePlayerUUIDPolicy;
import com.nano.islandMultiProfiles.identity.policy.IslandNamePolicy;

public final class IslandFactory {
	private IslandFactory() {}

	/**
	 * 가짜 UUID 정책을 기반으로 새로운 섬을 생성합니다.
	 */
	public static CompletableFuture<IslandCreationAlgorithm.IslandCreationResult> createMultiProfileIsland(
		Player player,
		String islandName,
		int slot,
		String schematicName,
		boolean isOwner
	) {
		SuperiorPlayer ow = SuperiorSkyblockAPI.getPlayer(player.getUniqueId());

		// 1. UUID 및 가짜 플레이어 설정
		UUID fakePlayerUUID = FakePlayerUUIDPolicy.issue(player.getUniqueId());
		SuperiorPlayer fakeSp = SuperiorSkyblockAPI.getPlayer(fakePlayerUUID);
		UUID islandUUID = FakeIslandUUIdPolicy.issue(fakePlayerUUID, slot);

		// 2. 생성 파라미터 준비
		BlockPosition position = SuperiorSkyblockAPI.getGrid().getLastIslandPosition();
		Schematic schematic = SuperiorSkyblockAPI.getSchematic(schematicName == null ? "desert" : schematicName);

		islandName = slot == 1 ? islandName : IslandNamePolicy.encode(islandName,slot);

		// 3. 섬 생성 알고리즘 실행
		return SuperiorSkyblockAPI.getGrid()
			.getIslandCreationAlgorithm()
			.createIsland(islandUUID, fakeSp, position, islandName, schematic)
			.thenApply(result -> {
				if (result.getStatus() == IslandCreationAlgorithm.IslandCreationResult.Status.SUCCESS) {
					Island island = result.getIsland();
					SuperiorSkyblockAPI
						.getGrid()
						.getIslandsContainer()
						.addIsland(island);

					if (isOwner){
						island.addMember(ow, SuperiorSkyblockAPI.getRoles().getPlayerRole("ADMIN"));
					}
				}
				return result;
			});
	}
}
