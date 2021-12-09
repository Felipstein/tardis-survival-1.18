package br.felipstein.tardis.commands.auth;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Maps;

import br.felipstein.tardis.Account;
import br.felipstein.tardis.Auth;
import br.felipstein.tardis.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class LoginCommand implements CommandExecutor, TabCompleter, Listener {
	
	private Map<Player, Integer> tried = Maps.newHashMap();
	
	public LoginCommand() {
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if(tried.containsKey(player)) {
			tried.remove(player);
		}
	}
	
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if(sender instanceof Player player) {
			Account account = Account.getAccount(player);
			if(account == null) {
				player.sendMessage("§cVocê ainda não se registrou! Use \"/register <senha>\" para se registrar.");
				return false;
			}
			if(!Auth.getInstance().playerWaitingLogin(player)) {
				player.sendMessage("§cVocê já está logado.");
				return false;
			}
			if(args.length == 0) {
				player.sendMessage("§cUse \"/login <senha>\" para logar.");
				return false;
			}
			String password = args[0];
			if(!account.matchPassword(password)) {
				player.sendMessage("§cSenha incorreta.");
				int total = 1;
				if(tried.containsKey(player)) {
					total += tried.get(player);
				}
				if(total >= 6) {
					tried.remove(player);
					player.kick(Component.text("Pelo amor de Deus, tá com alzheimer? Vá falar com o Far ou o Lion caso tenha esquecido sua senha ou tente novamente.").color(TextColor.color(255, 0, 0)));
				} else {
					tried.put(player, total);
				}
				return false;
			}
			Auth.getInstance().playerLogged(player);
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