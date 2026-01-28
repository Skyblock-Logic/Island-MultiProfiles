package com.nano.islandMultiProfiles.infra.command;

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
	aliases = {"이름"},
	usage = "이름 <new name>",
	description = "섬의 명칭을 변경합니다.",
	minArgs = 2,
	maxArgs = 2,
	console = true,
	display = true
)
@HandleIslandException
public class IMCmdRename extends AnnotatedCommand {

	private final IslandService islandService;

	public IMCmdRename(IslandService islandService) {
		this.islandService = islandService;
	}

	@Override
	public void executeCore(SuperiorSkyblock superiorSkyblock, CommandSender sender, Player player, String[] args) {
		islandService.rename(player, args[1]);
	}
}
