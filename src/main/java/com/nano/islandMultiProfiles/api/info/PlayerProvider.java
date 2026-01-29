package com.nano.islandMultiProfiles.api.info;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.nano.islandMultiProfiles.exception.PlayerNotFoundException;

public class PlayerProvider {

	public Player getPlayerByName(String playerName) {
		return Optional.ofNullable(Bukkit.getPlayer(playerName))
			.orElseThrow(PlayerNotFoundException::new);
	}
}
