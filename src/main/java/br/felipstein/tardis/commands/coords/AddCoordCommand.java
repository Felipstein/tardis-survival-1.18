package br.felipstein.tardis.commands.coords;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import br.felipstein.tardis.Coord;
import br.felipstein.tardis.utils.Formats;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class AddCoordCommand implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(sender instanceof Player player) {
			if(args.length == 0) {
				player.sendMessage("§cUse \"/ac <nome>\"");
				return false;
			}
			if(args.length > 1) {
				player.sendMessage("§cNão use espaços no nome da sua coordenada.");
				return false;
			}
			String name = args[0];
			try {
				Integer.parseInt(name);
				player.sendMessage("§cNão use apenas números no nome da sua coordenada.");
				return false;
			} catch(NumberFormatException e) {}
			Coord coord = Coord.getCoord(player.getUniqueId(), name);
			if(coord == null) {
				Coord.addCoord(player, name);
				player.sendMessage(MiniMessage.get().parse("<green>Coordenada <#00ff00>" + name + " <green>setada em <yellow>" + Formats.locationToSimpleString(player.getLocation()) + " <green>com êxito."));
			} else {
				Location location = player.getLocation();
				coord.setLocation(location);
				player.sendMessage(MiniMessage.get().parse("<green>Coordenada <#00ff00>" + name + " <green>alterado para <yellow>" + Formats.locationToSimpleString(location) + " <green>com êxito."));
			}
			return true;
		}
		return false;
	}
	
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if(sender instanceof Player) {
			if(args.length == 1 && args[0].isEmpty()) {
				return Arrays.asList("<nome>");
			}
			if(args.length > 1) {
				return new ArrayList<>();
			}
		}
		return null;
	}
	
}