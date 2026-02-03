package com.nano.islandMultiProfiles.annoitation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.commands.SuperiorCommand;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.nano.islandMultiProfiles.api.ProfileProviderCore;
import com.nano.islandMultiProfiles.interceptor.CommandRunner;

public abstract class AnnotatedCommand implements SuperiorCommand, CommandRunner.CoreCommand {

	private final CommandMeta meta;
	private final CommandRunner runner = new CommandRunner();

	protected AnnotatedCommand() {
		this.meta = getClass().getAnnotation(CommandMeta.class);
		if (this.meta == null) {
			throw new IllegalStateException("@CommandMeta missing: " + getClass().getName());
		}
	}

	@Override
	public final List<String> getAliases() {
		return List.of(meta.aliases());
	}

	@Override
	public final String getPermission() {
		return meta.permission();
	}

	@Override
	public final String getUsage(Locale locale) {
		return meta.usage();
	}

	@Override
	public final String getDescription(Locale locale) {
		return meta.description();
	}

	@Override
	public final int getMinArgs() {
		return meta.minArgs();
	}

	@Override
	public final int getMaxArgs() {
		return meta.maxArgs();
	}

	@Override
	public final boolean canBeExecutedByConsole() {
		return meta.console();
	}

	@Override
	public final boolean displayCommand() {
		return meta.display();
	}

	@Override
	public final void execute(SuperiorSkyblock superiorSkyblock, CommandSender sender, String[] args) {
		runner.run(this, superiorSkyblock, sender, args);
	}

	/**
	 * usage 기반 자동 탭완성
	 *
	 * 규칙:
	 * - usage: "초대 <player>" 형태
	 * - args는 SuperiorSkyblock 커맨드 프레임워크 기준으로 args[1]부터 서브커맨드가 들어오는 것으로 보임
	 *   (기존 코드가 args[1]을 첫 토큰으로 사용)
	 *
	 * 지원 placeholder:
	 * - <player> : 전체 온라인 플레이어
	 * - <islandPlayer> : 내 섬에 속한 온라인 플레이어
	 * - <!islandPlayer> : 내 섬에 속하지 않은 온라인 플레이어
	 * - <coopPlayer> : 알바로 추가된 플레리어 목록
	 * - <slot> : 1,2,3
	 */
	@Override
	public List<String> tabComplete(SuperiorSkyblock superiorSkyblock, CommandSender sender, String[] args) {
		String usage = meta.usage();
		if (usage == null || usage.isBlank()) return List.of();

		List<String> tokens = new ArrayList<>(Arrays.stream(usage.trim().split("\\s+"))
			.filter(s -> !s.isBlank())
			.toList());

		// usage가 "이동 <...>" 처럼 커맨드 리터럴을 포함하면 제거
		if (!tokens.isEmpty()) {
			String first = tokens.getFirst();
			boolean isAliasLiteral = Arrays.stream(meta.aliases())
				.anyMatch(a -> a.equalsIgnoreCase(first));
			if (isAliasLiteral) tokens.removeFirst();
		}

		// 이제 args[1]이 tokens[0]에 대응되도록 유지
		int tokenIndex = args.length - 2;
		if (tokenIndex < 0 || tokenIndex >= tokens.size()) return List.of();

		String rawToken = tokens.get(tokenIndex);
		String prefix = Objects.toString(args[args.length - 1], "");

		if (isPlaceholder(rawToken)) {
			String key = unwrapPlaceholder(rawToken);
			return completePlaceholder(sender, key, prefix);
		}

		return filterByPrefix(List.of(rawToken), prefix);
	}

	private boolean isPlaceholder(String token) {
		return token.startsWith("<") && token.endsWith(">");
	}

	private String unwrapPlaceholder(String token) {
		return token.substring(1, token.length() - 1).trim();
	}

	private List<String> completePlaceholder(CommandSender sender, String key, String prefix) {
		return switch (key) {
			case "player" -> filterByPrefix(
				Bukkit.getOnlinePlayers().stream().map(Player::getName).toList(),
				prefix
			);

			case "slot" -> filterByPrefix(List.of("1", "2", "3"), prefix);

			case "islandPlayer" -> {
				if (!(sender instanceof Player player)) yield List.of();

				Island myIsland = SuperiorSkyblockAPI.getPlayer(player.getUniqueId()).getIsland();
				if (myIsland == null) yield List.of();

				List<String> names = Bukkit.getOnlinePlayers().stream()
					.filter(p -> SuperiorSkyblockAPI.getPlayer(p.getUniqueId()).getIsland() == myIsland)
					.map(Player::getName)
					.toList();

				yield filterByPrefix(names, prefix);
			}

			case "!islandPlayer" -> {
				if (!(sender instanceof Player player)) yield List.of();

				Island myIsland = SuperiorSkyblockAPI.getPlayer(player.getUniqueId()).getIsland();
				if (myIsland == null) yield filterByPrefix(
					Bukkit.getOnlinePlayers().stream().map(Player::getName).toList(),
					prefix
				);

				List<String> names = Bukkit.getOnlinePlayers().stream()
					.filter(p -> SuperiorSkyblockAPI.getPlayer(p.getUniqueId()).getIsland() != myIsland)
					.map(Player::getName)
					.toList();

				yield filterByPrefix(names, prefix);
			}

			case "coopPlayer" -> {
				if (!(sender instanceof Player player)) yield List.of();
				Island myIsland = ProfileProviderCore.getInstance().getInfoProvider().getMainIsland(player);

				List<String> names = myIsland.getCoopPlayers()
					.stream()
					.map(SuperiorPlayer::getName)
					.toList();

				yield filterByPrefix(names, prefix);
			}

			default -> List.of();
		};
	}

	private List<String> filterByPrefix(List<String> candidates, String prefix) {
		if (candidates == null || candidates.isEmpty()) return List.of();
		if (prefix == null || prefix.isBlank()) return distinctSorted(candidates);

		String p = prefix.toLowerCase(Locale.ROOT);
		return distinctSorted(
			candidates.stream()
				.filter(s -> s != null && s.toLowerCase(Locale.ROOT).startsWith(p))
				.toList()
		);
	}

	private List<String> distinctSorted(List<String> list) {
		return list.stream()
			.filter(Objects::nonNull)
			.distinct()
			.sorted(String.CASE_INSENSITIVE_ORDER)
			.collect(Collectors.toList());
	}
}