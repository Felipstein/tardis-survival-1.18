package br.felipstein.tardis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Maps;

import br.felipstein.tardis.commands.*;
import br.felipstein.tardis.commands.auth.*;
import br.felipstein.tardis.commands.coords.*;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Main extends JavaPlugin implements Listener {
	
	public static World MAIN_WORLD;
	
	private static Main instance;
	
	private Settings settings;
	private Map<Player, Integer> talkingAlone;
	
	@Override
	public void onEnable() {
		instance = this;
		settings = new Settings(this, new File(getDataFolder(), "config.yml"), getConfig());
		MAIN_WORLD = Bukkit.getWorld("world");
		registerCommands();
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getOnlinePlayers().forEach(player -> configureAttribute(player));
		Skorbord.setupScoreboard(this);
		talkingAlone = Maps.newHashMap();
		Auth.setupAuth(this);
	}
	
	private void registerCommands() {
		registerCommand("changepassword", new ChangePasswordCommand());
		registerCommand("login", new LoginCommand());
		registerCommand("register", new RegisterCommand());
		registerCommand("sharecoord", new ShareCoordCommand());
		registerCommand("listcoords", new ListCoordsCommand());
		registerCommand("removecoord", new RemoveCoordCommand());
		registerCommand("addcoord", new AddCoordCommand());
		registerCommand("infocoord", new InfoCoordCommand());
		registerCommand("global", new GlobalCommand());
		registerCommand("r", new RCommand());
		registerCommand("reloadconfig", new ReloadConfigCommand());
		registerCommand("team", new TeamCommand());
		registerCommand("tell", new TellCommand());
	}
	
	private void registerCommand(String syntax, Object command) {
		PluginCommand cmd = getCommand(syntax);
		if(command instanceof CommandExecutor) {
			cmd.setExecutor((CommandExecutor) command);
		}
		if(command instanceof TabCompleter) {
			cmd.setTabCompleter((TabCompleter) command);
		}
	}
	
	public void sendOperatorMessage(Component message) {
		getServer().getOnlinePlayers().stream().filter(player -> player.isOp()).forEach(player -> player.sendMessage(message));
		getServer().getConsoleSender().sendMessage(message);
	}
	
	@EventHandler
	public void onPreLogin(AsyncPlayerPreLoginEvent e) {
		String name = e.getName();
		if(Bukkit.getPlayer(name) != null) {
			e.disallow(Result.KICK_OTHER, Component.text("O nickname \"" + name + "\" já se encontra on-line."));
			sendOperatorMessage(MiniMessage.get().parse("<red>O usuário <dark_red>" + name + " <red>já está on-line, porém uma tentativa de conectar com o mesmo nickname foi realizada sem êxito. O responsável possuí <#ff0000>" + e.getAddress().getHostAddress() + " <red>como endereço de IP."));
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		e.quitMessage(Component.text(player.getName() + " saiu.").color(TextColor.color(102, 153, 153)));
		if(talkingAlone.containsKey(player)) {
			talkingAlone.remove(player);
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		configureAttribute(e.getPlayer());
	}
	
	@EventHandler
	public void onSendLocalMessage(AsyncChatEvent e) {
		Player player = e.getPlayer();
		String name = player.getName();
		Component message = e.message();
		ConsoleCommandSender console = getServer().getConsoleSender();
		LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();
		if(settings.teamsHabilited()) {
			if(settings.hasPlayerNameInA(name)) {
				getServer().getOnlinePlayers().stream().filter(target -> settings.hasPlayerNameInA(target.getName())).forEach(target -> target.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("§f<§a" + name + "§f> ").append(message)));
				console.sendMessage(serializer.deserialize("§7[§eLOCAL§7] §6[A] §f<§7" + name + "§f> ").append(message));
			} else if(settings.hasPlayerNameInB(name)) {
				getServer().getOnlinePlayers().stream().filter(target -> settings.hasPlayerNameInB(target.getName())).forEach(target -> target.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("§f<§a" + name + "§f> ").append(message)));
				console.sendMessage(serializer.deserialize("§7[§eLOCAL§7] §6[B] §f<§7" + name + "§f> ").append(message));
			} else {
				GlobalCommand.sendGlobalMessage(player, e.message());
			}
		} else {
			List<Player> nearby = getServer().getOnlinePlayers().stream().filter(target -> player.getWorld().getName().equals(target.getWorld().getName()) && player.getLocation().distance(target.getLocation()) < 200d).collect(Collectors.toList());
			if(nearby.size() <= 1) {
				player.sendMessage(serializer.deserialize("§f<§7" + name + "§f> ").append(message));
				int total = 1;
				if(talkingAlone.containsKey(player)) {
					total += talkingAlone.get(player);
				}
				if(total == 4) {
					player.sendMessage(Component.text("Put* que p*riu! Eu já te falei que ninguém está próximo de você para ver suas mensagens, tu é esquizofrênico car*lho?").color(TextColor.color(102, 153, 153)));
				} else if(total == 6) {
					player.sendMessage(Component.text("O que eu já falei para você? Tá carente seu desgr*çado?").color(TextColor.color(102, 153, 153)));
				} else if(total == 8) {
					player.sendMessage(Component.text("Ok... Ok... Eu desisto de você...").color(TextColor.color(102, 153, 153)));
				} else if(total >= 10) {
					player.sendMessage(Component.text("...").color(TextColor.color(102, 153, 153)));
				} else if(total > 17) {
					player.sendMessage(Component.text("...").color(TextColor.color(102, 153, 153)));
					talkingAlone.remove(player);
					console.sendMessage(serializer.deserialize("§7[§eLOCAL§7] §f<§7" + name + "§f> ").append(message));
					e.setCancelled(true);
					return;
				} else {
					player.sendMessage(Component.text("Não tem ninguém próximo de você para ver suas mensagens.").color(TextColor.color(102, 153, 153)));
				}
				talkingAlone.put(player, total);
			} else {
				nearby.forEach(target -> target.sendMessage(serializer.deserialize("§f<§7" + name + "§f> ").append(message)));
				if(talkingAlone.containsKey(player)) {
					talkingAlone.remove(player);
				}
			}
			console.sendMessage(serializer.deserialize("§7[§eLOCAL§7] §f<§7" + name + "§f> ").append(message));
		}
		e.setCancelled(true);
	}
	
	public void configureAttribute(Player player) {
		AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
		double old = attribute.getBaseValue();
		attribute.setBaseValue(24);
		player.saveData();
		sendOperatorMessage(Component.text("Atributo para velocidade de ataque de " + player.getName() + " alterado de " + (int) old + " para 24.").color(TextColor.color(102, 153, 153)));
	}
	
	public List<String> getOnlinePlayersName() {
		List<String> names = new ArrayList<>();
		getServer().getOnlinePlayers().forEach(player -> names.add(player.getName()));
		return names;
	}
	
	public Settings getSettings() {
		return settings;
	}
	
	public static Main getInstance() {
		return instance;
	}
	
}