package com.nano.islandMultiProfiles.api.info;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.PlayerRole;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.nano.islandMultiProfiles.exception.PlayerNotFoundException;
import com.nano.islandMultiProfiles.identity.policy.FakeIslandUUIdPolicy;
import com.nano.islandMultiProfiles.identity.policy.FakePlayerUUIDPolicy;

public final class IslandInfoProvider {
	/**
	 * @param playerUUID 유저 ID
	 * @return {@link Optional} {@link Island}
	 * @note 유저가 속해있는 섬을 찾는 메서드 입니다.
	 */
	public Optional<Island> findByIsland(UUID playerUUID) {

		SuperiorPlayer sp = SuperiorSkyblockAPI.getPlayer(playerUUID);
		Island island = sp.getIsland();

		return Optional.empty();
	}

	/**
	 * @param player 해당 유저의 메인섬을 가져옵니다.
	 *
	 */
	public Island getMainIsland(Player player) {
		SuperiorPlayer sp = SuperiorSkyblockAPI.getPlayer(player.getUniqueId());
		PlayerRole adminRole = SuperiorSkyblockAPI.getRoles().getPlayerRole("ADMIN");

		if ( sp.getIsland() != null && sp.getPlayerRole() == adminRole) {
			UUID fakePlayerUUID = FakePlayerUUIDPolicy.issue(player.getUniqueId());
			UUID fakeIslandUUID = FakeIslandUUIdPolicy.issue(fakePlayerUUID, 1);
			return SuperiorSkyblockAPI.getIslandByUUID(fakeIslandUUID);
		}
		// 만약, ADMIN 권한이 없을 경우 자신이 속한 섬의 메인섬을 찾는 코드
		Island island = sp.getIsland();
		return island.getIslandMembers(adminRole).stream()
			.findFirst()
			.map(adminSp -> {
				UUID fakePlayerUUID = FakePlayerUUIDPolicy.issue(adminSp.getUniqueId());
				UUID fakeIslandUUID = FakeIslandUUIdPolicy.issue(fakePlayerUUID, 1);
				return SuperiorSkyblockAPI.getIslandByUUID(fakeIslandUUID);
			})
			.orElseThrow(PlayerNotFoundException::new);
	}
}
