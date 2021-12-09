package br.felipstein.tardis.commands.coords;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import br.felipstein.tardis.Coord;
import br.felipstein.tardis.utils.Formats;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ListCoordsCommand implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(sender instanceof Player player) {
			List<Coord> coords = new ArrayList<>(Coord.getCoords(player));
			int totalMyself = coords.size();
			coords.addAll(Coord.getSharedsCoords(player));
			if(coords.isEmpty()) {
				player.sendMessage("§cVocê não tem nenhuma coordenada salvada ou compartilhada com você. Use \"/ac <nome>\" para adicionar uma coordenada.");
				return false;
			}
			player.sendMessage(" ");
			if(coords.size() == 1) {
				Coord coord = coords.get(0);
				player.sendMessage("§2Sua única coordenada é:");
				if(coord.getOwner().equals(player.getUniqueId())) {
					Component shareds = Component.newline();
					if(coord.getShared().isEmpty()) {
						shareds = shareds.append(Component.text("Essa coordenada não foi compartilhada com ninguém").color(TextColor.color(255, 0, 0)));
						shareds = shareds.append(Component.newline());
					} else {
						shareds = shareds.append(Component.text("Compartilhando essa coordenada com:").color(TextColor.color(154, 203, 255))).append(Component.newline());
						for(UUID shared : coord.getShared()) {
							String playerName;
							if(Bukkit.getPlayer(shared) != null) {
								playerName = Bukkit.getPlayer(shared).getName();
							} else {
								playerName = Bukkit.getOfflinePlayer(shared).getName();
							}
							shareds = shareds.append(Component.text(" - ").color(NamedTextColor.GOLD).append(Component.text(playerName).color(NamedTextColor.WHITE))).append(Component.newline());
						}
					}
					player.sendMessage(MiniMessage.get().parse("<gold>[ID <#ffd500>" + coord.getId() + "<gold>] - <gray>" + coord.getName() + " <white>- <green>" + Formats.locationToSimpleString(coord.getLocation()) + " <white>- ").append(Component.text(coord.getShared().size()).color(TextColor.color(174, 255, 0)).hoverEvent(HoverEvent.showText(shareds))));
				} else {
					String ownerName;
					if(Bukkit.getPlayer(coord.getOwner()) != null) {
						ownerName = Bukkit.getPlayer(coord.getOwner()).getName();
					} else {
						ownerName = Bukkit.getOfflinePlayer(coord.getOwner()).getName();
					}
					player.sendMessage(MiniMessage.get().parse("<red>[<#ff7138>Com.<red>] - <gray>" + coord.getName() + " <white>- <green>" + Formats.locationToSimpleString(coord.getLocation()) + " <white>- ").append(Component.text(ownerName).color(TextColor.color(255, 113, 56))));
				}
			} else {
				player.sendMessage("§2Exibindo §6" + coords.size() + " (" + totalMyself + " suas e " + (coords.size() - totalMyself) + " compartilhadas) §2coordenadas:");
				for(Coord coord : coords) {
					if(coord.getOwner().equals(player.getUniqueId())) {
						Component shareds = Component.newline();
						if(coord.getShared().isEmpty()) {
							shareds = shareds.append(Component.text("Essa coordenada não foi compartilhada com ninguém").color(TextColor.color(255, 0, 0)));
							shareds = shareds.append(Component.newline());
						} else {
							shareds = shareds.append(Component.text("Compartilhando essa coordenada com:").color(TextColor.color(154, 203, 255))).append(Component.newline());
							for(UUID shared : coord.getShared()) {
								String playerName;
								if(Bukkit.getPlayer(shared) != null) {
									playerName = Bukkit.getPlayer(shared).getName();
								} else {
									playerName = Bukkit.getOfflinePlayer(shared).getName();
								}
								shareds = shareds.append(Component.text(" - ").color(NamedTextColor.GOLD).append(Component.text(playerName).color(NamedTextColor.WHITE))).append(Component.newline());
							}
						}
						player.sendMessage(MiniMessage.get().parse("<gold>[ID <#ffd500>" + coord.getId() + "<gold>] - <gray>" + coord.getName() + " <white>- <green>" + Formats.locationToSimpleString(coord.getLocation()) + " <white>- ").append(Component.text(coord.getShared().size()).color(TextColor.color(174, 255, 0)).hoverEvent(HoverEvent.showText(shareds))));
					} else {
						String ownerName;
						if(Bukkit.getPlayer(coord.getOwner()) != null) {
							ownerName = Bukkit.getPlayer(coord.getOwner()).getName();
						} else {
							ownerName = Bukkit.getOfflinePlayer(coord.getOwner()).getName();
						}
						player.sendMessage(MiniMessage.get().parse("<red>[<#ff7138>Com.<red>] - <gray>" + coord.getName() + " <white>- <green>" + Formats.locationToSimpleString(coord.getLocation()) + " <white>- ").append(Component.text(ownerName).color(TextColor.color(255, 113, 56))));
					}
				}
			}
			player.sendMessage(Component.text("Veja informações sobre uma única coordenada com /ic <nome ou id>").color(TextColor.color(156, 133, 133)));
			player.sendMessage(" ");
			return true;
		}
		return false;
	}
	
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if(sender instanceof Player player) {
			return new ArrayList<>();
		}
		return null;
	}
	
}