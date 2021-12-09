package br.felipstein.tardis.commands.coords;

import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import br.felipstein.tardis.Coord;
import br.felipstein.tardis.utils.Formats;
import br.felipstein.tardis.utils.ListUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class InfoCoordCommand implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(sender instanceof Player player) {
			if(args.length == 0) {
				player.sendMessage("§cUse \"/ic <nome ou id>\" para ver informações sobre tal coordenada.");
				return false;
			}
			Coord coord;
			try {
				int id = Integer.parseInt(args[0]);
				coord = Coord.getCoord(player, id);
				if(coord == null) {
					coord = Coord.getCoordShared(player, id);
				}
			} catch(NumberFormatException e) {
				coord = Coord.getCoord(player, args[0]);
				if(coord == null) {
					coord = Coord.getCoordShared(player, args[0]);
				}
			}
			if(coord == null) {
				player.sendMessage("§cVocê não possui nenhuma coordenada com esse nome/ID.");
				return false;
			}
			String name = coord.getName();
			player.sendMessage(" ");
			player.sendMessage("§2Informações sobre a coordenada §6" + name + "§2:");
			if(coord.getOwner().equals(player.getUniqueId())) {
				player.sendMessage("§aID: §f" + coord.getId());
				player.sendMessage("§aNome: §7" + name);
				player.sendMessage("§aLocalização: §f" + Formats.locationToSimpleString(coord.getLocation()));
				int dist = (int) coord.getLocation().distance(player.getLocation());
				player.sendMessage("§aDistância atual: §e" + dist + " bloco" + (dist > 1 ? "s" : ""));
				player.sendMessage("§aCompartilhada com:");
				if(coord.getShared().isEmpty()) {
					player.sendMessage("§c- Ninguém");
				} else {
					StringBuilder sb = new StringBuilder();
					if(coord.getShared().size() == 1) {
						String playerName;
						if(Bukkit.getPlayer(coord.getShared().get(0)) != null) {
							playerName = Bukkit.getPlayer(coord.getShared().get(0)).getName();
						} else {
							playerName = Bukkit.getOfflinePlayer(coord.getShared().get(0)).getName();
						}
						sb.append(playerName);
					} else {
						int index = 0;
						for(UUID uniqueId : coord.getShared()) {
							String playerName;
							if(Bukkit.getPlayer(uniqueId) != null) {
								playerName = Bukkit.getPlayer(uniqueId).getName();
							} else {
								playerName = Bukkit.getOfflinePlayer(uniqueId).getName();
							}
							if(index == coord.getShared().size() - 1) {
								sb.append(" §7e §f" + playerName);
							} else if(index == 0) {
								sb.append("§f" + playerName);
							} else {
								sb.append("§7, §f" + playerName);
							}
							++index;
						}
					}
					player.sendMessage("§6 » §f" + sb.toString() + "§7.");
				}
			} else {
				player.sendMessage("§aNome: §7" + name);
				player.sendMessage("§aLocalização: §f" + Formats.locationToSimpleString(coord.getLocation()));
				int dist = (int) coord.getLocation().distance(player.getLocation());
				player.sendMessage("§aDistância atual: §e" + dist + " bloco" + (dist > 1 ? "s" : ""));
				String playerName;
				if(Bukkit.getPlayer(coord.getOwner()) != null) {
					playerName = Bukkit.getPlayer(coord.getOwner()).getName();
				} else {
					playerName = Bukkit.getOfflinePlayer(coord.getOwner()).getName();
				}
				player.sendMessage("§aCompartilhada por: §c" + playerName);
			}
			player.sendMessage(" ");
			return true;
		}
		return false;
	}
	
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if(sender instanceof Player player) {
			if(args.length == 1) {
				List<String> coords = new ArrayList<>(Coord.getCoordsName(player));
				Coord.getSharedsCoords(player).forEach(coord -> coords.add(coord.getName()));
				if(coords.isEmpty()) {
					if(args[0].isEmpty()) {
						return Arrays.asList("<nome ou id>");
					}
					return new ArrayList<>();
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