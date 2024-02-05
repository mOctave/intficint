package intficint.src;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Parser {
	String filepath;
	List<String> filedata;
	boolean parseFailed;
	final File thisFile = new File(Parser.class.getProtectionDomain().getCodeSource().getLocation().getPath());
	final String thisLoc = thisFile.getParent().replaceAll("%20", " ");
	String[] fileLocations = {"", thisLoc + "/", thisLoc + "/sumatra/", ""};
	int parseLayer = 0;

	final int READ_OK = 0x0;
	final int NO_FILE = 0x1;
	final int DUPLICATE = 0x2;



	int read(String fp) {
		Pattern extractDir = Pattern.compile("^(.*)\\/([^\\/]+)$");

		filedata = new ArrayList<String>();

		for (String loc : fileLocations) {
			try {
				filepath = loc + fp;
				File f = new File(filepath);
				filepath = getFilePath(f);
				Scanner s = new Scanner(f);

				if (Data.parsedFiles.contains(filepath)) {
					ThrowParserWarning(0, "file already parsed");
					s.close();
					return DUPLICATE;
				}
				Data.parsedFiles.add(filepath);

				while (s.hasNextLine()) {
					filedata.add(s.nextLine().trim()); // Trim whitespace on all lines, and add result to the filedata
				}
				s.close();
				PrintParserMessage("Source successfully found at " + filepath);
				Matcher m = extractDir.matcher(filepath);
				if (m.find()) {
					//PrintParserMessage("New root directory for subparser: "+m.group(1));
					Array.set(fileLocations, 0, m.group(1) + "/");
				}
				return READ_OK;
			} catch (FileNotFoundException e) {
				//PrintParserMessage("No source was found at "+filepath);
				//e.printStackTrace();
			}
		}
		PrintParserMessage("No source was found at any filepath for " + fp);
		return NO_FILE;
	}



	String getFilePath(File f) {
		String path = f.getAbsolutePath();
		Pattern p = Pattern.compile("(?<=\\/)[^\\/\\.]+/\\.\\.\\/");
		Matcher m = p.matcher(path);
		int i = 0;
		while (m.find()) {
			path = path.replace(m.group(i), "");
			i ++;
		}
		System.out.println(path);
		return path;
	}



	void parseFiledata() {
		String s = "";

		// Decide on which interpreter/version to use
		try {
			s = filedata.get(0);
		} catch (IndexOutOfBoundsException e) {
			System.out.println(Constants.ANSI_RED + "Fatal IF compiler error: no file to parse");
			e.printStackTrace();
			System.out.println(Constants.ANSI_RESET);
			System.exit(2);
		}

		if (s.contains("#"))
			s = s.split("#")[0];
		s = s.trim().toLowerCase();

		if (s.equals("sumatra 1")) {
			parseSumatra1();
		} else {
			ThrowParserError(0, "invalid interpreter", true);
		}
	}



	void parseSumatra1() {
		// Declare variables
		String s, sl;
		char mode = 'm';
		ThingBuilder newThing = new ThingBuilder();
		RoomBuilder newRoom = new RoomBuilder();
		ExitBuilder newExit = new ExitBuilder();
		HookBuilder newHook = new HookBuilder();
		Pattern pattern = Pattern.compile("(.+):(.+)");

		parseFailed = false;

		PrintParserMessage(
			Constants.ANSI_BLUE + "\nParsing " + filepath + " using Sumatra 1"+Constants.ANSI_RESET + "\n"
		);

		// Loop through all lines in the filedata
		for (int i = 0; i < filedata.size(); i ++) {
			// Get and store all code on a line
			s = filedata.get(i);
			if (s.contains("#"))
				s = s.split("#")[0];
			sl = s.toLowerCase();

			if (mode == 'm') {
				// Check for known blocks to start
				if (s.startsWith("--")) {
					if (sl.startsWith("--extends--"))
						mode = '+';
					else if (sl.startsWith("--config--"))
						mode = 'c';
					else if (sl.startsWith("--room--"))
						mode = 'r';
					else if (sl.startsWith("--exit--"))
						mode = 'x';
					else if (sl.startsWith("--player--"))
						mode = 'p';
					else if (sl.startsWith("--methodcode--"))
						mode = '*';
					else if (sl.startsWith("--hook--"))
						mode = 'h';
					else if (sl.startsWith("--global--"))
						mode = 'g';
					else if (sl.startsWith("--thing--")) {
						mode = 't';
						newThing = new ThingBuilder();
					} else {
						ThrowParserWarning(i,"unknown block type");
					}
				}
			} else if (!s.equals("")) {
				// Parsing for non-empty lines
				if (sl.startsWith("--end--")) {
					// Manage end statements, building any new objects
					if (mode == 't') {
						Data.things.add(newThing.build());
						newThing = new ThingBuilder();
					} else if (mode == 'r') {
						Data.rooms.add(newRoom.build());
						newRoom = new RoomBuilder();
					} else if (mode == 'x') {
						Data.exits.add(newExit.build());
						newExit = new ExitBuilder();
					} else if (mode == 'h') {
						Data.hooks.add(newHook.build());
						newHook = new HookBuilder();
					}
					mode = 'm';
				} else if (sl.startsWith("--")) {
					// Don't parse empty blocks
					ThrowParserWarning(i,"nested blocks");
				} else if (mode == '*') {
					// Add methodcode to the list, and record any labels or methods
					Data.methodcode.add(s.trim());
					String[] thisLnumRef = {filepath.replaceAll("^.*[\\/\\\\]", ""), Integer.toString(i)};
					Data.lnumRef.add(thisLnumRef);

					if (sl.startsWith("label "))
						Data.labels.put(sl.substring(5).trim(), Data.methodcode.size() - 1);

					if (sl.startsWith("method "))
						Data.methods.put(
							Array.get(sl.substring(5).split(" "), 1).toString(),
							new int[] {
								Data.methodcode.size() - 1,
								Integer.parseInt(Array.get(sl.substring(5).split(" "), 2).toString()),
								Integer.parseInt(Array.get(sl.substring(5).split(" "), 3).toString())
							}
						);

				} else if (mode == '+') {
					// Parse an extended file
					Parser subparser = new Parser();
					subparser.fileLocations = fileLocations;
					subparser.parseLayer = parseLayer + 1;
					int status = subparser.read(sl.trim());

					if (status == NO_FILE) {
						ThrowParserWarning(i, "extended file not found");
					} else if (status == READ_OK) {
						subparser.parseFiledata();
					}
				} else {
					// Split string into parts "key" and "attribute"
					Matcher m = pattern.matcher(s);
					if (m.find()) {
						// Use split instead of the matcher to allow for colons in attributes
						String[] sections = s.split(":");
						String key = (String) Array.get(sections,0);
						key = key.toLowerCase().trim();
						String attr = "";
						for (int word = 1; word < sections.length; word++)
							attr += (String) Array.get(sections, word) + ":";
						if (attr.length() > 1)
							attr = attr.substring(0, attr.length()-1);
						attr = attr.trim();

						// Parse thing elements
						if (mode == 't') {
							if (key.equals("name"))
								newThing.setName(attr);
							else if (key.equals("desc"))
								newThing.setDesc(attr);
							else if (key.equals("alias"))
								newThing.addAlias(attr);
							else if (key.equals("location"))
								newThing.setLocation(attr);
							else
								newThing.addAttribute(key, attr);
						}
						// Parse room elements
						else if (mode == 'r') {
							if (key.equals("name"))
								newRoom.setName(attr);
							else if (key.equals("desc"))
								newRoom.setDesc(attr);
							else if (key.equals("alias"))
								newRoom.addAlias(attr);
							else if (key.startsWith("->"))
								newRoom.addExit(key.substring(2).trim(), attr);
							else
								newRoom.addAttribute(key, attr);
						}
						// Parse player elements
						else if (mode == 'p') {
							if (key.equals("name"))
								Player.setName(attr);
							else if (key.equals("desc"))
								Player.setDesc(attr);
							else if (key.equals("alias"))
								Player.addAlias(attr);
							else if (key.startsWith("location"))
								Player.setLocation(attr);
							else
								Player.addAttribute(key, attr);
						}
						// Parse exit elements
						if (mode == 'x') {
							if (key.equals("name"))
								newExit.setName(attr);
							else if (key.equals("alias"))
								newExit.addAlias(attr);
							else
								newExit.addAttribute(key, attr);
						}
						// Parse config elements
						else if (mode == 'c') {
							if (key.equals("name"))
								Data.metadata.put("name", attr);
							else if (key.equals("author"))
								Data.metadata.put("author", attr);
							else if (key.equals("version"))
								Data.metadata.put("version", attr);
							else if (key.startsWith("copyright"))
								Data.metadata.put("copyright", attr);
							else if (key.startsWith("printerrormessages"))
								Data.printErrorMessages = attr.toLowerCase().equals("true");
						}
						// Parse hook elements
						else if (mode == 'h') {
							if (key.equals("name"))
								newHook.setName(attr);
							else if (key.equals("trigger"))
								newHook.setTrigger(attr.toLowerCase());
							else if (key.equals("command"))
								newHook.addCommand(attr.toLowerCase());
							else if (key.equals("execute"))
								newHook.setExecutable(attr);
						}
						// Add global variables
						if (mode == 'g') {
							Data.variables.put(key, attr);
						}

					} else {
						ThrowParserWarning(i,"no attribute to match key");
					}

				}
			}
		}
		Data.metadata.put("parser", "This game was parsed and written using mOctave's Sumatra 1 interpreter.");
		if (parseFailed) {
			PrintParserMessage(Constants.ANSI_RED+"\nFile parsing failed for "+filepath+Constants.ANSI_RESET+"\n");
			System.exit(2);
		} else {
			PrintParserMessage(Constants.ANSI_GREEN+"\nFile parsing complete for "+filepath+Constants.ANSI_RESET+"\n");
		}
	}



	void ThrowParserWarning(int line, String desc) {
		String a = "";
		for (int i = 0; i < parseLayer; i++) {
			a += "|   ";
		}
		System.out.printf(
			Constants.ANSI_YELLOW+a+"Warning on line %d of %s: %s.%n", line+1, filepath, desc.replaceAll("\n", "\n"+a)
		);
		try {
			if (line > 1)
				System.out.println(a + (line-1) + "\t" + filedata.get(line-2));
			if (line > 0)
				System.out.println(a + line + "\t" + filedata.get(line-1));
			System.out.println(a + (line+1) + "\t" + filedata.get(line) + Constants.ANSI_RESET + "\n" + a);
		} catch (IndexOutOfBoundsException e) {
			System.out.println(Constants.ANSI_RESET + "\n");
		}
	}



	void ThrowParserError(int line, String desc, boolean immediatelyFatal) {
		String a = "";
		for (int i = 0; i < parseLayer; i++) {
			a += "|   ";
		}
		System.out.printf(
			Constants.ANSI_RED+a+"Error on line %d of %s: %s.%n", line+1, filepath, desc.replaceAll("\n", "\n"+a)
		);
		try {
			if (line > 1)
				System.out.println(a + (line-1) + "\t" + filedata.get(line-2));
			if (line > 0)
				System.out.println(a + line + "\t" + filedata.get(line-1));
			System.out.println(a + (line+1) + "\t" + filedata.get(line) + Constants.ANSI_RESET + "\n" + a);
		} catch (IndexOutOfBoundsException e) {
			System.out.println(Constants.ANSI_RESET + "\n");
		}
		if (immediatelyFatal) {
			System.exit(2);
		}
		else {
			parseFailed = true;
		}

	}



	void PrintParserMessage(String desc) {
		String a = "";
		for (int i = 0; i < parseLayer; i++) {
			a += "|   ";
		}
		a += desc.replaceAll("\n", "\n"+a);
		System.out.println(a);
	}
}
