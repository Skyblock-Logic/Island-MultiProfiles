package com.nano.islandMultiProfiles.infra.command.island.common;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.nano.islandMultiProfiles.annoitation.AnnotatedCommand;
import com.nano.islandMultiProfiles.annoitation.CommandMeta;
import com.nano.islandMultiProfiles.annoitation.CommandType;
import com.nano.islandMultiProfiles.annoitation.HandleIslandException;
import com.nano.islandMultiProfiles.api.ProfileProviderCore;
import com.nano.islandMultiProfiles.exception.IslandException;
import com.nano.islandMultiProfiles.service.IslandService;

@CommandType(player = true, console = false)
@CommandMeta(
	aliases = {"생성"},
	permission = "superior.island.create",
	usage = "생성 <player> <slot> <name/empty>",
	description = "<player> 명의로 된 <slot> 번호의 섬을 생성합니다.",
	minArgs = 3,
	maxArgs = 5,
	console = false
)
@HandleIslandException
public class IMCmdIslandCreate extends AnnotatedCommand {

	private final IslandService islandService;

	public IMCmdIslandCreate(IslandService islandService) {
		this.islandService = islandService;
	}

	@Override
	public void executeCore(SuperiorSkyblock superiorSkyblock, CommandSender sender, Player player, String[] args) {
		// 기존 규칙 유지:
		// args[1] = <player>
		// args[2] = <slot>
		// args[3] = <name> (slot=1일 때만 사용)
		Player target = ProfileProviderCore.getInstance()
			.getProvider()
			.getPlayerByName(args[1]);

		int slot = Integer.parseInt(args[2]);

		if (slot == 1) {
			if (args.length < 4 || args[3].isBlank()) {
				throw new IslandException("메인 섬 이름을 입력해주세요.");
			}
			islandService.createMainIsland(target, args[3]);
			return;
		}
		islandService.createSubIsland(target, slot);
	}

	@Override
	public List<String> tabComplete(SuperiorSkyblock superiorSkyblock, CommandSender commandSender, String[] args) {
		// 1) <player> 자동완성
		if (args.length == 2) {
			return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
		}

		// 2) <slot> 자동완성
		if (args.length == 3) {
			return List.of("1", "2", "3");
		}

		if (args.length == 4) {
			if ("1".equals(args[2])) {
				return List.of("<name>");
			}
			return List.of();
		}

		if (args.length == 5) {
			return new ArrayList<>(SuperiorSkyblockAPI.getSchematics().getSchematics());
		}

		return List.of();
	}
}