package com.nano.islandMultiProfiles.api.info;

import java.util.Objects;
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
import com.nano.islandMultiProfiles.identity.policy.IslandNamePolicy;

public final class IslandInfoProvider {
	/**
	 * @note 프로필 슬롯 섬 조회 공통 로직
	 */
	public Optional<Island> findProfileIsland(Player player, int slot) {
		return findProfileIsland(player.getUniqueId(), slot);
	}

	public Optional<Island> findProfileIsland(UUID playerUUID, int slot) {
		UUID fakePlayerUUID = FakePlayerUUIDPolicy.issue(Objects.requireNonNull(playerUUID));
		UUID islandUUID = FakeIslandUUIdPolicy.issue(fakePlayerUUID, slot);
		return Optional.ofNullable(SuperiorSkyblockAPI.getIslandByUUID(islandUUID));
	}

	public Optional<Island> findIsland(String islandName, int slot) {
		return Optional.ofNullable(getIsland(islandName,slot));
	}

	public Island getIsland(String islandName, int slot){
		String encodeName = IslandNamePolicy.encode(islandName,slot);
		return SuperiorSkyblockAPI.getIsland(encodeName);
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
