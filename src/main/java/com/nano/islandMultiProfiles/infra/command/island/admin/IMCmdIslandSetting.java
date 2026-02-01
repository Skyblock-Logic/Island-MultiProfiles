package com.nano.islandMultiProfiles.infra.command.island.admin;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.island.IslandFlag;
import com.nano.islandMultiProfiles.annoitation.AnnotatedCommand;
import com.nano.islandMultiProfiles.annoitation.CommandMeta;
import com.nano.islandMultiProfiles.annoitation.CommandType;
import com.nano.islandMultiProfiles.annoitation.HandleIslandException;
import com.nano.islandMultiProfiles.service.IslandService;

@CommandType(player = true, console = false)
@CommandMeta(
	aliases = {"설정 <type> <true/false>"},
	usage = "설정 <type> <true/false>",
	description = "메인 섬의 세부사항을 설정합니다.",
	minArgs = 3,
	maxArgs = 3
)
@HandleIslandException
public class IMCmdIslandSetting extends AnnotatedCommand {

	private final IslandService islandService;

	public IMCmdIslandSetting(IslandService islandService) {
		this.islandService = islandService;
	}
	/**
	 * @note 섬의 세부설정을 할 수 있는 커맨드 입니다.
	 * @condition
	 * - 명령어를 쓰는 사람은 플레이어야 한다.
	 * - 설정은 섬장만 가능하다 ( Role : Admin )
	 */
	@Override
	public void executeCore(SuperiorSkyblock superiorSkyblock, CommandSender sender, Player player, String[] args) {
		String flagName = args[1];
		String flagValue1 = args[2];
		boolean flagValue2 = Boolean.parseBoolean(flagValue1);

		islandService.setting(player, flagName, flagValue2);
	}
	@Override
	public List<String> tabComplete(SuperiorSkyblock superiorSkyblock, CommandSender commandSender, String[] args) {
		if (args.length == 2) {
			return IslandFlag.values().stream().map(IslandFlag::getName).toList();
		}
		if (args.length == 3) {
			return List.of("true","false");
		}
		return List.of();
	}
}