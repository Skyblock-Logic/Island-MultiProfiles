package com.nano.islandMultiProfiles.infra.command.island.admin;

import java.util.List;

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
	aliases = {"업그레이드"},
	usage = "업그레이드 <me/섬이름> <next/<step>>",
	description = "메인 섬의 세부사항을 설정합니다.",
	minArgs = 3,
	maxArgs = 3,
	permission = "superior.island.upgrade"
)
@HandleIslandException
public class IMCmdIslandUpgrade extends AnnotatedCommand {

	private final IslandService islandService;

	public IMCmdIslandUpgrade(IslandService islandService) {
		this.islandService = islandService;
	}
	/**
	 * @note 섬을 업그레이드할 수 있는 커맨드
	 * @condition
	 * - 업그레이드는 일반유저가 사용할 수 없으며, 오로지 OP 권한으로 실행되어야 한다.
	 * - 섬이름으로 검색이 가능하며, 섬 이름이 없을 경우 자신의 섬이 강화가 됩니다.
	 */
	@Override
	public void executeCore(SuperiorSkyblock superiorSkyblock, CommandSender sender, Player player, String[] args) {
		String islandName = args[1];
		String upgradeStep = args[2];

		if ( islandName.equals("me") && upgradeStep.equals("next") ) {
			islandService.upgradeIsland(player);
			return;
		}

		if ( upgradeStep.equals("next") ) {
			islandService.upgradeIsland(player, islandName);
			return;
		}

		int step = Integer.parseInt(upgradeStep);
		islandService.upgradeIsland(player, islandName, step);


	}

	@Override
	public List<String> tabComplete(SuperiorSkyblock superiorSkyblock, CommandSender commandSender, String[] args) {
		if (args.length == 2) {
			return List.of("me","<섬이름>");
		}
		if (args.length == 3) {
			return List.of("next","<단계>");
		}
		return List.of();
	}
}