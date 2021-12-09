package br.felipstein.tardis;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class Skorbord {
	
	private static Skorbord instance;
	private final Main main;
	private final Settings settings;
	
	private Scoreboard teamA, teamB, scoreboard;
	
	private boolean teamsHabilited;
	
	private List<Objective> healths;
	
	private Skorbord(Main main) {
		this.main = main;
		settings = main.getSettings();
		teamsHabilited = settings.teamsHabilited();
		healths = new ArrayList<>();
		if(teamsHabilited) {
			teamA = main.getServer().getScoreboardManager().getNewScoreboard();
			teamB = main.getServer().getScoreboardManager().getNewScoreboard();
			Objective healthA = teamA.registerNewObjective("health", "dummy", MiniMessage.get().parse("% <#99ff33>❤"));
			Objective healthB = teamB.registerNewObjective("health", "dummy", MiniMessage.get().parse("% <#99ff33>❤"));
			healthA.setDisplaySlot(DisplaySlot.BELOW_NAME);
			healthB.setDisplaySlot(DisplaySlot.BELOW_NAME);
			healths.add(healthA);
			healths.add(healthB);
			Team teamAA = teamA.registerNewTeam("team_a");
			Team teamAB = teamA.registerNewTeam("team_b");
			Team teamBA = teamB.registerNewTeam("team_a");
			Team teamBB = teamB.registerNewTeam("team_b");
			teamAA.color(NamedTextColor.GREEN);
			teamAB.color(NamedTextColor.RED);
			teamBA.color(NamedTextColor.RED);
			teamBB.color(NamedTextColor.GREEN);
		} else {
			scoreboard = main.getServer().getScoreboardManager().getMainScoreboard();
			Objective health = scoreboard.getObjective("health");
			if(health != null) {
				health.unregister();
			}
			health = scoreboard.registerNewObjective("health", "dummy", MiniMessage.get().parse("% <#99ff33>❤"));
			health.setDisplaySlot(DisplaySlot.BELOW_NAME);
			healths.add(health);
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Objective obj : healths) {
					main.getServer().getOnlinePlayers().forEach(player -> obj.getScore(player.getName()).setScore((int) ((player.getHealth() * 100d) / 20d)));
				}
			}
		}.runTaskTimer(main, 0, 0);
	}
	
	private Scoreboard scoreboardOfPlayer(Player player) {
		if(settings.hasPlayerNameInA(player.getName())) {
			return teamA;
		} else if(settings.hasPlayerNameInB(player.getName())) {
			return teamB;
		} else {
			return null;
		}
	}
	
	private Scoreboard scoreboardOfEnemyTeamOfPlayer(Player player) {
		if(settings.hasPlayerNameInA(player.getName())) {
			return teamB;
		} else if(settings.hasPlayerNameInB(player.getName())) {
			return teamA;
		} else {
			return null;
		}
	}
	
	public static void applyScoreboard(Player player) {
		if(instance.teamsHabilited) {
			Scoreboard scoreboard = instance.scoreboardOfPlayer(player);
			if(scoreboard != null) {
				player.setScoreboard(scoreboard);
				Scoreboard enemyScoreboard = instance.scoreboardOfEnemyTeamOfPlayer(player);
				String name = player.getName();
				if(instance.settings.hasPlayerNameInA(name)) {
					scoreboard.getTeam("team_a").addEntry(name);
					enemyScoreboard.getTeam("team_a").addEntry(name);
					Component componentHovered = Component.text("Jogadores:").color(NamedTextColor.AQUA);
					for(String playerName : instance.settings.playersNameInA()) {
						componentHovered = componentHovered.append(Component.newline())
						.append(Component.text("- ").color(NamedTextColor.GOLD).append(Component.text(playerName).color(TextColor.color(255, 255, 255))));
					}
					player.sendMessage(Component.text("Você está na equipe A.").color(TextColor.color(0, 255, 0)).hoverEvent(HoverEvent.showText(componentHovered)));
				} else {
					scoreboard.getTeam("team_b").addEntry(name);
					enemyScoreboard.getTeam("team_b").addEntry(name);
					Component componentHovered = Component.text("Jogadores:").color(NamedTextColor.AQUA);
					for(String playerName : instance.settings.playersNameInB()) {
						componentHovered = componentHovered.append(Component.newline())
						.append(Component.text("- ").color(NamedTextColor.GOLD).append(Component.text(playerName).color(TextColor.color(255, 255, 255))));
					}
					player.sendMessage(Component.text("Você está na equipe B.").color(TextColor.color(0, 255, 0)).hoverEvent(HoverEvent.showText(componentHovered)));
				}
			} else {
				instance.main.sendOperatorMessage(MiniMessage.get().parse("<red>O jogador <#ff0000>" + player.getName() + " <red>não está presente em nenhuma equipe."));
				player.sendMessage("§cVocê não está em nenhuma equipe.");
			}
		} else {
			player.setScoreboard(instance.scoreboard);
		}
	}
	
	public static void applyScoreboard() {
		instance.main.getServer().getOnlinePlayers().forEach(player -> applyScoreboard(player));
	}
	
	public static void resetupScoreboard() {
		setupScoreboard(Main.getInstance());
	}
	
	public static void setupScoreboard(Main main) {
		instance = new Skorbord(main);
		applyScoreboard();
	}
	
}