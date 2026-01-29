package com.nano.islandMultiProfiles.infra.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.nano.islandMultiProfiles.annoitation.AnnotatedCommand;
import com.nano.islandMultiProfiles.annoitation.CommandMeta;
import com.nano.islandMultiProfiles.annoitation.CommandType;
import com.nano.islandMultiProfiles.annoitation.HandleIslandException;
import com.nano.islandMultiProfiles.api.ProfileProviderCore;
import com.nano.islandMultiProfiles.service.IslandService;

@CommandType(player = true, console = false)
@CommandMeta(
	aliases = {"추방"},
	usage = "추방 <islandPlayer>",
	description = "섬원을 추방 합니다.",
	minArgs = 2,
	maxArgs = 2
)
@HandleIslandException
public class IMCmdPlayerKick extends AnnotatedCommand {

	private final IslandService islandService;

	public IMCmdPlayerKick(IslandService islandService) {
		this.islandService = islandService;
	}
	/**
	 * @note 섬원을 추방하는 명령어 입니다.
	 * @condition
	 * - 명령어를 쓰는 사람은 플레이어야 한다.
	 * - 추방하는 플레이어가 섬장이여야 한다. ( Role : Admin )
	 */
	@Override
	public void executeCore(SuperiorSkyblock superiorSkyblock, CommandSender sender, Player player, String[] args) {
		Player target = ProfileProviderCore.getInstance()
			.getProvider()
			.getPlayerByName(args[1]);

		islandService.kickMember(player, target);
	}
}