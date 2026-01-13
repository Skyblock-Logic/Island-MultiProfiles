package com.nano.islandMultiProfiles.util.factory;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.PlayerRole;
import com.bgsoftware.superiorskyblock.api.schematic.Schematic;
import com.bgsoftware.superiorskyblock.api.world.algorithm.IslandCreationAlgorithm;
import com.bgsoftware.superiorskyblock.api.wrappers.BlockPosition;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.nano.islandMultiProfiles.infra.identity.policy.FakeIslandUUIdPolicy;
import com.nano.islandMultiProfiles.infra.identity.policy.FakePlayerUUIDPolicy;

public final class IslandFactory {
	private IslandFactory() {}

	/**
	 * 가짜 UUID 정책을 기반으로 새로운 섬을 생성합니다.
	 */
	public static CompletableFuture<IslandCreationAlgorithm.IslandCreationResult> createMultiProfileIsland(Player player, String islandName, int slot, String schematicName) {

		SuperiorPlayer owner = SuperiorSkyblockAPI.getPlayer(player.getUniqueId());

		// 1. UUID 및 가짜 플레이어 설정
		UUID fakePlayerUUID = FakePlayerUUIDPolicy.issue(player.getUniqueId(), slot);
		SuperiorPlayer fakeSp = SuperiorSkyblockAPI.getPlayer(fakePlayerUUID);
		UUID islandUUID = FakeIslandUUIdPolicy.issue(fakePlayerUUID, slot);

		// 2. 생성 파라미터 준비
		BlockPosition position = SuperiorSkyblockAPI.getGrid().getLastIslandPosition();
		Schematic schematic = SuperiorSkyblockAPI.getSchematic(schematicName == null ? "desert" : schematicName);

		// 3. 섬 생성 알고리즘 실행
		return SuperiorSkyblockAPI.getGrid()
			.getIslandCreationAlgorithm()
			.createIsland(islandUUID, fakeSp, position, islandName, schematic)
			.thenApply(result -> {
				if (result.getStatus() == IslandCreationAlgorithm.IslandCreationResult.Status.SUCCESS) {
					Island island = result.getIsland();

					// 4. 추가 설정 (관리 권한 부여 및 그리드 등록)
					PlayerRole role = SuperiorSkyblockAPI.getRoles().getPlayerRole("ADMIN");
					island.addMember(owner, role);

					SuperiorSkyblockAPI
						.getGrid()
						.getIslandsContainer()
						.addIsland(island);
				}
				return result;
			});
	}
}
