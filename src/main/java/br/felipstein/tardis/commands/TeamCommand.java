package br.felipstein.tardis.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import br.felipstein.tardis.Main;
import br.felipstein.tardis.Settings;
import br.felipstein.tardis.Skorbord;
import br.felipstein.tardis.utils.ListUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class TeamCommand implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(sender instanceof Player player) {
			if(!player.isOp()) {
				return false;
			}
		}
		Settings settings = Main.getInstance().getSettings();
		if(!settings.teamsHabilited()) {
			sender.sendMessage("§cO sistema de equipes está desabilitado.");
			return false;
		}
		if(args.length < 2) {
			sender.sendMessage(Component.newline());
			sender.sendMessage("§2Para o controle de equipes A e B:");
			sender.sendMessage("§6 » §a/team A list §fLista os jogadores da equipe A.");
			sender.sendMessage("§6 » §a/team B list §fLista os jogadores da equipe B.");
			sender.sendMessage("§6 » §a/team A add <jogador> §fAdiciona tal jogador na equipe A.");
			sender.sendMessage("§6 » §a/team B add <jogador> §fAdiciona tal jogador na equipe B.");
			sender.sendMessage("§6 » §a/team A remove <jogador> §fRemove tal jogador da equipe A.");
			sender.sendMessage("§6 » §a/team B remove <jogador> §fRemove tal jogador da equipe B.");
			sender.sendMessage(Component.newline());
			return true;
		}
		char team;
		if(args[0].equalsIgnoreCase("A")) {
			team = 'A';
		} else if(args[0].equalsIgnoreCase("B")) {
			team = 'B';
		} else {
			sender.sendMessage(MiniMessage.get().parse("<red>As equipes disponíveis são <#ff0000>A <red>e <#ff0000>B<red>."));
			return false;
		}
		if(args[1].equals("list")) {
			if(team == 'A') {
				for(String playerName : settings.playersNameInA()) {
					sender.sendMessage("§6- §7" + playerName);
				}
			} else {
				for(String playerName : settings.playersNameInB()) {
					sender.sendMessage("§6- §7" + playerName);
				}
			}
		} else if(args[1].equals("add")) {
			String playerName = args[2];
			if(team == 'A') {
				if(settings.hasPlayerNameInA(playerName)) {
					sender.sendMessage("§cEsse jogador já está na equipe A.");
					return false;
				}
				if(settings.hasPlayerNameInB(playerName)) {
					sender.sendMessage("§cEsse jogador já está na equipe adversária (B).");
					return false;
				}
				settings.addPlayerNameInA(playerName);
				sender.sendMessage("§aJogador adicionado na equipe A.");
				Skorbord.resetupScoreboard();
			} else {
				if(settings.hasPlayerNameInB(playerName)) {
					sender.sendMessage("§cEsse jogador já está na equipe B.");
					return false;
				}
				if(settings.hasPlayerNameInA(playerName)) {
					sender.sendMessage("§cEsse jogador já está na equipe adversária (A).");
					return false;
				}
				settings.addPlayerNameInB(playerName);
				sender.sendMessage("§aJogador adicionado na equipe B.");
				Skorbord.resetupScoreboard();
			}
		} else if(args[1].equals("remove")) {
			String playerName = args[2];
			if(team == 'A') {
				if(settings.hasPlayerNameInB(playerName)) {
					sender.sendMessage("§cEsse jogador não está na equipe A, mas sim na equipe B.");
					return false;
				}
				if(!settings.hasPlayerNameInA(playerName)) {
					sender.sendMessage("§cEsse jogador não está na equipe A.");
					return false;
				}
				settings.removePlayerNameOfA(playerName);
				sender.sendMessage("§aJogador removido da equipe A.");
				Skorbord.resetupScoreboard();
			} else {
				if(settings.hasPlayerNameInA(playerName)) {
					sender.sendMessage("§cEsse jogador não está na equipe B, mas sim na equipe A.");
					return false;
				}
				if(!settings.hasPlayerNameInB(playerName)) {
					sender.sendMessage("§cEsse jogador não está na equipe B.");
					return false;
				}
				settings.removePlayerNameOfB(playerName);
				sender.sendMessage("§aJogador removido da equipe B.");
				Skorbord.resetupScoreboard();
			}
		} else {
			sender.sendMessage(MiniMessage.get().parse("<red>Argumento \"<#ff0000>" + args[0] + "<red>\" inválido."));
		}
		return false;
	}
	
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if(sender instanceof Player player && player.isOp()) {
			if(args.length == 1) {
				return ListUtils.getElementsStartingWith(args[0], true, Arrays.asList("A", "B"));
			}
			if(args.length == 2) {
				return ListUtils.getElementsStartingWith(args[1], Arrays.asList("list", "add", "remove"));
			}
			if(args.length == 3 && (args[1].equals("add") || args[1].equals("remove"))) {
				return ListUtils.getElementsStartingWith(args[2], true, Main.getInstance().getOnlinePlayersName());
			}
		}
		return new ArrayList<>();
	}
	
	
}