package com.nano.islandMultiProfiles.service;

import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.nano.islandMultiProfiles.IslandMultiProfiles;
import com.nano.islandMultiProfiles.api.ProfileProviderCore;

public class IslandCoopService {
	private final IslandMultiProfiles plugin;
	private final ProfileProviderCore core = ProfileProviderCore.getInstance();

	public IslandCoopService(IslandMultiProfiles plugin) {
		this.plugin = plugin;
	}

	/**
	 * @param player 섬장
	 * @param playerName 초대하려는 사람의 닉네임
	 * @note 메인섬에 알바를 추가하는 코드
	 */
	public void inviteCoopPlayer(Player player, String playerName) {
		Player target = core.getProvider()
			.getPlayerByName(playerName);

		if ( !check(player) ){
			return;
		}

		Island mainIsland = core.getInfoProvider().getMainIsland(player);
		if ( mainIsland.getCoopPlayers().size() >= mainIsland.getCoopLimit() ){
			// 메세지 : 이미 알바가 꽉참
			return;
		}

		mainIsland.addCoop(SuperiorSkyblockAPI.getPlayer(target.getUniqueId()));
		// 메세지 : 알바 추가됨 메세지
	}

	public void kickCoopPlayer(Player player, String playerName) {
		Player target = core.getProvider()
			.getPlayerByName(playerName);

		if ( !check(player) ){
			return;
		}

		Island mainIsland = core.getInfoProvider().getMainIsland(player);
		mainIsland.removeCoop(SuperiorSkyblockAPI.getPlayer(target.getUniqueId()));
		// 메세지 : 추방됨
	}

	private boolean check(Player player){
		SuperiorPlayer adminSp = SuperiorSkyblockAPI.getPlayer(player.getUniqueId());
		if ( adminSp.getIsland() == null ){
			// 메세지 ( 섬에 가입이 되어있지 않거나 없음 )
			return false;
		}

		if (adminSp.getPlayerRole() != SuperiorSkyblockAPI.getRoles().getPlayerRole("ADMIN")){
			// 메세지 : 어드민 권한이 아님
			return false;
		}

		return true;
	}
}
