package com.nano.islandMultiProfiles.infra.command;

import java.util.List;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.commands.SuperiorCommand;
import com.nano.islandMultiProfiles.exception.IslandException;
import com.nano.islandMultiProfiles.service.IslandService;

public class IMCmdIslandInvite implements SuperiorCommand {

	private final IslandService islandService;

	public IMCmdIslandInvite(IslandService islandService) {
		this.islandService = islandService;
	}

	@Override
	public List<String> getAliases() {
		return List.of("초대");
	}

	@Override
	public String getPermission() {
		return "";
	}

	@Override
	public String getUsage(Locale locale) {
		return "초대 <player>";
	}

	@Override
	public String getDescription(Locale locale) {
		return "<player> 을 초대 합니다.";
	}

	@Override
	public int getMinArgs() {
		return 2;
	}

	@Override
	public int getMaxArgs() {
		return 3;
	}

	@Override
	public boolean canBeExecutedByConsole() {
		return true;
	}

	@Override
	public boolean displayCommand() {
		return true;
	}

	/**
	 * @note 섬원을 초대하는 명령어 입니다.
	 * @condition
	 * - 명령어를 쓰는 사람은 플레이어야 한다.
	 * - 초대받는 플레이어는 반드시 같은 서버에 있어야 한다.
	 * - 초대하는 플레이어가 섬장이여야 한다. ( Role : Admin )
	 * - 초대받는 플레이어는 섬에 가입되어 있다면, 자동으로 섬이 삭제되고 초대한 사람의 섬에 귀속된다. ( /섬 수락 )
	 * -
	 *
	 */
	@Override
	public void execute(SuperiorSkyblock superiorSkyblock, CommandSender sender, String[] args) {
		if (!(sender instanceof Player player)) {
			sender.sendMessage("플레이어만 사용 가능합니다.");
			return;
		}

		Player target = Bukkit.getPlayer(args[1]);
		if (target == null) {
			player.sendMessage("존재하지 않는 플레이어입니다.");
			return;
		}

		try {
			islandService.invitePlayer(player, target);
		} catch (IslandException e) {
			player.sendMessage(e.getMessage());
		}
	}

	@Override
	public List<String> tabComplete(SuperiorSkyblock superiorSkyblock, CommandSender commandSender, String[] args) {
		if (args.length == 2) {
			return List.of("초대");
		}
		if( args.length == 3 ){
			return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
		}

		return List.of();
	}
}
