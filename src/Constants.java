package intficint.src;

import java.util.HashMap;

public final class Constants {
	// Colours to be printed
	public static final String ANSI_RESET = "\u001B[0m";

	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";
	public static final String ANSI_DEFAULT = "\u001B[39m";

	public static final String ANSI_BRIGHT_BLACK = "\u001B[90m";
	public static final String ANSI_BRIGHT_RED = "\u001B[91m";
	public static final String ANSI_BRIGHT_GREEN = "\u001B[92m";
	public static final String ANSI_BRIGHT_YELLOW = "\u001B[93m";
	public static final String ANSI_BRIGHT_BLUE = "\u001B[94m";
	public static final String ANSI_BRIGHT_PURPLE = "\u001B[95m";
	public static final String ANSI_BRIGHT_CYAN = "\u001B[96m";
	public static final String ANSI_BRIGHT_WHITE = "\u001B[97m";

	public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
	public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
	public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
	public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
	public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
	public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
	public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
	public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
	public static final String ANSI_DEFAULT_BACKGROUND = "\u001B[49m";

	public static final String ANSI_BRIGHT_BLACK_BACKGROUND = "\u001B[100m";
	public static final String ANSI_BRIGHT_RED_BACKGROUND = "\u001B[101m";
	public static final String ANSI_BRIGHT_GREEN_BACKGROUND = "\u001B[102m";
	public static final String ANSI_BRIGHT_YELLOW_BACKGROUND = "\u001B[103m";
	public static final String ANSI_BRIGHT_BLUE_BACKGROUND = "\u001B[104m";
	public static final String ANSI_BRIGHT_PURPLE_BACKGROUND = "\u001B[105m";
	public static final String ANSI_BRIGHT_CYAN_BACKGROUND = "\u001B[106m";
	public static final String ANSI_BRIGHT_WHITE_BACKGROUND = "\u001B[107m";

	// More ANSI special formatting
	public static final String ANSI_BOLD = "\u001B[1m";
	public static final String ANSI_DIM = "\u001B[2m";
	public static final String ANSI_BOLD_RESET = "\u001B[22m";

	public static final String ANSI_ITALIC = "\u001B[3m";
	public static final String ANSI_ITALIC_RESET = "\u001B[23m";

	public static final String ANSI_UNDERLINE = "\u001B[4m";
	public static final String ANSI_DOUBLE_UNDERLINE = "\u001B[21m";
	public static final String ANSI_UNDERLINE_RESET = "\u001B[24m";

	public static final String ANSI_BLINKING = "\u001B[5m";
	public static final String ANSI_BLINKING_RESET = "\u001B[25m";

	public static final String ANSI_INVERSE = "\u001B[7m";
	public static final String ANSI_INVERSE_RESET = "\u001B[27m";

	public static final String ANSI_HIDDEN = "\u001B[8m";
	public static final String ANSI_HIDDEN_RESET = "\u001B[28m";

	public static final String ANSI_STRIKETHROUGH = "\u001B[8m";
	public static final String ANSI_STRIKETHROUGH_RESET = "\u001B[28m";

	public static final String ANSI_CLEAR_SCREEN = "\u001B[2J";
	public static final String ANSI_HOME = "\u001B[H";

	// General ANSI escape code
	public static final String ANSI_ESC = "\u001B";

	// Null objects
	public static final Hook NULL_HOOK = new Hook("null_hook", "", new String[0], "");
	public static final MatchedHook UNMATCHED_HOOK = new MatchedHook(NULL_HOOK, new HashMap<>());
	public static final MatchedHook FULFILLED_HOOK = new MatchedHook(NULL_HOOK, new HashMap<>());

	public static final Thing NULL_THING = new Thing("null_thing", "", new String[0], "nowhere", new HashMap<>());

	public static final Room NULL_ROOM = new Room("null_room", "", new String[0], new HashMap<>(), new HashMap<>());

	public static final Exit NULL_EXIT = new Exit("null_exit", new String[0], new HashMap<>());
}
