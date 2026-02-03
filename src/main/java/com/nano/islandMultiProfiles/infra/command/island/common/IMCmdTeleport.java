package com.nano.islandMultiProfiles.infra.command.island.common;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.nano.islandMultiProfiles.annoitation.AnnotatedCommand;
import com.nano.islandMultiProfiles.annoitation.CommandMeta;
import com.nano.islandMultiProfiles.annoitation.CommandType;
import com.nano.islandMultiProfiles.annoitation.HandleIslandException;
import com.nano.islandMultiProfiles.service.IslandService;

@CommandType(player = true, console = false)
@CommandMeta(
	aliases = {"이동"},
	usage = "이동 <name> <slot>",
	description = "특정 섬 번호로 이동합니다.",
	minArgs = 3,
	maxArgs = 3,
	console = true,
	display = true
)
@HandleIslandException
public class IMCmdTeleport extends AnnotatedCommand {

	private final IslandService islandService;

	public IMCmdTeleport(IslandService islandService) {
		this.islandService = islandService;
	}

	@Override
	public void executeCore(SuperiorSkyblock superiorSkyblock, CommandSender sender, Player player, String[] args) {
		String islandName = args[1];
		int slot = Integer.parseInt(args[2]);

		islandService.teleportToSlotIsland(player, islandName, slot);
	}
}
