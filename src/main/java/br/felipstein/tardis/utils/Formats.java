package br.felipstein.tardis.utils;

import org.bukkit.Location;
import org.bukkit.World;

public final class Formats {
	
	private Formats() {}
	
	public static String locationToString(Location location) {
		World world = location.getWorld();
		String coords = "x: " + location.getBlockX() + ", y: " + location.getBlockY() + ", z: " + location.getBlockZ();
		if(world == null) {
			return coords;
		} else {
			return "mundo: " + world.getName() + ", " + coords;
		}
	}
	
	public static String locationToSimpleString(Location location) {
		World world = location.getWorld();
		String coords = location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ();
		if(world == null) {
			return coords;
		} else {
			return world.getName() + ", " + coords;
		}
	}
	
}