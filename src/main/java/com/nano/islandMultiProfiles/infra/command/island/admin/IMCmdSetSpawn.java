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
	aliases = {"스폰설정"},
	usage = "스폰설정",
	description = "현재 서있는 위치를 섬의 스폰으로 변경 합니다..",
	minArgs = 1,
	maxArgs = 1
)
@HandleIslandException
public class IMCmdSetSpawn extends AnnotatedCommand {

	private final IslandService islandService;

	public IMCmdSetSpawn(IslandService islandService) {
		this.islandService = islandService;
	}
	/**
	 * @note 섬의 스폰 위치를 설정하는 명령어 입니다.
	 * @condition
	 * - 명령어를 쓰는 사람은 플레이어야 한다.
	 * - 추방하는 플레이어가 섬장이여야 한다. ( Role : Admin )
	 * - 위치를 설정하는 곳은 메인 섬 내부여야 한다.
	 */
	@Override
	public void executeCore(SuperiorSkyblock superiorSkyblock, CommandSender sender, Player player, String[] args) {
		islandService.setSpawn(player);
	}
}