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
	usage = "알바 나가기 <islandName>",
	description = "현재 자신이 알바로 추가된 섬에서 나갑니다.",
	minArgs = 3,
	maxArgs = 3
)
@HandleIslandException
public class IMCmdCoopLeave extends AnnotatedCommand {

	private final IslandCoopService islandService;

	public IMCmdCoopLeave(IslandCoopService islandService) {
		this.islandService = islandService;
	}
	/**
	 * @note 알바를 나가는 기능
	 * @see IMCmdIslandInvite
	 * @condition
	 * - 명령어를 쓰는 사람은 플레이어야 한다.
	 * - 존재하지 않는 섬은 나갈 수 없다.
	 */

	@Override
	public void executeCore(SuperiorSkyblock superiorSkyblock, CommandSender sender, Player player, String[] args) {
		islandService.leaveCoop(player, args[2]);
	}
}