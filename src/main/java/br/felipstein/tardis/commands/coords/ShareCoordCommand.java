package br.felipstein.tardis.commands.coords;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import br.felipstein.tardis.Coord;
import br.felipstein.tardis.Main;
import br.felipstein.tardis.utils.ListUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ShareCoordCommand implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(sender instanceof Player player) {
			if(args.length < 2) {
				player.sendMessage("§cUse \"/cc <nome da coordenada> <jogador>\" para compartilhar/descompartilhar a coordenada para tal jogador.");
				return false;
			}
			Coord coord = Coord.getCoord(player.getUniqueId(), args[0]);
			if(coord == null) {
				player.sendMessage("§cVocê não possui nenhuma coordenada com esse nome.");
				return false;
			}
			Player target = Bukkit.getPlayer(args[1]);
			if(target == null) {
				player.sendMessage(Component.text("O Jogador está off-line").color(TextColor.color(255, 0, 0)));
				return false;
			}
			if(target == player) {
				player.sendMessage(Component.text("Esse é você! Você não pode compartilhar uma coordenada para você.").color(TextColor.color(255, 0, 0)));
				return false;
			}
			if(coord.isSharing(target)) {
				coord.unshareLocation(target);
				player.sendMessage(MiniMessage.get().parse("<green>Você parou de compartilhar a sua coordenada <#00ff00>" + coord.getName() + " <green> com <yellow>" + target.getName() + "<green>."));
			} else {
				coord.shareLocation(target);
				player.sendMessage(MiniMessage.get().parse("<green>O jogador <yellow>" + target.getName() + " <green>agora pode ver sua coordenada <#00ff00>" + coord.getName() + "<green>."));
				target.sendMessage(MiniMessage.get().parse("<green>O jogador <yellow>" + player.getName() + " <green>compartilhou a coordenada <#00ff00>" + coord.getName() + " <green>com você. Use <white>/ic " + coord.getName() + " <green>para ver informações sobre ela."));
				player.sendMessage("§7Para descompartilhar, basta repetir o comando.");
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
				if(args[0].isEmpty() && coords.isEmpty()) {
					return Arrays.asList("<nome da coordenada>");
				}
				return ListUtils.getElementsStartingWith(args[0], true, coords);
			}
			if(args.length == 2) {
				List<String> playersName = Main.getInstance().getOnlinePlayersName();
				playersName.remove(player.getName());
				if(playersName.isEmpty()) {
					if(args[1].isEmpty()) {
						return Arrays.asList("<jogador>");
					}
					return new ArrayList<>();
				}
				return ListUtils.getElementsStartingWith(args[1], true, playersName);
			}
			if(args.length > 2) {
				return new ArrayList<>();
			}
		}
		return null;
	}
	
}