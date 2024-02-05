package intficint.src;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Main {
	private static boolean debug_mode = false;
	public static void main(String[] args) {
		List<String> argList = Arrays.asList(args);
		if (argList.contains("-d") || argList.contains("--debug"))
			debug_mode = true;
		System.out.println(
			Constants.ANSI_BLUE+"\nWelcome to mOctave's Interactive Fiction Interpreter!"+Constants.ANSI_RESET
		);
		System.out.println("Please enter the path to the file you wish to load below.");
		System.out.print(Data.prompt);
		Parser parseMain = new Parser();

		Data.globalScanner = new Scanner(System.in);

		parseMain.read(Data.globalScanner.nextLine());
		if (parseMain.filedata == null)
			System.exit(4);
		parseMain.parseFiledata();

		System.out.println(
			Constants.ANSI_BLUE+"\nFiledata successfully loaded! Let the game begin..."+Constants.ANSI_RESET
		);

		// Force null hook to initialize before game begins
		HumanReadable.represent(Constants.NULL_HOOK);

		play();
	}



	public static void play() {
		// Unless debug mode is active, open the alternate buffer on startup
		if (!debug_mode) {
			System.out.println("\u001B[?1049h\u001B[2J");
			System.out.println("\u001B[5000B");
			// Disable the alternate buffer on shutdown
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					System.out.println("\u001B[?1049l");
					System.out.println("Shutting down...");
				}});
		}
		System.out.println(Constants.ANSI_UNDERLINE+Constants.ANSI_BOLD+Data.metadata.get("name")+Constants.ANSI_RESET);
		System.out.println("An interactive fiction by "+Data.metadata.get("author"));
		System.out.println("Version "+Data.metadata.get("version"));
		System.out.println(Data.metadata.get("copyright"));
		System.out.println(Data.metadata.get("parser"));
		doGameStartEvents();
		while (true)
			nextTurn(Data.globalScanner);

	}



	public static void nextTurn(Scanner s) {
		doPreEvents();
		System.out.print(Data.prompt);
		MatchedHook cmd = InputHandler.matchCommand(s.nextLine());
		if (cmd == Constants.UNMATCHED_HOOK) {
			System.out.println(Data.variables.get("err_invalidcommand"));
		} else if (cmd != Constants.FULFILLED_HOOK) {
			InputHandler.execute(cmd, s);
		}
		doPostEvents();
	}



	public static void doGameStartEvents() {
		for (Hook h : Data.hooks) {
			if (h.trigger.toLowerCase().equals("event")) {
				if (Utils.lowerCase(Arrays.asList(h.commands)).contains("start"))
					InputHandler.execute(new MatchedHook(h, new HashMap<>()), Data.globalScanner);
			}
		}
	}



	public static void doPreEvents() {
		for (Hook h : Data.hooks) {
			if (h.trigger.toLowerCase().equals("event")) {
				if (Utils.lowerCase(Arrays.asList(h.commands)).contains("pre"))
					InputHandler.execute(new MatchedHook(h, new HashMap<>()), Data.globalScanner);
			}
		}
	}



	public static void doPostEvents() {
		for (Hook h : Data.hooks) {
			if (h.trigger.toLowerCase().equals("event")) {
				if (Utils.lowerCase(Arrays.asList(h.commands)).contains("post"))
					InputHandler.execute(new MatchedHook(h, new HashMap<>()), Data.globalScanner);
			}
		}
	}
}
