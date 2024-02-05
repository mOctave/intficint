package src;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputHandler {
	// A class to handle all the user input to the program when it is running

	public static MatchedHook matchCommand(String x) {
		String cmd = x.trim();

		if (cmd.toLowerCase().equals("save")) {
			System.out.println("What would you like to call this save?");
			System.out.print(Data.prompt);
			String saveName = Data.globalScanner.nextLine().trim();
			if (SaveGame.save(saveName))
				System.out.println("Save successful!");
			return(Constants.FULFILLED_HOOK);
		}

		Pattern argNameCollector = Pattern.compile("«([^«]+)»");

		HashMap<String, String> namedArgs = new HashMap<>();
		for (Hook h : Data.hooks) {
			if (h.trigger.toLowerCase().equals("cmd")) {
				for (String c : Arrays.asList(h.commands)) {
					Matcher args = Pattern.compile(c.replaceAll("«[^«]+»", "(\\\\S|\\\\S.*\\\\S)")).matcher(cmd);
					Matcher argNames = argNameCollector.matcher(c);

					boolean match = args.matches();
					argNames.find();
					for (int i = 1; i != -1; i++) {
						try {
							namedArgs.put(argNames.group(i), args.group(i));
						} catch (Exception e) {
							break;
						}
					}
					if (match)
					return new MatchedHook(h, namedArgs);
				}
			}
		}
		return Constants.UNMATCHED_HOOK;
	}



	public static void execute(MatchedHook h, Scanner s) {
		Data.localVars.push(new HashMap<String, String> ());
		String[] args = h.hook.executable.split("\\s+",0);

		for (String argName : h.args.keySet()) {
			Data.localVars.peek().put(argName.toLowerCase(), h.args.get(argName));
		}

		// Eval the expression to get data
		Methodcode.evalExpression(Arrays.asList(args), s);

		// Get the method to execute if it exists
		int[] methodData = Data.methods.get(Methodcode.lastEvaledLine.get(0).toLowerCase());
		if (methodData == null) {
			Methodcode.ThrowMethodcodeError("Method " + Methodcode.lastEvaledLine.get(0) + " not found", Data.line);
		}

		// Pass inputs
		if (Methodcode.lastEvaledLine.size() - 1 != (int) Array.get(methodData, 1)) {
			Methodcode.ThrowMethodcodeError(
				"Expected "+Array.get(methodData, 1)+" inputs to method, got "+(Methodcode.lastEvaledLine.size() - 1),
				Data.line
			);
		}

		for (int i = 1; i < Methodcode.lastEvaledLine.size(); i++) {
			HashMap<String,String> hm = Data.localVars.pop();
			hm.put(Integer.toString(i), Methodcode.lastEvaledLine.get(i));
			Data.localVars.push(hm);
		}

		// Start method execution
		Data.line = (int) Array.get(methodData, 0);
		Data.methodStack.push(Methodcode.lastEvaledLine.get(0).toLowerCase());
		Methodcode.enter();
	}
}
