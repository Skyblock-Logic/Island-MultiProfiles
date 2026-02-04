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
	aliases = {"알바초대"},
	usage = "알바초대 <!islandPlayer>",
	description = "<player> 을 알바로 추가 합니다.",
	minArgs = 2,
	maxArgs = 2,
	console = true,
	display = true
)
@HandleIslandException
public class IMCmdCoopInvite extends AnnotatedCommand {

	private final IslandCoopService islandService;

	public IMCmdCoopInvite(IslandCoopService islandService) {
		this.islandService = islandService;
	}
	/**
	 * @note 섬에 알바를 추가하는 명령어 입니다.
	 * @see IMCmdIslandInvite
	 * @condition
	 * - 명령어를 쓰는 사람은 플레이어야 한다.
	 * - 알바 추가는 섬장만 가능하다. ( ADMIN 권한 )
	 * - 초대받는 플레이어는 반드시 같은 서버에 있어야 한다.
	 * - 섬에 알바 자리가 남아야 한다.
	 * - 알바를 받는 사람은 가입할 수 있는 알바의 한도가 넘지않아야 한다.
	 */

	@Override
	public void executeCore(SuperiorSkyblock superiorSkyblock, CommandSender sender, Player player, String[] args) {
		islandService.inviteCoopPlayer(player, args[1]);
	}
}