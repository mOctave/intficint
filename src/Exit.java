package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Exit {
	// Exits link two rooms together. At its simplest, an exit is simply a name
	// and a list of aliases, but they can also be turned into cloeable and
	// lockable doors

	// The proper name of this exit. The player will refer to it by this name,
	// as will other code. This exit should have a different name from every
	// other object.
	String name = "Unnamed Exit";

	// An array of aliases that can be called by the player or by other code
	// to refer to this exit. Like names, no object should have overlapping
	// aliases with another. In strings, refer individually to each alias
	// with «A(index)».
	String[] aliases;

	// A dictionary containing key value pairs allowing for custom properties
	// to be given to exits. Each value can be referred to in a string by
	// «C(key)». All keys and values are strings.
	HashMap<String, String> attributes;



	Exit (
		String name,
		String[] aliases,
		HashMap<String, String> attributes
	) {
		this.name = name;
		this.aliases = aliases;
		this.attributes = attributes;
	}
}



class ExitBuilder {
	private String name = "Unnamed Exit";
	private List<String> aliases = new ArrayList<>();
	private HashMap<String, String> attributes;



	ExitBuilder setName(String name) {
		this.name = name;
		return this;
	}



	ExitBuilder addAlias(String alias) {
		this.aliases.add(alias);
		return this;
	}



	ExitBuilder addAttribute(String key, String value) {
		this.attributes.put(key, value);
		return this;
	}



	Exit build() {
		return new Exit (
			this.name,
			this.aliases.toArray(new String[0]),
			this.attributes
		);
	}
}
