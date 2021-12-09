package br.felipstein.tardis;

import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.common.collect.Maps;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public final class Auth implements Listener {
	
	private static Auth instance;
	
	private Map<Player, Location> waitingLogin;
	
	private Auth() {
		instance = this;
		waitingLogin = Maps.newHashMap();
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.joinMessage(null);
		onPlayerJoin(e.getPlayer());
	}
	
	private void onPlayerJoin(Player player) {
		Account account = Account.getAccount(player);
		if(account == null) {
			waitingLogin.put(player, player.getLocation());
			player.teleport(Main.MAIN_WORLD.getSpawnLocation());
			player.sendMessage(" ");
			player.sendMessage("§4 » §aPara não ter perigo de outros jogadores de má fé entrar na sua conta e localizar suas coordenadas, você precisa se registrar.");
			player.sendMessage("§4 » §aUse §f/register <senha> §apara proteger sua conta. Lembre-se de salvar essa sua senha!");
			player.sendMessage("§7§oFarRed aprova...");
			player.sendMessage(" ");
			player.sendActionBar(Component.text("Você pode trocar sua senha com §e/trocarsenha <nova senha>").color(TextColor.color(255, 150, 20)));
		} else {
			if(!player.getAddress().getAddress().getHostAddress().equals(account.getLastAddress())) {
				waitingLogin.put(player, player.getLocation());
				player.teleport(Main.MAIN_WORLD.getSpawnLocation());
				player.sendMessage(" ");
				player.sendMessage("§4 » §aParece que você está tentando conectar de outra máquina ou outro endereço de IP.");
				player.sendMessage("§4 » §aSe realmente for você, §e" + player.getName() + ", §aefetua o login com a senha na qual você se registrou na primeira vez que conectou.");
				player.sendMessage("§4 » §aUse §f/login <senha> §apara validar e poder continuar.");
				player.sendMessage(" ");
			} else {
				playerPostLogged(player);
			}
		}
	}
	
	public void playerLogged(Player player) {
		playerLogged(player, false);
	}
	
	public void playerLogged(Player player, boolean registered) {
		Account account = Account.getAccount(player);
		Validate.isTrue(account != null, "O jogador " + player.getName() + " não possui uma conta registrada.");
		playerPostLogged(player);
		if(!registered) {
			account.updateAddress(player.getAddress().getAddress().getHostAddress());
			player.sendMessage("§4 » §aCertô mizeravi!");
		}
		player.teleport(waitingLogin.get(player));
		waitingLogin.remove(player);
	}
	
	public void playerPostLogged(Player player) {
		Main.getInstance().getServer().broadcast(Component.text(player.getName() + " entrou.").color(TextColor.color(102, 153, 153)));
		Main.getInstance().configureAttribute(player);
		Skorbord.applyScoreboard(player);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		if(waitingLogin.containsKey(player)) {
			player.teleport(waitingLogin.get(player));
			waitingLogin.remove(player);
		}
	}
	
	public boolean playerWaitingLogin(Player player) {
		return waitingLogin.containsKey(player);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if(waitingLogin.containsKey(e.getPlayer())) {
			Location from = e.getFrom();
			Location to = e.getTo();
			if(from.getX() != to.getX()) {
				e.setCancelled(true);
			}
			if(from.getZ() != to.getZ()) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerBreakBlock(BlockBreakEvent e) {
		if(waitingLogin.containsKey(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerPlaceBlock(BlockPlaceEvent e) {
		if(waitingLogin.containsKey(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent e) {
		if(waitingLogin.containsKey(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player player) {
			if(waitingLogin.containsKey(player)) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if(waitingLogin.containsKey(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(EntityPickupItemEvent e) {
		if(e.getEntity() instanceof Player player) {
			if(waitingLogin.containsKey(player)) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerAttack(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Player player) {
			if(waitingLogin.containsKey(player)) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(waitingLogin.containsKey(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerUseInventory(InventoryClickEvent e) {
		if(e.getWhoClicked() instanceof Player player) {
			if(waitingLogin.containsKey(player)) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerOpenInventory(InventoryOpenEvent e) {
		if(e.getPlayer() instanceof Player player) {
			if(waitingLogin.containsKey(player)) {
				e.setCancelled(true);
				player.closeInventory();
			}
		}
	}
	
	@EventHandler
	public void onPlayerProcessCommand(PlayerCommandPreprocessEvent e) {
		if(waitingLogin.containsKey(e.getPlayer())) {
			String command = e.getMessage().toLowerCase();
			if(command.startsWith("/register") || command.startsWith("/registro") || command.startsWith("/registrar") || command.startsWith("/login") || command.startsWith("/logar") || command.startsWith("/log-in")) {
				return;
			}
			e.setCancelled(true);
		}
	}
	
	public static void setupAuth(Main main) {
		Auth instance = new Auth();
		Server server = main.getServer();
		server.getPluginManager().registerEvents(instance, main);
		server.getOnlinePlayers().forEach(player -> instance.onPlayerJoin(player));
	}
	
	public static Auth getInstance() {
		return instance;
	}
	
}