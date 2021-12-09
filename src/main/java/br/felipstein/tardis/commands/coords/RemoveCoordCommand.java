package br.felipstein.tardis.commands.coords;

import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import br.felipstein.tardis.Coord;
import br.felipstein.tardis.utils.ListUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RemoveCoordCommand implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {	
		if(sender instanceof Player player) {
			if(args.length == 0) {
				player.sendMessage("§cUse \"/rc <id ou nome>\" para remover uma coordenada. Use \"lc\" para ver todas suas coordenadas.");
				return true;
			}
			try {
				int id = Integer.parseInt(args[0]);
				Coord coord = Coord.getCoord(player.getUniqueId(), id);
				if(coord == null) {
					player.sendMessage("§cVocê não tem nenhuma coordenada com esse ID.");
					return false;
				}
				Coord.removeCoord(player, id);
				player.sendMessage("§aVocê removeu a coordenada §e" + coord.getName() + " §a.");
			} catch(NumberFormatException e) {
				String name = args[0];
				Coord coord = Coord.getCoord(player.getUniqueId(), name);
				if(coord == null) {
					player.sendMessage("§cVocê não tem nenhuma coordenada com esse nome.");
					return false;
				}
				Coord.removeCoord(player, name);
				player.sendMessage("§aVocê removeu a coordenada §e" + coord.getName() + "§a.");
			}
			return true;
		}
		return false;
	}
	
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if(sender instanceof Player player) {
			if(args.length == 1) {
				List<String> coords = Coord.getCoordsName(player.getUniqueId());
				if(coords.isEmpty() && args[0].isEmpty()) {
					return Arrays.asList("<id ou nome>");
				}
				return ListUtils.getElementsStartingWith(args[0], true, coords);
			}
			if(args.length > 1) {
				return new ArrayList<>();
			}
		}
		return null;
	}
	
}