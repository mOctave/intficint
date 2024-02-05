package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class Data {
	// A list of all the files that have been parsed, used to prevent any
	// circular dependencies or re-parsing of dependencies of multiple files.
	public static List<String> parsedFiles = new ArrayList<String>();

	// A list of all the things in play
	public static List<Thing> things = new ArrayList<Thing>();

	// A list of all the rooms in play
	public static List<Room> rooms = new ArrayList<Room>();

	// A list of all the exits being used
	public static List<Exit> exits = new ArrayList<Exit>();

	// A list of all the hooks being used
	public static List<Hook> hooks = new ArrayList<Hook>();

	// The global input scanner
	public static Scanner globalScanner;

	/* METHODCODE */

	// A dictionary of all program-defined variables
	public static HashMap<String, String> variables = new HashMap<>();

	// A stack to provide local variables
	public static Stack<HashMap<String, String>> localVars = new Stack<>();

	// A dictionary of project metadata
	public static HashMap<String, String> metadata = new HashMap<>();

	// A list of all project methodcode
	public static List<String> methodcode = new ArrayList<>();

	// A dictionary to match labels with line numbers
	public static HashMap<String, Integer> labels = new HashMap<>();

	// A dictionary to match methods with line numbers, inputs, and outputs
	public static HashMap<String, int[]> methods = new HashMap<>();

	// The GOTO and method stacks
	public static Stack<Integer> gotoStack = new Stack<>();
	public static Stack<String> methodStack = new Stack<>();

	// The current line being parsed by the interpreter
	public static int line = 0;

	// The line to go to rather then exit the program upon an error
	public static int catchPoint = -1;

	// The current command prompt
	public static String prompt = Constants.ANSI_PURPLE+"> "+Constants.ANSI_RESET;

	// Should the methodcode interpreter print non-fatal error messages?
	public static boolean printErrorMessages = true;

	// Methodcode line number reference for debug messages
	public static List<String[]> lnumRef = new ArrayList<>();

	// Attached object for methodcode
	public static String attachedObject = "";
}
