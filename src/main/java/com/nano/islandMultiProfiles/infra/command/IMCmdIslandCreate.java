package com.nano.islandMultiProfiles.infra.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.commands.SuperiorCommand;
import com.nano.islandMultiProfiles.exception.IslandException;
import com.nano.islandMultiProfiles.service.IslandService;

public class IMCmdIslandCreate implements SuperiorCommand {
	private final IslandService islandService;

	public IMCmdIslandCreate(IslandService islandService) {
		this.islandService = islandService;
	}

	@Override
	public List<String> getAliases() {
		return List.of("subcreate");
	}

	@Override
	public String getPermission() {
		return "superior.island.create";
	}

	@Override
	public String getUsage(Locale locale) {
		return "subcreate <player> <slot> <name>";
	}

	@Override
	public String getDescription(Locale locale) {
		return "<player> 명의로 된 <slot> 번호의 섬을 생성합니다.";
	}

	@Override
	public int getMinArgs() {
		return 3;
	}

	@Override
	public int getMaxArgs() {
		return 5;
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

		int slot;
		try {
			slot = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			player.sendMessage("슬롯은 숫자여야 합니다.");
			return;
		}

		try {
			if ( slot == 1 ) {
				islandService.createMainIsland(target,args[3]);
			}
			else if ( slot > 1 ) {
				islandService.createSubIsland(target,slot);
			}
		} catch (IslandException e) {
			player.sendMessage(e.getMessage());
		}
	}

	@Override
	public List<String> tabComplete(SuperiorSkyblock superiorSkyblock, CommandSender commandSender, String[] args) {
		if (args.length == 2) {
			return List.of(commandSender.getName());
		}

		if (args.length == 3) {
			return List.of("1", "2", "3");
		}
		if(  args[2].equals("1") ) {
			if (args.length == 4) {
				return List.of("<name>");
			}
		}

		if (args.length == 5) {
			return new ArrayList<>(SuperiorSkyblockAPI.getSchematics().getSchematics());
		}

		return List.of();
	}
}
