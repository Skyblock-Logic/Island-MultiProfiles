package com.nano.islandMultiProfiles.infra.command;

import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.commands.SuperiorCommand;
import com.nano.islandMultiProfiles.exception.IslandException;
import com.nano.islandMultiProfiles.service.IslandService;

public class IMCmdRename implements SuperiorCommand {

	private final IslandService islandService;

	public IMCmdRename(IslandService islandService) {
		this.islandService = islandService;
	}

	@Override
	public List<String> getAliases() {
		return List.of("이름변경");
	}

	@Override
	public String getPermission() {
		return "";
	}

	@Override
	public String getUsage(Locale locale) {
		return "이름변경 <name>";
	}

	@Override
	public String getDescription(Locale locale) {
		return "섬 이름을 변경합니다.";
	}

	@Override
	public int getMinArgs() {
		return 2;
	}

	@Override
	public int getMaxArgs() {
		return 3;
	}

	@Override
	public boolean canBeExecutedByConsole() {
		return true;
	}

	@Override
	public boolean displayCommand() {
		return true;
	}

	@Override
	public void execute(SuperiorSkyblock superiorSkyblock, CommandSender sender, String[] args) {
		if (!(sender instanceof Player player)) {
			sender.sendMessage("플레이어만 사용 가능합니다.");
			return;
		}

		Player target = Bukkit.getPlayer(args[1]);
		if (target == null) {
			player.sendMessage("존재하지 않는 플레이어입니다.");
			return;
		}

		try {
			islandService.rename(player, args[1]);
		} catch (IslandException e) {
			player.sendMessage(e.getMessage());
		}
	}


	@Override
	public List<String> tabComplete(SuperiorSkyblock superiorSkyblock, CommandSender commandSender, String[] args) {
		if (args.length == 2) {
			return List.of();
		}

		return List.of();
	}
}
