package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Player {
	// The player is (generally speaking) the narrator of any work of
	// interactive fiction. The player is the focal point for commands and
	// descriptions, and can move between rooms collecting things.

	// The proper name of the player. This is what the game engine calls the
	// player, ignoring case sensitivity. No object should have the same name as
	// another, even if capitalized differently. Referred to in strings as «N».
	public static String name = "Player";

	// The description to be printed when the player uses the "look" command
	// on themself. Referred to in strings as «D».
	public static String desc = "";

	// A list of aliases that can be called by the player or by other code
	// to refer to the player. Like names, no object should have overlapping
	// aliases with another. In strings, refer individually to each aliases
	// with «A(index)».
	// Unlike other objects, the player has aliases stored in a list, as it has
	// no corresponding builder class.
	public static List<String> aliases = new ArrayList<String>();

	// The name of the room in which the player is located. Unlike things, the
	// player cannot be contained within a thing but only a room. Referred to
	// in strings as «L».
	public static String location = "Nowhere";

	// A dictionary containing key value pairs allowing for custom properties
	// to be given to the player. Each value can be referred to in a string by
	// «C(key)». All keys and values are strings.
	public static HashMap<String, String> attributes = new HashMap<>();



	// A method to return the player's inventory in list format
	public static List<String> contents() {
		List <String> contents = new ArrayList<>();
		for (Thing t : Data.things) {
			if (t.location.toLowerCase().equals(name.toLowerCase()) || Utils.lowerCase(aliases).contains(t.location))
				contents.add(t.name);
		}
		return contents;
	}


	// Methods to set the player's data
	public static void setName(String x) {
		name = x;
	}



	public static void setDesc(String x) {
		desc = x;
	}



	public static void addAlias(String x) {
		aliases.add(x);
	}



	public static void setLocation(String x) {
		location = x;
	}



	public static void addAttribute(String key, String value) {
		attributes.put(key, value);
	}
}
