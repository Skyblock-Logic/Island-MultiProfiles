package com.nano.islandMultiProfiles.infra.command.island.admin;

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
	aliases = {"삭제"},
	usage = "삭제",
	description = "섬을 삭제 합니다.",
	minArgs = 1,
	maxArgs = 1
)
@HandleIslandException
public class IMCmdIslandDisband extends AnnotatedCommand {

	private final IslandService islandService;

	public IMCmdIslandDisband(IslandService islandService) {
		this.islandService = islandService;
	}
	/**
	 * @note 섬을 삭제하는 명령어 입니다.
	 * @condition
	 * - 명령어를 쓰는 사람은 플레이어야 한다.
	 * - 이 명령어를 쓰는 플레이어는 섬장이여야 한다. ( Role: Admin )
	 */

	@Override
	public void executeCore(SuperiorSkyblock superiorSkyblock, CommandSender sender, Player player, String[] args) {
		islandService.disbandIsland(player);
	}
}
