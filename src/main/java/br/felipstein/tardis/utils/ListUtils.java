package br.felipstein.tardis.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ListUtils {
	
	private ListUtils() {}
	
	public static List<String> getElementsStartingWith(String startWith, List<String> elements) {
		return getElementsStartingWith(startWith, false, elements);
	}
	
	public static List<String> getElementsStartingWith(String startWith, Set<String> elements) {
		return getElementsStartingWith(startWith, false, new ArrayList<String>(elements));
	}
	
	public static List<String> getElementsStartingWith(String startWith, Collection<String> elements) {
		return getElementsStartingWith(startWith, false, new ArrayList<String>(elements));
	}
	
	public static List<String> getElementsStartingWith(String startWith, boolean ignoreCase, Set<String> elements) {
		return getElementsStartingWith(startWith, ignoreCase, new ArrayList<String>(elements));
	}
	
	public static List<String> getElementsStartingWith(String startWith, boolean ignoreCase, Collection<String> elements) {
		return getElementsStartingWith(startWith, ignoreCase, new ArrayList<String>(elements));
	}
	
	public static List<String> getElementsStartingWith(String startWith, boolean ignoreCase, boolean ignoreKeys, Collection<String> elements) {
		ArrayList<String> obj = new ArrayList<>();
		if(ignoreKeys) {
			for(String element : elements) {
				obj.add(element.replace("<", "").replace("[", "").replace("{", "").replace("}", "").replace("]", "").replace(">", ""));
			}
		} else {
			obj = new ArrayList<>(elements);
		}
		return getElementsStartingWith(startWith, ignoreCase, obj);
	}
	
	public static List<String> getElementsStartingWith(String startWith, boolean ignoreCase, List<String> elements) {
		return new ArrayList<String>(elements.stream().filter(x -> ignoreCase ? x.toLowerCase().startsWith(startWith.toLowerCase()) : x.startsWith(startWith)).collect(Collectors.toList()));
	}
	
}