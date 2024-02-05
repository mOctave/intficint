package src;

import java.util.Arrays;
import java.util.List;

public final class HumanReadable {
	// A series of functions to allow for the representations of various
	// objects that are normally only represented by class and memory
	// address.

	// Format a thing in a human-readable format
	public static final String represent(Thing a) {
		String repr = String.format(
			"<Thing name = \"%s\", desc = \"%s\", aliases = %s, location = %s, attributes = %s>",
			a.name,
			a.desc,
			represent(a.aliases),
			a.location,
			a.attributes
		);
		return repr;
	}



	// Format a room in a human-readable format
	public static final String represent(Room a) {
		String repr = String.format(
			"<Room name = \"%s\", desc = \"%s\", aliases = %s, exits = %s, attributes = %s>",
			a.name,
			a.desc,
			represent(a.aliases),
			a.exits,
			a.attributes
		);
		return repr;
	}



	// Format a hook in a human-readable format
	public static final String represent(Hook a) {
		String repr = String.format(
			"<Hook name = \"%s\", trigger = \"%s\", commands = %s, executable = \"%s\">",
			a.name,
			a.trigger,
			represent(a.commands),
			a.executable
		);
		return repr;
	}



	// Format the player data in a human-readable format
	public static final String representPlayer() {
		String repr = String.format(
			"<Player name = \"%s\", desc = \"%s\", aliases = %s, location = %s, attributes = %s>",
			Player.name,
			Player.desc,
			represent(Player.aliases),
			Player.location,
			Player.attributes
		);
		return repr;
	}



	// Format a string array in a human-readable format
	public static final String represent(String[] a) {
		String repr = "<String[] {";
		for (String x : a) {
			repr += String.format("\"%s\", ", x);
		}
		try {
			return repr.substring(0, repr.length() - 2) + "}>";
		} catch (StringIndexOutOfBoundsException e) {
			return "<String[] {}>";
		}
	}



	// Format an integer array in a human-readable format
	public static final String represent(int[] a) {
		String repr = "<int[] {";
		for (int x : a) {
			repr += String.format("%d, ", x);
		}
		try {
			return repr.substring(0, repr.length() - 2) + "}>";
		} catch (StringIndexOutOfBoundsException e) {
			return "<int[] {}>";
		}
	}



	// Format a list in a human-readable format
	public static final String represent(List a) {

		String repr = "[";
		for (Object x : a) {
			try {
				repr += represent((Thing) x)+", ";
			} catch (ClassCastException e1) { try {
				repr += represent((Room) x)+", ";
			} catch (ClassCastException e2) { try {
				repr += represent((Hook) x)+", ";
			} catch (ClassCastException e3) {
				repr += x + ", ";
			}}}
		}
		try {
			return repr.substring(0, repr.length() - 2) + "]";
		} catch (StringIndexOutOfBoundsException e) {
			return "[]";
		}
	}

	// Format a list in the format used by the list library
	public static final String libstyle(List a) {

		String repr = "[";
		for (Object x : a) {
			try {
				repr += "`" + represent((Thing) x) + "`,";
			} catch (ClassCastException e1) { try {
				repr += "`" + represent((Room) x) + "`,";
			} catch (ClassCastException e2) { try {
				repr += "`" + represent((Hook) x) + "`,";
			} catch (ClassCastException e3) {
				repr += "`" + x + "`,";
			}}}
		}
		try {
			return repr.substring(0, repr.length() - 2) + "`]";
		} catch (StringIndexOutOfBoundsException e) {
			return "[]";
		}
	}

	// Format a string array in the format used by the list library
	public static final String libstyle(String[] a) {
		return libstyle(Arrays.asList(a));
	}
}
