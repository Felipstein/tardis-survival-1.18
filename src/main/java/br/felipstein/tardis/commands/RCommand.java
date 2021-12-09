package br.felipstein.tardis.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class RCommand implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(@NotNull CommandSender s, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(s instanceof Player sender) {
			if(args.length < 1) {
				s.sendMessage(MiniMessage.get().parse("<red>Use \"<#ff0000>/r <mensagem...><red>\"!"));
				return false;
			}
			Map<UUID, UUID> lastTell = TellCommand.LAST_TELL;
			UUID senderUUID = sender.getUniqueId();
			if(!lastTell.containsKey(senderUUID)) {
				s.sendMessage(Component.text("Você não tem ninguem para responder.").color(TextColor.color(255, 0, 0)));
				return false;
			}
			UUID targetUUID = lastTell.get(senderUUID);
			Player target = Bukkit.getPlayer(targetUUID);
			if(target == null) {
				s.sendMessage(Component.text("O jogador na qual você teve uma conversinha picante recentemente está off-line.").color(TextColor.color(255, 0, 0)));
				return false;
			}
			String message = "";
			for(int i = 0; i < args.length; ++i) {
				message += args[i] + " ";
			}
			message = message.trim();
			sender.sendMessage("§8(Mensagem para " + target.getName() + "): §2" + message);
			target.sendMessage("§8(Mensagem de " + sender.getName() + "): §2" + message);
			return true;
		}
		return false;
	}
	
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if(sender instanceof Player player) {
			if(args[0].isEmpty()) {
				return Arrays.asList("<mensagem...>");
			}
			if(args.length > 0) {
				return new ArrayList<>();
			}
		}
		return null;
	}
	
}