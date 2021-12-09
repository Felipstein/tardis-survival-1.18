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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class GlobalCommand implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(@NotNull CommandSender s, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		String message = "";
		for(int i = 0; i < args.length; ++i) {
			message += args[i] + " ";
		}
		sendGlobalMessage(s, LegacyComponentSerializer.legacyAmpersand().deserialize(message.trim()));
		return true;
	}
	
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if(sender instanceof Player) {
			if(args[0].isEmpty()) {
				return Arrays.asList("<mensagem...>");
			}
			if(args.length > 0) {
				return new ArrayList<>();
			}
		}
		return null;
	}
	
	public static void sendGlobalMessage(CommandSender sender, Component message) {
		Main main = Main.getInstance();
		String name = sender.getName();
		if(sender instanceof Player player) {
			Settings settings = main.getSettings();
			if(settings.teamsHabilited()) {
				if(settings.hasPlayerNameInA(name)) {
					main.getServer().getOnlinePlayers().stream().filter(target -> settings.hasPlayerNameInA(target.getName())).forEach(target -> target.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("§7[§bGERAL§7] §f<§a" + name + "§f> ").append(message)));
					main.getServer().getOnlinePlayers().stream().filter(target -> settings.hasPlayerNameInB(target.getName())).forEach(target -> target.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("§7[§bGERAL§7] §f<§c" + name + "§f> ").append(message)));
					main.getServer().getConsoleSender().sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("§7[§bGERAL§7] §6[A] §f<§7" + name + "§f> ").append(message));
				} else if(settings.hasPlayerNameInB(name)) {
					main.getServer().getOnlinePlayers().stream().filter(target -> settings.hasPlayerNameInA(target.getName())).forEach(target -> target.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("§7[§bGERAL§7] §f<§c" + name + "§f> ").append(message)));
					main.getServer().getOnlinePlayers().stream().filter(target -> settings.hasPlayerNameInB(target.getName())).forEach(target -> target.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("§7[§bGERAL§7] §f<§a" + name + "§f> ").append(message)));
					main.getServer().getConsoleSender().sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("§7[§bGERAL§7] §6[B] §f<§7" + name + "§f> ").append(message));
				} else {
					main.getServer().broadcast(LegacyComponentSerializer.legacyAmpersand().deserialize("§7[§bGERAL§7] §f<§6" + name + "§f> ").append(message));
				}
			} else {
				main.getServer().broadcast(LegacyComponentSerializer.legacyAmpersand().deserialize("§7[§bGERAL§7] §f<§7" + name + "§f> ").append(message));
			}
		} else {
			main.getServer().broadcast(LegacyComponentSerializer.legacyAmpersand().deserialize("§7[§bGERAL§7] §f<§4" + sender.getName() + "§f> ").append(message));
		}
	}
	
}