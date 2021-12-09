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

import com.google.common.collect.Maps;

import br.felipstein.tardis.Main;
import br.felipstein.tardis.utils.ListUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class TellCommand implements CommandExecutor, TabCompleter {
	
	public static final Map<UUID, UUID> LAST_TELL = Maps.newHashMap();
	
	@Override
	public boolean onCommand(@NotNull CommandSender s, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(s instanceof Player sender) {
			if(args.length < 2) {
				s.sendMessage(MiniMessage.get().parse("<red>Use \"<#ff0000>/tell <jogador> <mensagem...><red>\"!"));
				return false;
			}
			Player target = Bukkit.getPlayer(args[0]);
			if(target == null) {
				s.sendMessage(Component.text("Jogador off-line, burro do caralho.").color(TextColor.color(255, 0, 0)));
				return false;
			}
			if(target == sender) {
				s.sendMessage(Component.text("Tá solitário, filha duma puta?").color(TextColor.color(255, 0, 0)));
				return false;
			}
			String message = "";
			for(int i = 1; i < args.length; ++i) {
				message += args[i] + " ";
			}
			message = message.trim();
			sender.sendMessage("§8(Mensagem para " + target.getName() + "): §2" + message);
			target.sendMessage("§8(Mensagem de " + sender.getName() + "): §2" + message);
			LAST_TELL.put(sender.getUniqueId(), target.getUniqueId());
			LAST_TELL.put(target.getUniqueId(), sender.getUniqueId());
			return true;
		}
		return false;
	}
	
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if(sender instanceof Player player) {
			if(args.length == 1) {
				List<String> playersName = Main.getInstance().getOnlinePlayersName();
				playersName.remove(player.getName());
				if(playersName.isEmpty()) {
					if(args[0].isEmpty()) {
						return Arrays.asList("<jogador>");
					}
					return new ArrayList<>();
				}
				return ListUtils.getElementsStartingWith(args[0], true, playersName);
			}
			if(args.length >= 2 && args[1].isEmpty()) {
				return Arrays.asList("<mensagem...>");
			}
			if(args.length >= 2 && !args[1].isEmpty()) {
				return new ArrayList<>();
			}
		}
		return null;
	}
	
}