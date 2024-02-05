package intficint.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Hook {
	// Hooks are what allow the player to trigger methods. They allow events to
	// progress in the game, and allow for complex and configurable games.
	// Hooks cannot be modified by the game code.

	// The proper name of the hook, which distinguishes it from other hooks
	String name;

	// The type of hook (game event or command)
	String trigger;

	// An array of commands that are used to trigger this hook. Commands are
	// written in the form that they are typed, with variables being defined in
	// phrases such as «a».
	String[] commands;

	// Method to execute. This phrase is converted into an EXECUTE statement.
	String executable;



	Hook (
		String name,
		String trigger,
		String[] commands,
		String executable
	) {
		this.name = name;
		this.trigger = trigger;
		this.commands = commands;
		this.executable = executable;

		System.out.println("New Hook initialized ("+name+")");
	}
}



class HookBuilder {
	private String name = "";
	private String trigger = "cmd";
	private List<String> commands = new ArrayList<String>();
	private String executable = "";



	HookBuilder setName(String name) {
		this.name = name;
		return this;
	}



	HookBuilder setTrigger(String trigger) {
		this.trigger = trigger;
		return this;
	}



	HookBuilder addCommand(String command) {
		this.commands.add(command);
		return this;
	}



	HookBuilder setExecutable(String executable) {
		this.executable = executable;
		return this;
	}



	Hook build() {
		return new Hook(
			name,
			trigger,
			commands.toArray(new String[0]),
			executable
		);
	}
}



class MatchedHook {
	// A class that holds a hook and all the inputs to it.

	Hook hook;

	HashMap <String, String> args;



	MatchedHook(
		Hook hook,
		HashMap <String, String> args
	) {
		this.hook = hook;
		this.args = args;
	}
}
