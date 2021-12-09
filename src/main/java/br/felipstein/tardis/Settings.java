package br.felipstein.tardis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.collect.Maps;

public final class Settings {
	
	private Main main;
	private FileConfiguration config;
	
	public Settings(Main main, File fileConfig, FileConfiguration config) {
		if(!fileConfig.exists()) {
			main.saveDefaultConfig();
			config.options().copyDefaults(true);
		}
		this.main = main;
		this.config = config;
	}
	
	public boolean showHealth() {
		return config.getBoolean("exibir_vida");
	}
	
	public boolean teamsHabilited() {
		return config.getBoolean("equipes.habilitado");
	}
	
	public List<String> playersNameInA() {
		return config.getStringList("equipes.equipe_A.jogadores");
	}
	
	public List<String> playersNameInB() {
		return config.getStringList("equipes.equipe_B.jogadores");
	}
	
	public List<String> coordsOfA() {
		return config.getStringList("equipes.equipe_A.coords");
	}
	
	public List<String> coordsOfB() {
		return config.getStringList("equipes.equipe_B.coords");
	}
	
	public boolean hasPlayerNameInA(String name) {
		return config.getStringList("equipes.equipe_A.jogadores").contains(name.toLowerCase());
	}
	
	public boolean hasPlayerNameInB(String name) {
		return config.getStringList("equipes.equipe_B.jogadores").contains(name.toLowerCase());
	}
	
	public void addPlayerNameInA(String name) {
		if(hasPlayerNameInB(name)) {
			throw new IllegalArgumentException("Não é possível colocar o jogador " + name + " na equipe A: já está presente na equipe B.");
		}
		List<String> names = playersNameInA();
		if(names.contains(name.toLowerCase())) {
			return;
		}
		names.add(name.toLowerCase());
		config.set("equipes.equipe_A.jogadores", names);
		main.saveConfig();
	}
	
	public void addPlayerNameInB(String name) {
		if(hasPlayerNameInA(name)) {
			throw new IllegalArgumentException("Não é possível colocar o jogador " + name + " na equipe B: já está presente na equipe A.");
		}
		List<String> names = playersNameInB();
		if(names.contains(name.toLowerCase())) {
			return;
		}
		names.add(name.toLowerCase());
		config.set("equipes.equipe_B.jogadores", names);
		main.saveConfig();
	}
	
	public void removePlayerNameOfA(String name) {
		List<String> names = playersNameInA();
		if(!names.contains(name.toLowerCase())) {
			return;
		}
		names.remove(name.toLowerCase());
		config.set("equipes.equipe_A.jogadores", names);
		main.saveConfig();
	}
	
	public void removePlayerNameOfB(String name) {
		List<String> names = playersNameInB();
		if(!names.contains(name.toLowerCase())) {
			return;
		}
		names.remove(name.toLowerCase());
		config.set("equipes.equipe_B.jogadores", names);
		main.saveConfig();
	}
	
	public void addCoordToA(String coord) {
		List<String> coords = coordsOfA();
		if(coords.contains(coord)) {
			return;
		}
		coords.add(coord);
		config.set("equipes.equipe_A.coords", coords);
		main.saveConfig();
	}
	
	public void addCoordToB(String coord) {
		List<String> coords = coordsOfB();
		if(coords.contains(coord)) {
			return;
		}
		coords.add(coord);
		config.set("equipes.equipe_B.coords", coords);
		main.saveConfig();
	}
	
	public void removeCoordOfA(String coord) {
		List<String> coords = coordsOfA();
		if(!coords.contains(coord)) {
			return;
		}
		coords.remove(coord);
		config.set("equipes.equipe_A.coords", coords);
		main.saveConfig();
	}
	
	public void removeCoordOfB(String coord) {
		List<String> coords = coordsOfB();
		if(!coords.contains(coord)) {
			return;
		}
		coords.remove(coord);
		config.set("equipes.equipe_B.coords", coords);
		main.saveConfig();
	}
	
	public List<Account> getAccountsRegistred() {
		List<Account> accounts = new ArrayList<>();
		for(String uuidInString : config.getConfigurationSection("registros").getKeys(false)) {
			UUID uniqueId = UUID.fromString(uuidInString);
			String path = "registros." + uuidInString + ".";
			String passwordEncrypted = config.getString(path + "password_encrypted");
			String lastAddress = config.getString(path + "last_address");
			accounts.add(new Account(uniqueId, passwordEncrypted, lastAddress));
		}
		return accounts;
	}
	
	public void updateAccount(Account account) {
		String path = "registros." + account.getUniqueId().toString() + ".";
		config.set(path + "password_encrypted", account.getPasswordEncrypted());
		config.set(path + "last_address", account.getLastAddress());
		main.saveConfig();
		main.getServer().getConsoleSender().sendMessage("Registro " + account.getUniqueId() + " atualizado.");
	}
	
	public void unregisterAccount(UUID uniqueId) {
		config.set("registros." + uniqueId.toString(), null);
		main.saveConfig();
		main.getServer().getConsoleSender().sendMessage("Registro " + uniqueId + " removido.");
	}
	
	public Map<UUID, List<Coord>> getCoords() {
		Map<UUID, List<Coord>> coords = Maps.newHashMap();
		for(String uuidInString : config.getConfigurationSection("coords").getKeys(false)) {
			UUID uniqueId = UUID.fromString(uuidInString);
			List<Coord> theCoords = new ArrayList<>();
			for(String idStr : config.getConfigurationSection("coords." + uuidInString).getKeys(false)) {
				int id = Integer.parseInt(idStr.split("_")[1]);
				String path = "coords." + uuidInString + "." + idStr + ".";
				String name = config.getString(path + "name");
				List<UUID> shared = new ArrayList<>();
				config.getStringList(path + "shared").forEach(uuidInString2 -> shared.add(UUID.fromString(uuidInString2)));
				theCoords.add(new Coord(uniqueId, id, name, getLocation(path + "location"), shared));
			}
			coords.put(uniqueId, theCoords);
		}
		return coords;
	}
	
	public List<Coord> getCoords(UUID owner) {
		String path = "coords." + owner.toString();
		if(!config.contains(path)) {
			return new ArrayList<>();
		}
		List<Coord> coords = new ArrayList<>();
		for(String idStr : config.getConfigurationSection("coords." + owner.toString()).getKeys(false)) {
			int id = Integer.parseInt(idStr.split("_")[1]);
			path = "coords." + owner.toString() + "." + idStr + ".";
			String name = config.getString(path + "name");
			List<UUID> shared = new ArrayList<>();
			config.getStringList(path + "shared").forEach(uuidInString2 -> shared.add(UUID.fromString(uuidInString2)));
			coords.add(new Coord(owner, id, name, getLocation(path + "location"), shared));
		}
		return coords;
	}
	
	public Coord getCoord(UUID owner, int id) {
		String path = "coords." + owner.toString() + ".id_" + id;
		if(config.contains(path)) {
			path += ".";
			String name = config.getString(path + "name");
			List<UUID> shared = new ArrayList<>();
			config.getStringList(path + "shared").forEach(uuidInString -> shared.add(UUID.fromString(uuidInString)));
			return new Coord(owner, id, name, getLocation(path + "location"), shared);
		}
		return null;
	}
	
	public Coord getCoord(UUID owner, String name) {
		String path = "coords." + owner.toString();
		if(config.contains(path)) {
			for(String idStr : config.getConfigurationSection(path).getKeys(false)) {
				if(name.equalsIgnoreCase(config.getString(path + "." + idStr + ".name"))) {
					return getCoord(owner, Integer.parseInt(idStr.split("_")[1]));
				}
			}
		}
		return null;
	}
	
	public void updateCoord(Coord coord) {
		String path = "coords." + coord.getOwner().toString() + ".id_" + coord.getId() + ".";
		config.set(path + "name", coord.getName());
		setLocation(path + "location", coord.getLocation());
		List<String> uuids = new ArrayList<>();
		coord.getShared().forEach(uniqueId -> uuids.add(uniqueId.toString()));
		config.set(path + "shared", uuids);
		main.saveConfig();
	}
	
	public void removeCoord(UUID owner, int id) {
		config.set("coords." + owner.toString() + ".id_" + id, null);
		main.saveConfig();
	}
	
	public void removeCoord(UUID owner, String name) {
		Coord coord = getCoord(owner, name);
		if(coord == null) {
			return;
		}
		removeCoord(owner, coord.getId());
		main.saveConfig();
	}
	
	private Location getLocation(String path) {
		String inString[] = config.getString(path).split(",");
		boolean hasWorld = inString.length > 3;
		World world = hasWorld ? Bukkit.getWorld(inString[0]) : null;
		double x = Double.parseDouble(inString[hasWorld ? 1 : 0]);
		double y = Double.parseDouble(inString[hasWorld ? 2 : 1]);
		double z = Double.parseDouble(inString[hasWorld ? 3 : 2]);
		return new Location(world, x, y, z);
	}
	
	private void setLocation(String path, Location location) {
		World world = location.getWorld();
		config.set(path, world == null ? "null" : world.getName() + "," + location.getX() + "," + location.getY() + "," + location.getZ());
	}
	
}