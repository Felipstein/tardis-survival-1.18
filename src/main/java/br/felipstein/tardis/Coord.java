package br.felipstein.tardis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Coord {
	
	private UUID owner;
	
	private int id;
	private String name;
	private Location location;
	
	private List<UUID> shared;
	
	public Coord(UUID owner, int id, String name, Location location, List<UUID> shared) {
		this.owner = owner;
		this.id = id;
		this.name = name;
		this.location = location;
		this.shared = shared;
	}
	
	public UUID getOwner() {
		return owner;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public List<UUID> getShared() {
		return shared;
	}
	
	public void setLocation(Location location) {
		this.location = location;
		handleUpdate();
	}
	
	public void shareLocation(Player to) {
		shareLocation(to.getUniqueId());
	}
	
	public void shareLocation(UUID to) {
		if(shared.contains(to)) {
			return;
		}
		shared.add(to);
		handleUpdate();
	}
	
	public void unshareLocation(Player to) {
		unshareLocation(to.getUniqueId());
	}
	
	public void unshareLocation(UUID to) {
		if(!shared.contains(to)) {
			return;
		}
		shared.remove(to);
		handleUpdate();
	}
	
	public boolean isSharing(Player with) {
		return isSharing(with.getUniqueId());
	}
	
	public boolean isSharing(UUID with) {
		return shared.contains(with);
	}
	
	private void handleUpdate() {
		Main.getInstance().getSettings().updateCoord(this);
	}
	
	public static Map<UUID, List<Coord>> getCoords() {
		return Collections.unmodifiableMap(Main.getInstance().getSettings().getCoords());
	}
	
	public static List<Coord> getCoords(Player player) {
		return getCoords(player.getUniqueId());
	}
	
	public static List<Coord> getCoords(UUID owner) {
		return Collections.unmodifiableList(Main.getInstance().getSettings().getCoords(owner));
	}
	
	public static List<String> getCoordsName(Player player) {
		return getCoordsName(player.getUniqueId());
	}
	
	public static List<String> getCoordsName(UUID owner) {
		List<String> coords = new ArrayList<>();
		getCoords(owner).forEach(coord -> coords.add(coord.getName()));
		return Collections.unmodifiableList(coords);
	}
	
	public static List<Coord> getSharedsCoords(Player player) {
		return getSharedsCoords(player.getUniqueId());
	}
	
	public static List<Coord> getSharedsCoords(UUID owner) {
		List<Coord> coords = new ArrayList<>();
		getCoords().values().forEach(theCoords -> theCoords.stream().filter(coord -> !coord.owner.equals(owner) && coord.shared.contains(owner)).forEach(coord -> coords.add(coord)));
		return Collections.unmodifiableList(coords);
	}
	
	public static Coord getCoord(Player player, int id) {
		return getCoord(player.getUniqueId(), id);
	}
	
	public static Coord getCoord(Player player, String name) {
		return getCoord(player.getUniqueId(), name);
	}
	
	public static Coord getCoord(UUID owner, int id) {
		return Main.getInstance().getSettings().getCoord(owner, id);
	}
	
	public static Coord getCoord(UUID owner, String name) {
		return Main.getInstance().getSettings().getCoord(owner, name);
	}
	
	public static Coord getCoordShared(Player player, int id) {
		return getCoordShared(player.getUniqueId(), id);
	}
	
	public static Coord getCoordShared(Player player, String name) {
		return getCoordShared(player.getUniqueId(), name);
	}
	
	public static Coord getCoordShared(UUID owner, int id) {
		for(Coord coord : getSharedsCoords(owner)) {
			if(coord.id == id) {
				return coord;
			}
		}
		return null;
	}
	
	public static Coord getCoordShared(UUID owner, String name) {
		for(Coord coord : getSharedsCoords(owner)) {
			if(coord.name.equalsIgnoreCase(name)) {
				return coord;
			}
		}
		return null;
	}
	
	public static Coord addCoord(Player player, String name) {
		return addCoord(player.getUniqueId(), player.getLocation(), name);
	}
	
	public static Coord addCoord(UUID owner, Location location, String name) {
		Settings settings = Main.getInstance().getSettings();
		int freeId;
		for(freeId = 0; settings.getCoord(owner, freeId) != null; ++freeId) {}
		Coord coord = new Coord(owner, freeId, name, location, new ArrayList<>());
		settings.updateCoord(coord);
		return coord;
	}
	
	public static void removeCoord(Player player, String name) {
		removeCoord(player.getUniqueId(), name);
	}
	
	public static void removeCoord(UUID owner, String name) {
		Main.getInstance().getSettings().removeCoord(owner, name);
	}
	
	public static void removeCoord(Player player, int id) {
		removeCoord(player.getUniqueId(), id);
	}
	
	public static void removeCoord(UUID owner, int id) {
		Main.getInstance().getSettings().removeCoord(owner, id);
	}
	
}