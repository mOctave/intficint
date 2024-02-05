package intficint.src;

import java.lang.reflect.Array;
import java.lang.Math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class Methodcode {

	static List<String> lastEvaledLine = new ArrayList<> ();
	static boolean lineFailed = false;


	// Start at an entry point and work your way to an exit
	public static void enter() {
		while (parseLine(Data.line, Data.globalScanner)) {
			Data.line++;
		}
	}



	// Parse a single line of methodcode
	public static boolean parseLine(int lnum, Scanner s) {
		String line, firstWord;
		String[] args;

		if (lnum >= Data.methodcode.size()) {
			ThrowMethodcodeWarning("End of code reached", lnum - 1);
			return false;
		} else {
			line = Data.methodcode.get(lnum);
			args = line.split("\\s+",0);
			firstWord = getLower(args, 0);
		}

		// Set variables
		if (firstWord.equals("let")) {
			Data.variables.put(getLower(args, 1), evalExpression(getAfter(args, 2), s));
		} else if (firstWord.equals("local")) {
			Data.localVars.peek().put(getLower(args, 1), evalExpression(getAfter(args, 2), s));
		} else if (firstWord.equals("set")) {
			evalExpression(getAfter(args, 1), s);
			if (lastEvaledLine.size() < 3)
				ThrowMethodcodeError("Missing arguments in SET statement", lnum);
			else if (lastEvaledLine.size() > 3)
				ThrowMethodcodeWarning("Extra arguments in SET statement", lnum);
			setObjectAttribute(lastEvaledLine.get(0), lastEvaledLine.get(1).toLowerCase(), lastEvaledLine.get(2));
		} else if (firstWord.equals("attach")) {
			Data.attachedObject = evalExpression(getAfter(args, 1), s);
		} else if (firstWord.equals("detach")) {
			Data.attachedObject = "";
		}

		// Handle I/O
		else if (firstWord.equals("print")) {
			System.out.println(evalExpression(getAfter(args, 1), s));
		} else if (firstWord.equals("say")) {
			System.out.println(format(evalExpression(getAfter(args, 1), s)));
		} else if (firstWord.equals("cprmpt")) {
			Data.prompt = evalExpression(getAfter(args, 1), s);
		}

		// Handle flow
		else if (firstWord.equals("goto")) {
			try {
				Data.gotoStack.push(lnum);
				Data.line = Data.labels.get(evalExpression(getAfter(args, 1), s).toLowerCase());
			} catch (NullPointerException e) {
				ThrowMethodcodeError("Invalid GOTO location", lnum);
			}
		} else if (firstWord.equals("goback")) {
			try {
				Data.line = Data.gotoStack.pop();
			} catch (EmptyStackException e) {
				ThrowMethodcodeError("GOTO stack is empty", lnum);
			}
		} else if (firstWord.equals("skipto")) {
			try {
				Data.line = Data.labels.get(evalExpression(getAfter(args, 1), s).toLowerCase());
			} catch (NullPointerException e) {
				ThrowMethodcodeError("Invalid SKIPTO location", lnum);
			}
		} else if (firstWord.equals("push")) {
			Data.gotoStack.push(lnum);
		} else if (firstWord.equals("drop")) {
			try {
				Data.gotoStack.pop();
			} catch (EmptyStackException e) {
				ThrowMethodcodeWarning("GOTO stack is empty", lnum);
			}
		}

		// If then else
		else if (firstWord.equals("if")) {
			String condition = "", thenGo = "", elseGo = "";
			evalExpression(getAfter(args, 1), s);
			if (lastEvaledLine.get(lastEvaledLine.size() - 2).toLowerCase().equals("else")) {
				thenGo = lastEvaledLine.get(lastEvaledLine.size() - 3);
				elseGo = lastEvaledLine.get(lastEvaledLine.size() - 1);
				condition = listToSpacedString(lastEvaledLine.subList(0, lastEvaledLine.size() - 4));
			} else if (lastEvaledLine.get(lastEvaledLine.size() - 2).toLowerCase().equals("then")) {
				thenGo = lastEvaledLine.get(lastEvaledLine.size() - 1);
				condition = listToSpacedString(lastEvaledLine.subList(0, lastEvaledLine.size() - 2));
			} else {
				ThrowMethodcodeError("Inconclusive IF statement", lnum);
			}

			if (Boolean.valueOf(condition)) {
				try {
					Data.line = Data.labels.get(thenGo.toLowerCase());
				} catch (NullPointerException e) {
					ThrowMethodcodeError("Invalid GOTO location on THEN statement", lnum);
				}
			} else {
				if (!elseGo.equals("")) {
					try {
						Data.line = Data.labels.get(elseGo.toLowerCase());
					} catch (NullPointerException e) {
						ThrowMethodcodeError("Invalid GOTO location on ELSE statement", lnum);
					}
				}
			}
		}

		// Execute methods, return values
		else if (firstWord.equals("execute")) {
			// Eval the expression to get data
			evalExpression(getAfter(args, 1), s);

			// Get the method to execute if it exists
			int[] methodData = Data.methods.get(lastEvaledLine.get(0).toLowerCase());
			if (methodData == null) {
				ThrowMethodcodeError("Method " + lastEvaledLine.get(0) + " not found", lnum);
			}

			// Pass inputs
			if (lastEvaledLine.size() - 1 != (int) Array.get(methodData, 1)) {
				ThrowMethodcodeError(
					"Expected "+Array.get(methodData, 1)+" inputs to method, got "+(lastEvaledLine.size() - 1), lnum
				);
			}

			Data.localVars.push(new HashMap<String, String> ());
			for (int i = 1; i < lastEvaledLine.size(); i++) {
				HashMap<String,String> hm = Data.localVars.pop();
				hm.put(Integer.toString(i), lastEvaledLine.get(i));
				Data.localVars.push(hm);
			}

			// Start method execution
			Data.gotoStack.push(Data.line);
			Data.methodStack.push(lastEvaledLine.get(0).toLowerCase());
			Data.line = (int) Array.get(methodData, 0);
		}
		else if (firstWord.equals("return")) {
			if (Data.methodStack.size() == 0) {
				return false;
			} else {
				// Eval the expression to get data
				evalExpression(getAfter(args, 1), s);

				// Check if the right number of returns are being made
				int[] methodData = Data.methods.get(Data.methodStack.pop());
				if (lastEvaledLine.size() != (int) Array.get(methodData, 2)) {
					ThrowMethodcodeError(
						"Expected "+Array.get(methodData, 1)+" returns from method, got "+(lastEvaledLine.size()), lnum
					);
				}

				// Pass the evaled line back as local variables
				Data.localVars.pop();
				HashMap<String,String> hm = new HashMap<>();
				try {
					hm = Data.localVars.pop();
				} catch (EmptyStackException e) {
					Data.gotoStack = new Stack<>();
					return false;
				}
				for (int i = 0; i < lastEvaledLine.size(); i++) {
					hm.put("r"+Integer.toString(i+1), lastEvaledLine.get(i));
				}
				Data.localVars.push(hm);

				try {
					Data.line = Data.gotoStack.pop();
				} catch (EmptyStackException e) {
					return false;
				}
			}
		}

		// Catch errors
		else if (firstWord.equals("catch")) {
			try {
				String exp = evalExpression(getAfter(args, 1), s);
				Data.catchPoint = Data.labels.get(exp.toLowerCase());
			} catch (NullPointerException e) {
				ThrowMethodcodeWarning("Invalid CATCH label", lnum);
				Data.catchPoint = -1;
			}
		} else if (firstWord.equals("endcatch")) {
			Data.catchPoint = -1;
		}

		// Exit cleanly
		else if (firstWord.equals("exit")) {
			System.out.println("\u001B[?1049l");
			System.exit(0);
		}

		// Warnings and errors
		else if (firstWord.equals("warn"))
			ThrowMethodcodeWarning("Thrown by methodcode - " + evalExpression(getAfter(args, 1), s), lnum);
		else if (firstWord.equals("error"))
			ThrowMethodcodeError("Thrown by methodcode - " + evalExpression(getAfter(args, 1), s), lnum);
		else if (!"/label/method/".contains("/" + firstWord + "/"))
			ThrowMethodcodeWarning("Invalid operation", lnum);

		return true;
	}



	// Evaluate an expression given its component parts
	public static String evalExpression(List<String> exp, Scanner s) {
		lineFailed = false;
		String currentResult = "";
		String o;
		List<String> x = new ArrayList<>(exp);

		// String grouping operators;
		String currentGroup = "";
		boolean inGroup = false;
		int groupStart = 0;
		for (int i = 0; i < x.size(); i++) {
			if (x.get(i).equals("\"")) {
				if (inGroup) {
					x.set(groupStart, currentGroup.trim());
					for (int j = groupStart; j < i; j++)
						x.set(j+1, "");
					inGroup = false;
				} else {
					currentGroup = "";
					groupStart = i;
					inGroup = true;
				}
			} else if (inGroup) {
				currentGroup += x.get(i) + " ";
			}
		}
		x = clearBlanks(x);

		// Sub in any variables
		for (int i = 0; i < x.size(); i++) {
			String a = x.get(i).toLowerCase().substring(1);
			if (x.get(i).startsWith("@")) {
				if (a.equals("null")) {
					x.set(i, null);
				} else {
					try {
						x.set(i, Data.variables.get(a));
						if (x.get(i).length() > 0);
					} catch (NullPointerException e) {
						ThrowMethodcodeError("Unknown global variable \"" + a + "\"", Data.line);
					}
				}
			}
			else if (x.get(i).startsWith("$")) {
				try {
					x.set(i, Data.localVars.peek().get(a));
					if (x.get(i).length() > 0);
				} catch (NullPointerException e) {
					ThrowMethodcodeError("Unknown local variable \"" + a + "\"", Data.line);
				}

			}
		}
		// Clear blanks
		x = clearBlanks(x);

		// Get any requests for user input
		for (int i = 0; i < x.size(); i++) {
			if (x.get(i).toLowerCase().equals("input")) {
				System.out.print(Data.prompt);
				String a = s.nextLine();
				x.set(i, a.trim());
				if (a.equals(""))
					ThrowMethodcodeWarning("No value input", Data.line);
			}
		}

		// Deal with parentheses
		int pDepth = 0, openP = 0;
		for (int i = 0; i < x.size(); i++) {
			if (x.get(i).equals("(")) {
				if (pDepth == 0)
					openP = i;
				pDepth++;
			} else if (x.get(i).equals(")")) {
				pDepth--;
				if (pDepth == 0) {
					x.set(openP, evalExpression(x.subList(openP+1, i), s));
					for (int j = openP; j < i; j++)
						x.set(j+1, "");
				}
			}
		}
		// Clear blanks
		x = clearBlanks(x);

		// Deal with attribute calls
		for (int i = 0; i < x.size(); i++) {
			o = x.get(i);

			// Check if the operator is an attribute call
			if (o.equals(":")) {
				// Find operands
				String a = x.get(i-1);
				int j = 1;
				while (a.equals("")) {
					j++;
					a = x.get(i-j);
				}

				String b = x.get(i+1);
				int k = 1;
				while (b.equals("")) {
					k++;
					b = x.get(i+k);
				}

				// Sub it in
				x.set(i, getObjectAttribute(a, b));

				// Set the list
				x.set(i-j, "");
				x.set(i+k, "");
			}
		}
		// Clear blanks
		x = clearBlanks(x);

		// Start by searching for precedence 5
		for (int i = 0; i < x.size(); i++) {
			o = x.get(i);
			try {
				// Check if the operator is string operator
				if (o.equals("~") || o.equals("¢") || o.equals("¬")) {
					// Find operands
					String a = "";
					int j = 1;
					if (!o.equals("¬")){
						a = x.get(i-1);
						while (a.equals("")) {
							j++;
							a = x.get(i-j);
						}
					}

					String b = x.get(i+1);
					int k = 1;
					while (b.equals("")) {
						k++;
						b = x.get(i+k);
					}
					// Get the stuff from the string. Negative b values are
					// added to the list length to simulate subtraction.
					if (o.equals("~")) {
						int index = (int) Double.parseDouble(b);
						if (index < 0)
							x.set(i, a.substring(0, a.length() + index));
						else
							x.set(i, a.substring(index));
					} else if (o.equals("¢")) {
						int index = (int) Double.parseDouble(b);
						if (index < 0) {
							if (index == -1)
								x.set(i, a.substring(a.length() + index));
							else
								x.set(i, a.substring(a.length() + index, a.length() + index + 1));
						}
						else
							x.set(i, a.substring(index, index + 1));
					} else if (o.equals("¬")) {
						x.set(i, Integer.toString(b.length()));
					}

					// Set the list
					if (!o.equals("¬"))
						x.set(i-j, "");
					x.set(i+k, "");
				}
			} catch (StringIndexOutOfBoundsException e) {
				ThrowMethodcodeError("String index out of bounds", Data.line);
			}
		}
		// Clear blanks
		x = clearBlanks(x);

		// Then precedence 4
		for (int i = 0; i < x.size(); i++) {
			o = x.get(i);

			// Check if the operator is exponentation or modulus
			if (o.equals("^") || o.equals("%")) {
				// Find operands
				String a = x.get(i-1);
				int j = 1;
				while (a.equals("")) {
					j++;
					a = x.get(i-j);
				}

				String b = x.get(i+1);
				int k = 1;
				while (b.equals("")) {
					k++;
					b = x.get(i+k);
				}

				// Do the math
				if (o.equals("^")) {
					x.set(i, Double.toString(Math.pow(Double.parseDouble(a), Double.parseDouble(b))));
				} else {
					x.set(i, Double.toString(Double.parseDouble(a) % Double.parseDouble(b)));
				}

				// Set the list
				x.set(i-j, "");
				x.set(i+k, "");
			}
		}
		// Clear blanks
		x = clearBlanks(x);

		// Then precedence 3
		for (int i = 0; i < x.size(); i++) {
			o = x.get(i);

			// Check if the operator is multiplication or division
			if (o.equals("*") || o.equals("/")) {
				// Find operands
				String a = x.get(i-1);
				int j = 1;
				while (a.equals("")) {
					j++;
					a = x.get(i-j);
				}

				String b = x.get(i+1);
				int k = 1;
				while (b.equals("")) {
					k++;
					b = x.get(i+k);
				}

				// Do the math
				if (o.equals("*")) {
					x.set(i, Double.toString(Double.parseDouble(a) * Double.parseDouble(b)));
				} else {
					x.set(i, Double.toString(Double.parseDouble(a) / Double.parseDouble(b)));
				}

				// Set the list
				x.set(i-j, "");
				x.set(i+k, "");
			}
		}
		// Clear blanks
		x = clearBlanks(x);

		// Then precedence 2
		for (int i = 0; i < x.size(); i++) {
			o = x.get(i);

			// Check if the operator is addition or subtraction
			if (o.equals("+") || o.equals("-")) {
				// Find operands
				String a = x.get(i-1);
				int j = 1;
				while (a.equals("")) {
					j++;
					a = x.get(i-j);
				}

				String b = x.get(i+1);
				int k = 1;
				while (b.equals("")) {
					k++;
					b = x.get(i+k);
				}

				// Do the math
				if (o.equals("+")) {
					x.set(i, Double.toString(Double.parseDouble(a) + Double.parseDouble(b)));
				} else {
					x.set(i, Double.toString(Double.parseDouble(a) - Double.parseDouble(b)));
				}

				// Set the list
				x.set(i-j, "");
				x.set(i+k, "");
			}
		}
		// Clear blanks
		x = clearBlanks(x);

		// Then precedence 1
		for (int i = 0; i < x.size(); i++) {
			o = x.get(i);

			// Check if the operator is a comparison operator
			if ("/==/!==/=/!=/>/</>=/<=/".contains("/"+o+"/")) {
				// Find operands
				String a = x.get(i-1);
				int j = 1;
				while (a.equals("")) {
					j++;
					a = x.get(i-j);
				}

				String b = x.get(i+1);
				int k = 1;
				while (b.equals("")) {
					k++;
					b = x.get(i+k);
				}

				// Do the math
				if (o.equals("==")) {
					x.set(i, Boolean.toString(a.equals(b)));
				} else if (o.equals("!==")) {
					x.set(i, Boolean.toString(!a.equals(b)));
				} else if (o.equals("=")) {
					x.set(i, Boolean.toString(Double.parseDouble(a) == Double.parseDouble(b)));
				} else if (o.equals("!=")) {
					x.set(i, Boolean.toString(Double.parseDouble(a) != Double.parseDouble(b)));
				} else if (o.equals(">")) {
					x.set(i, Boolean.toString(Double.parseDouble(a) > Double.parseDouble(b)));
				} else if (o.equals("<")) {
					x.set(i, Boolean.toString(Double.parseDouble(a) < Double.parseDouble(b)));
				} else if (o.equals(">=")) {
					x.set(i, Boolean.toString(Double.parseDouble(a) >= Double.parseDouble(b)));
				} else if (o.equals("<=")) {
					x.set(i, Boolean.toString(Double.parseDouble(a) <= Double.parseDouble(b)));
				}

				// Set the list
				x.set(i-j, "");
				x.set(i+k, "");
			}
		}
		// Clear blanks
		x = clearBlanks(x);

		// Then precedence 0
		for (int i = 0; i < x.size(); i++) {
			o = x.get(i);

			// Check if the operator is a boolean operator
			if ("/&&/||/!/".contains("/"+o+"/")) {
				// Find operands
				int j = 1;
				String a = "";
				if (!o.equals("!")) {
					a = x.get(i-1);
					while (a.equals("")) {
						j++;
						a = x.get(i-j);
					}
				}

				String b = x.get(i+1);
				int k = 1;
				while (b.equals("")) {
					k++;
					b = x.get(i+k);
				}

				// Do the math, set the list
				if (o.equals("&&")) {
					x.set(i, Boolean.toString(Boolean.parseBoolean(a) && Boolean.parseBoolean(b)));
					x.set(i-j, "");
				} else if (o.equals("||")) {
					x.set(i, Boolean.toString(Boolean.parseBoolean(a) || Boolean.parseBoolean(b)));
					x.set(i-j, "");
				} else if (o.equals("!")) {
					x.set(i, Boolean.toString(!Boolean.parseBoolean(b)));
				}

				// The next item is blanked for all logical operators
				x.set(i+k, "");
			}
		}
		// Clear blanks
		x = clearBlanks(x);

		// Deal with integer conversion operator
		for (int i = x.size() - 1; i > -1; i--) {
			if (x.get(i).toLowerCase().equals("int")) {
				x.set(i, Integer.toString((int) Double.parseDouble(x.get(i+1))));
				x.set(i+1, "");
			}
		}
		// Clear blanks
		x = clearBlanks(x);

		// Deal with formatting operator
		String stringToFormat = "";
		for (int i = x.size() - 1; i > -1; i--) {
			if (x.get(i).toLowerCase().equals("format")) {
				x.set(i, format(stringToFormat));
				x = x.subList(0, i+1);
				stringToFormat = "";
			} else {
				stringToFormat = x.get(i) + " " + stringToFormat;
			}
		}
		// Clear blanks
		x = clearBlanks(x);


		// Remove all § characters
		for (int i = 0; i<x.size(); i++)
			x.set(i, x.get(i).replaceAll("§", ""));

		// Set last evaled line list, replacing all ¶ and ‡ characters
		lastEvaledLine = new ArrayList<> ();
		for (String i : x)
			lastEvaledLine.add(i.replaceAll(".?¶", "").replaceAll("‡.?", ""));

		// Convert to string
		for (String i : x)
			currentResult += i + " ";
		if (!currentResult.equals(""))
			currentResult = currentResult.substring(0, currentResult.length()-1);

		// Remove all ¶ and ‡ characters
		currentResult = currentResult.replaceAll(".?¶", "");
		currentResult = currentResult.replaceAll("‡.?", "");

		// If an error was thrown, set the proper line now
		if (lineFailed)
			Data.line = Data.catchPoint;

		// Return the result
		return currentResult;
	}



	// Format a string
	public static String format(String s) {
		if (Data.attachedObject.equals("")) {
			return StringFormatter.general_format(s);
		} else {
			if (Player.name.toLowerCase().equals(Data.attachedObject) ||
				Utils.lowerCase(Player.aliases).contains(Data.attachedObject)) {
				return StringFormatter.format_player(s);
			}
			Thing t = getThing(Data.attachedObject);
			Room r;
			Exit x;
			if (t == Constants.NULL_THING) {
				r = getRoom(Data.attachedObject);
				if (r == Constants.NULL_ROOM) {
					x = getExit(Data.attachedObject);
					if (x == Constants.NULL_EXIT) {
						ThrowMethodcodeWarning("Object not found", Data.line);
						return StringFormatter.general_format(s);
					} else return StringFormatter.format(s, x);
				} else return StringFormatter.format(s, r);
			} else return StringFormatter.format(s, t);
		}
	}



	// Get a thing from its proper name or one of its aliases
	public static Thing getThing(String objectName) {
		String obj = objectName.toLowerCase();
		for (Thing t : Data.things) {
			if (t.name.toLowerCase().equals(obj) || Utils.lowerCase(Arrays.asList(t.aliases)).contains(obj)) {
				return t;
			}
		}
		return Constants.NULL_THING;
	}



	// Get a room from its proper name or one of its aliases
	public static Room getRoom(String objectName) {
		String obj = objectName.toLowerCase();
		for (Room r : Data.rooms) {
			if (r.name.toLowerCase().equals(obj) || Utils.lowerCase(Arrays.asList(r.aliases)).contains(obj)) {
				return r;
			}
		}
		return Constants.NULL_ROOM;
	}



	// Get an exit from its proper name or one of its aliases
	public static Exit getExit(String objectName) {
		String obj = objectName.toLowerCase();
		for (Exit x : Data.exits) {
			if (x.name.toLowerCase().equals(obj) || Utils.lowerCase(Arrays.asList(x.aliases)).contains(obj)) {
				return x;
			}
		}
		return Constants.NULL_EXIT;
	}



	// Get an object attribute from its proper name and a provided attribute key
	public static String getObjectAttribute(String objectName, String attribute) {
		String obj = objectName.toLowerCase();

		// The player gets first priority
		if (Player.name.toLowerCase().equals(obj) || Utils.lowerCase(Player.aliases).contains(obj)) {
			if (attribute.equals("name"))
				return Player.name;
			else if (attribute.equals("desc"))
				return Player.desc;
			else if (attribute.equals("location"))
				return Player.location;
			else if (attribute.startsWith("alias"))
				return (String) Array.get(Player.aliases, Integer.parseInt(attribute.substring(5).trim()));
			else if (attribute.equals("aliases"))
				return HumanReadable.libstyle(Player.aliases);
			else if (attribute.equals("contents"))
				return HumanReadable.libstyle(Player.contents());
			else {
				String s = Player.attributes.get(attribute);
				return s == null ? "nothing" : s;
			}
		}

		// Next, check things
		for (Thing t : Data.things) {
			if (t.name.toLowerCase().equals(obj) || Utils.lowerCase(Arrays.asList(t.aliases)).contains(obj)) {
				if (attribute.equals("name"))
					return t.name;
				else if (attribute.equals("desc"))
					return t.desc;
				else if (attribute.equals("location"))
					return t.location;
				else if (attribute.startsWith("alias"))
					return (String) Array.get(t.aliases, Integer.parseInt(attribute.substring(5).trim()));
				else if (attribute.equals("aliases"))
					return HumanReadable.libstyle(t.aliases);
				else if (attribute.equals("contents"))
					return HumanReadable.libstyle(t.contents());
				else {
					String s = t.attributes.get(attribute);
					return s == null ? "nothing" : s;
				}
			}
		}

		// Next, check rooms
		for (Room r : Data.rooms) {
			if (r.name.toLowerCase().equals(obj) || Utils.lowerCase(Arrays.asList(r.aliases)).contains(obj)) {
				if (attribute.equals("name"))
					return r.name;
				else if (attribute.equals("desc"))
					return r.desc;
				else if (attribute.startsWith("alias"))
					return (String) Array.get(r.aliases, Integer.parseInt(attribute.substring(5).trim()));
				else if (attribute.equals("aliases"))
					return HumanReadable.libstyle(r.aliases);
				else if (attribute.equals("contents"))
					return HumanReadable.libstyle(r.contents());
				else if (attribute.startsWith("exit")) {
					return r.exits.get(attribute.substring(4));
				}
				else if (attribute.equals("exits"))
					return ""+r.exits;
				else {
					String s = r.attributes.get(attribute);
					return s == null ? "nothing" : s;
				}
			}
		}

		// Finally, check exits
		for (Exit e : Data.exits) {
			if (e.name.toLowerCase().equals(obj) || Utils.lowerCase(Arrays.asList(e.aliases)).contains(obj)) {
				if (attribute.equals("name"))
					return e.name;
				else if (attribute.startsWith("alias")) {
					return (String) Array.get(e.aliases, Integer.parseInt(attribute.substring(5).trim()));
				} else if (attribute.startsWith("aliases")) {
					return HumanReadable.libstyle(e.aliases);
				}
				else
					return e.attributes.get(attribute);
			}
		}

		ThrowMethodcodeError("Object \""+objectName+"\" not found", Data.line);
		// Return won't be used, but it appeases the Java compiler
		return "Methodcode Fatal Error";
	}




	// Set an object attribute from its proper name, attribute key, and new value
	public static void setObjectAttribute(String objectName, String attribute, String value) {
		String obj = objectName.toLowerCase();
		boolean objFound = false;

		// The player gets first priority
		if (Player.name.toLowerCase().equals(obj) || Utils.lowerCase(Player.aliases).contains(obj)) {
			objFound = true;
			if (attribute.equals("name"))
				Player.name = value;
			else if (attribute.equals("desc"))
				Player.desc = value;
			else if (attribute.equals("location"))
				Player.location = value;
			else if (attribute.startsWith("addalias"))
				Player.aliases.add(value);
			else if (attribute.startsWith("remalias"))
				Player.aliases.remove(Player.aliases.indexOf(value));
			else
				Player.attributes.put(attribute, value);
		}

		// Next, check things
		for (Thing t : Data.things) {
			if (t.name.toLowerCase().equals(obj) || Utils.lowerCase(Arrays.asList(t.aliases)).contains(obj)) {
				objFound = true;
				List<String> aliases = new ArrayList<>(Arrays.asList(t.aliases));
				if (attribute.equals("name"))
					t.name = value;
				else if (attribute.equals("desc"))
					t.desc = value;
				else if (attribute.equals("location"))
					t.location = value;
				else if (attribute.startsWith("addalias")) {
					aliases.add(value);
					t.aliases = (String[]) aliases.toArray();
				} else if (attribute.startsWith("remalias")) {
					aliases.remove(aliases.indexOf(value));
					t.aliases = (String[]) aliases.toArray();
				}
				else
					t.attributes.put(attribute, value);
			}
		}

		// Next, check rooms
		for (Room r : Data.rooms) {
			if (r.name.toLowerCase().equals(obj) || Utils.lowerCase(Arrays.asList(r.aliases)).contains(obj)) {
				objFound = true;
				List<String> aliases = new ArrayList<>(Arrays.asList(r.aliases));
				if (attribute.equals("name"))
					r.name = value;
				else if (attribute.equals("desc"))
					r.desc = value;
				else if (attribute.startsWith("addalias")) {
					aliases.add(value);
					r.aliases = (String[]) aliases.toArray();
				} else if (attribute.startsWith("remalias")) {
					aliases.remove(aliases.indexOf(value));
					r.aliases = (String[]) aliases.toArray();
				} else if (attribute.startsWith("->")) {
					r.exits.put(attribute.substring(2).trim(), value);
				} else if (attribute.startsWith("remexit")) {
					r.exits.remove(value);
				}
				else
					r.attributes.put(attribute, value);
			}
		}

		// Finally, check exits
		for (Exit e : Data.exits) {
			if (e.name.toLowerCase().equals(obj) || Utils.lowerCase(Arrays.asList(e.aliases)).contains(obj)) {
				objFound = true;
				List<String> aliases = new ArrayList<>(Arrays.asList(e.aliases));
				if (attribute.equals("name"))
					e.name = value;
				else if (attribute.startsWith("addalias")) {
					aliases.add(value);
					e.aliases = (String[]) aliases.toArray();
				} else if (attribute.startsWith("remalias")) {
					aliases.remove(aliases.indexOf(value));
					e.aliases = (String[]) aliases.toArray();
				}
				else
					e.attributes.put(attribute, value);
			}
		}

		if (!objFound)
			ThrowMethodcodeWarning("Object \""+objectName+"\" not found", Data.line);
	}



	// Methodcode-specific helper methods
	public static String get(String[] arr, int i) {
		return Array.get(arr, i).toString();
	}



	public static String getLower(String[] arr, int i) {
		return Array.get(arr, i).toString().toLowerCase();
	}



	public static List<String> getAfter(String[] arr, int start) {
		List<String> al = Arrays.asList(arr);
		return al.subList(start, al.size());
	}



	public static List<String> getBetween(String[] arr, int start, int end) {
		List<String> al = Arrays.asList(arr);
		return al.subList(start, end);
	}



	public static List<String> clearBlanks(List<String> al) {
		List<String> newal = new ArrayList<>();
		for (int i = 0; i < al.size(); i++) {
			try {
				if(!al.get(i).equals(""))
					newal.add(al.get(i));
			} catch (NullPointerException e) {
				newal.add("«null value»");
			}
		}
		return newal;
	}



	public static String listToSpacedString(List<String> al) {
		String s = "";
		for (String word : al)
			s += word + " ";
		return s.trim();
	}



	// Warnings and errors
	public static void ThrowMethodcodeWarning(String desc, int line) {
		if (Data. catchPoint == -1 || Data.printErrorMessages) {
			Stack<Integer> s = Utils.flipStack((Stack<Integer>) Data.gotoStack.clone());
			int x = line;
			System.out.println(Constants.ANSI_YELLOW + "Methodcode Warning: " + desc);
			while (s.size() > 0) {
				x = s.pop();
				System.out.println(
					Data.lnumRef.get(x)[0] + "\t" + Data.lnumRef.get(x)[1] + "\t" + Data.methodcode.get(x)
					);
			}
			System.out.println(
				Data.lnumRef.get(line)[0] + "\t" + Data.lnumRef.get(line)[1] + "\t" + Data.methodcode.get(line)
			);
			System.out.println(Constants.ANSI_RESET);
		}
	}



	public static void ThrowMethodcodeError(String desc, int line) {
		if (Data. catchPoint == -1 || Data.printErrorMessages) {
			Stack<Integer> s = Utils.flipStack((Stack<Integer>) Data.gotoStack.clone());
			int x = line;
			if (Data.catchPoint == -1)
				System.out.println(Constants.ANSI_RED + "Fatal Methodcode Error: " + desc);
			else
				System.out.println(Constants.ANSI_YELLOW + "Caught Potentially Fatal Methodcode Error: " + desc);
			while (s.size() > 0) {
				x = s.pop();
				System.out.println(
					Data.lnumRef.get(x)[0] + "\t" + Data.lnumRef.get(x)[1] +  "\t" + Data.methodcode.get(x)
				);
			}
			System.out.println(
				Data.lnumRef.get(line)[0] + "\t" + Data.lnumRef.get(line)[1] + "\t" + Data.methodcode.get(line)
			);
			System.out.println(Constants.ANSI_RESET);
		}
		if (Data.catchPoint == -1) {
			System.out.println("\u001B[?1049l");
			System.exit(3);
		}
		else
			lineFailed = true;
	}
}
