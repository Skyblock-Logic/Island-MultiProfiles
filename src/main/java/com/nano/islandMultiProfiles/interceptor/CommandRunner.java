package com.nano.islandMultiProfiles.interceptor;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.nano.islandMultiProfiles.annoitation.CommandType;
import com.nano.islandMultiProfiles.annoitation.HandleIslandException;
import com.nano.islandMultiProfiles.exception.IslandException;

public final class CommandRunner {

	public void run(Object command, SuperiorSkyblock superiorSkyblock, CommandSender sender, String[] args) {
		CommandType type = command.getClass().getAnnotation(CommandType.class);
		HandleIslandException handle = command.getClass().getAnnotation(HandleIslandException.class);

		Player player = null;

		// CommandType 기반 실행 주체 제한
		if (type != null) {
			boolean isPlayer = sender instanceof Player;

			if (type.player() && !isPlayer) {
				sender.sendMessage(type.message());
				return;
			}
			if (!type.console() && !isPlayer) {
				sender.sendMessage(type.message());
				return;
			}

			if (isPlayer) {
				player = (Player) sender;
			}
		} else {
			// 타입이 없으면 player는 있으면 넣어줌(탭완성/기본 처리에서 쓸 수 있게)
			if (sender instanceof Player p) {
				player = p;
			}
		}

		try {
			if (command instanceof CoreCommand core) {
				core.executeCore(superiorSkyblock, sender, player, args);
			} else {
				throw new IllegalStateException("Command must implement CoreCommand: " + command.getClass().getName());
			}
		} catch (IslandException e) {
			if (handle != null && handle.enabled()) {
				sender.sendMessage(e.getMessage());
				return;
			}
			throw e;
		}
	}

	public interface CoreCommand {
		void executeCore(SuperiorSkyblock superiorSkyblock, CommandSender sender, Player player, String[] args);
	}
}