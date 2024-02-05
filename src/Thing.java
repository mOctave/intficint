package src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Thing {
	// Things are the core building block of most interactive fiction. They are
	// physical objects that can exist.

	// The proper name of the thing. The player will access it by this name,
	// ignoring case sensitivity. No object should have the same name as
	// another, even if capitalized differently. Referred to in strings as «N».
	String name;

	// The description to be printed when the player looks at/examines the
	// thing. Referred to in strings as «D».
	String desc;

	// An array of aliases that can be called by the player or by other code
	// to refer to this thing. Like names, no object should have overlapping
	// aliases with another. In strings, refer individually to each aliases
	// with «A(index)».
	String[] aliases;

	// A dictionary containing key value pairs allowing for custom properties
	// to be given to things. Each value can be referred to in a string by
	// «C(key)». All keys and values are strings.
	HashMap<String, String> attributes;

	// The name of the object in which this thing is located. Can be a person,
	// room, or another thing. Referred to in strings as «L».
	String location;



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
		return contents;
	}



	Thing (
		String name,
		String desc,
		String[] aliases,
		String location,
		HashMap<String, String> attributes
	) {
		this.name = name;
		this.desc = desc;
		this.aliases = aliases;
		this.location = location;

		this.attributes = attributes;
		System.out.println("New Thing initialized ("+name+")");
	}
}



class ThingBuilder {
	private String name = "Unnamed Thing";
	private String desc = "You see nothing special about the «N».";
	private List<String> aliases = new ArrayList<String>();
	private String location = "Nowhere";
	HashMap<String, String> attributes = new HashMap<>();



	ThingBuilder setName(String name) {
		this.name = name;
		return this;
	}



	ThingBuilder setDesc(String desc) {
		this.desc = desc;
		return this;
	}



	ThingBuilder addAlias(String alias) {
		this.aliases.add(alias);
		return this;
	}



	ThingBuilder setLocation(String location) {
		this.location = location;
		return this;
	}



	ThingBuilder addAttribute(String key, String value) {
		this.attributes.put(key, value);
		return this;
	}



	Thing build() {
		return new Thing(
			name,
			desc,
			aliases.toArray(new String[0]),
			location,
			attributes
		);
	}
}
