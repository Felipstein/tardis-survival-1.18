package br.felipstein.tardis.commands.auth;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import br.felipstein.tardis.Account;

public class ChangePasswordCommand implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(sender instanceof Player player) {
			if(args.length == 0) {
				player.sendMessage("§cUtilize \"/trocarsenha <nova senha>\".");
				return false;
			}
			String newPassword = args[0];
			Account account = Account.getAccount(player);
			if(account.matchPassword(newPassword)) {
				player.sendMessage("§cEssa já é a sua senha.");
				return false;
			}
			if(newPassword.length() < 3) {
				player.sendMessage("§cUse uma senha com três ou mais caracteres, né? Pelo amor...");
				return false;
			}
			account.setPassword(newPassword);
			player.sendMessage("§aVocê trocou sua senha com êxito.");
			return true;
		}
		return false;
	}
	
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if(sender instanceof Player) {
			if(args.length == 1 && args[0].isEmpty()) {
				return Arrays.asList("<nova senha>");
			}
		}
		return null;
	}
	
}