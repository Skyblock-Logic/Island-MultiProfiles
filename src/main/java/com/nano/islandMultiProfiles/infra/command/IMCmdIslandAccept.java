package com.nano.islandMultiProfiles.infra.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.nano.islandMultiProfiles.annoitation.AnnotatedCommand;
import com.nano.islandMultiProfiles.annoitation.CommandMeta;
import com.nano.islandMultiProfiles.annoitation.CommandType;
import com.nano.islandMultiProfiles.annoitation.HandleIslandException;
import com.nano.islandMultiProfiles.service.IslandService;

@CommandType(player = true, console = true)
@CommandMeta(
	aliases = {"수락"},
	usage = "수락",
	description = "섬 초대를 수락 합니다.",
	minArgs = 1,
	maxArgs = 1,
	console = true,
	display = true
)
@HandleIslandException
public class IMCmdIslandAccept extends AnnotatedCommand {

	private final IslandService islandService;

	public IMCmdIslandAccept(IslandService islandService) {
		this.islandService = islandService;
	}
	/**
	 * @note 섬 초대를 수락하는 명령어 입니다.
	 * @see IMCmdIslandInvite
	 * @condition
	 * - 명령어를 쓰는 사람은 플레이어야 한다.
	 * - 초대장이 존재해야 한다.
	 * - 초대받는 플레이어는 반드시 같은 서버에 있어야 한다.
	 * - 초대하는 플레이어가 섬장이여야 한다. ( Role : Admin )
	 * - 초대받는 플레이어는 섬에 가입되어 있다면, 자동으로 섬이 삭제되고 초대한 사람의 섬에 귀속된다. ( /섬 수락 )
	 */

	@Override
	public void executeCore(SuperiorSkyblock superiorSkyblock, CommandSender sender, Player player, String[] args) {
		islandService.acceptInvite(player,true);
	}
}
