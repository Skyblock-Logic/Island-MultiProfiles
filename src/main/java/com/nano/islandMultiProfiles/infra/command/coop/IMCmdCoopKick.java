package com.nano.islandMultiProfiles.infra.command.coop;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.nano.islandMultiProfiles.annoitation.AnnotatedCommand;
import com.nano.islandMultiProfiles.annoitation.CommandMeta;
import com.nano.islandMultiProfiles.annoitation.CommandType;
import com.nano.islandMultiProfiles.annoitation.HandleIslandException;
import com.nano.islandMultiProfiles.infra.command.island.admin.IMCmdIslandInvite;
import com.nano.islandMultiProfiles.service.IslandCoopService;

@CommandType(
	player = true,
	console = true
)
@CommandMeta(
	aliases = {"알바"},
	usage = "알바 추방 <coopPlayer>",
	description = "알바로 추가된 플레이어를 추방 합니다.",
	minArgs = 3,
	maxArgs = 3
)
@HandleIslandException
public class IMCmdCoopKick extends AnnotatedCommand {

	private final IslandCoopService islandService;

	public IMCmdCoopKick(IslandCoopService islandService) {
		this.islandService = islandService;
	}
	/**
	 * @note 섬에 알바를 추방하는 기능
	 * @see IMCmdIslandInvite
	 * @condition
	 * - 명령어를 쓰는 사람은 플레이어야 한다.
	 * - 알바 추방은 섬장만 가능하다.
	 * - 없는 알바생은 추방할 수 없다.
	 */

	@Override
	public void executeCore(SuperiorSkyblock superiorSkyblock, CommandSender sender, Player player, String[] args) {
		islandService.kickCoopPlayer(player, args[2]);
	}
}