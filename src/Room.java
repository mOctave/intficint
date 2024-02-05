package src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Room {
	// Rooms are where all the action in an interactive fiction game takes
	// place. They can hold both things and the player, and are connected by
	// exits.

	// The proper name of the thing. The player will access it by this name,
	// ignoring case sensitivity. No object should have the same name as
	// another, even if capitalized differently. Referred to in strings as «N».
	String name;

	// The description to be printed when the player uses the "look" command
	// while in this room. Referred to in strings as «D».
	String desc;

	// An array of aliases that can be called by the player or by other code
	// to refer to this room. Like names, no object should have overlapping
	// aliases with another. In strings, refer individually to each aliases
	// with «A(index)».
	String[] aliases;

	// An dictionary containing exit names and destinations. Each destination
	// can be referred to individually in strings by «X(exit name)».
	HashMap<String, String> exits;

	// A dictionary containing key value pairs allowing for custom properties
	// to be given to rooms. Each value can be referred to in a string by
	// «C(key)». All keys and values are strings.
	HashMap<String, String> attributes;



	// A method to return the contents of this object in list format
	public List<String> contents() {
		List <String> contents = new ArrayList<>();
		for (Thing t : Data.things) {
			if (
				t.location.toLowerCase().equals(name.toLowerCase()) ||
				Utils.lowerCase(Arrays.asList(aliases)).contains(t.location)
			)
				contents.add(t.name);
		}
		if (
			Player.location.toLowerCase().equals(name.toLowerCase()) ||
			Utils.lowerCase(Arrays.asList(aliases)).contains(Player.location)
		)
			contents.add(Player.name);
		return contents;
	}



	Room (
		String name,
		String desc,
		String[] aliases,
		HashMap<String, String> exits,
		HashMap<String, String> attributes
	) {
		this.name = name;
		this.desc = desc;
		this.aliases = aliases;
		this.exits = exits;

		this.attributes = attributes;
		System.out.println("New Room initialized (" + name + ")");
	}
}



class RoomBuilder {
	private String name = "Unnamed Room";
	private String desc = "This is a nondescript room.";
	private List<String> aliases = new ArrayList<String>();
	private HashMap<String, String> exits = new HashMap<>();
	HashMap<String, String> attributes = new HashMap<>();



	RoomBuilder setName(String name) {
		this.name = name;
		return this;
	}



	RoomBuilder setDesc(String desc) {
		this.desc = desc;
		return this;
	}



	RoomBuilder addAlias(String alias) {
		this.aliases.add(alias);
		return this;
	}



	RoomBuilder addExit(String exit, String destination) {
		this.exits.put(exit, destination);
		return this;
	}



	RoomBuilder addAttribute(String key, String value) {
		this.attributes.put(key, value);
		return this;
	}



	Room build() {
		return new Room(
			name,
			desc,
			aliases.toArray(new String[0]),
			exits,
			attributes
		);
	}
}
