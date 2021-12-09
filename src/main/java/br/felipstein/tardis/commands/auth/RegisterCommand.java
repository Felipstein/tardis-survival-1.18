package br.felipstein.tardis.commands.auth;

import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import br.felipstein.tardis.Account;
import br.felipstein.tardis.Auth;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RegisterCommand implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(sender instanceof Player player) {
			if(Account.getAccount(player) != null) {
				player.sendMessage("§cVocê já se registrou.");
				return false;
			}
			if(args.length == 0) {
				player.sendMessage("§cUse \"/register <senha>\" para se registrar.");
				return false;
			}
			String password = args[0];
			if(password.length() < 3) {
				player.sendMessage("§cUse uma senha com três ou mais caracteres, né? Pelo amor...");
				return false;
			}
			Account.registerAccount(player, password);
			Auth.getInstance().playerLogged(player, true);
			player.sendMessage("§4 » §aVocê se registrou com êxito!");
			player.sendMessage("§7§oManas aprovam!!!");
			player.sendMessage(" ");
			return true;
		}
		return false;
	}
	
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if(sender instanceof Player) {
			if(args.length == 1 && args[0].isEmpty()) {
				return Arrays.asList("<senha>");
			}
		}
		return null;
	}
	
}