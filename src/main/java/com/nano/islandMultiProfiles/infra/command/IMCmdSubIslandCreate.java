package com.nano.islandMultiProfiles.infra.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.commands.SuperiorCommand;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.world.algorithm.IslandCreationAlgorithm;
import com.nano.islandMultiProfiles.util.factory.IslandFactory;

public class IMCmdSubIslandCreate implements SuperiorCommand {
	@Override
	public List<String> getAliases() {
		return List.of("subcreate");
	}

	@Override
	public String getPermission() {
		return "superior.subisland.create";
	}

	@Override
	public String getUsage(Locale locale) {
		return "subcreate <player> <slot>";
	}

	@Override
	public String getDescription(Locale locale) {
		return "<player> 명의로 된 <slot> 번호의 서브섬을 생성합니다.";
	}

	@Override
	public int getMinArgs() {
		return 3;
	}

	@Override
	public int getMaxArgs() {
		return 4;
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

		Island ownerIsland = SuperiorSkyblockAPI.getIslandByUUID(player.getUniqueId());
		String islandName = ownerIsland.getName();
		String schematic = ownerIsland.getSchematicName();
		int slot = Integer.parseInt(args[2]);

		IslandFactory.createMultiProfileIsland(player, islandName, slot, schematic)
			.thenAccept(result -> {
				if (result.getStatus() == IslandCreationAlgorithm.IslandCreationResult.Status.SUCCESS) {
					player.sendMessage("§b섬 생성 완료: §f" + result.getIsland().getName());
				} else {
					player.sendMessage("§c섬 생성 실패: " + result.getStatus().name());
				}
			});
	}

	@Override
	public List<String> tabComplete(SuperiorSkyblock superiorSkyblock, CommandSender commandSender, String[] args) {
		if (args.length == 2) {
			return List.of("<섬이름>");
		}

		if (args.length == 3) {
			return List.of("1", "2", "3");
		}

		if (args.length == 4) {
			return new ArrayList<>(SuperiorSkyblockAPI.getSchematics().getSchematics());
		}

		return List.of();
	}
}
