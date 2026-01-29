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
	aliases = {"초대"},
	usage = "초대 <player>",
	description = "<player> 에게 섬 초대장을 보냅니다.",
	minArgs = 2,
	maxArgs = 2
)
@HandleIslandException
public class IMCmdIslandInvite extends AnnotatedCommand {

	private final IslandService islandService;

	public IMCmdIslandInvite(IslandService islandService) {
		this.islandService = islandService;
	}
	/**
	 * @note 섬원을 초대하는 명령어 입니다.
	 * @condition
	 * - 명령어를 쓰는 사람은 플레이어야 한다.
	 * - 초대받는 플레이어는 반드시 같은 서버에 있어야 한다.
	 * - 초대하는 플레이어가 섬장이여야 한다. ( Role : Admin )
	 * - 초대받는 플레이어는 섬에 가입되어 있다면, 자동으로 섬이 삭제되고 초대한 사람의 섬에 귀속된다. ( /섬 수락 )
	 */

	@Override
	public void executeCore(SuperiorSkyblock superiorSkyblock, CommandSender sender, Player player, String[] args) {
		Player target = ProfileProviderCore.getInstance()
			.getProvider()
			.getPlayerByName(args[1]);

		islandService.invitePlayer(player, target);
	}
}
