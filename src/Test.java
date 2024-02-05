package intficint.src;

import java.io.FileNotFoundException;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Scanner;

public class Test {
	public static void main(String[] args) throws FileNotFoundException {
		System.out.println(Constants.ANSI_BLUE+"\nRunning Tests...\n"+Constants.ANSI_RESET);
		Parser parseMain = new Parser();
		Array.set(parseMain.fileLocations, 1, parseMain.thisLoc+"/extras/intficint/sumatra/");
		parseMain.read("intficint/example.sumatra");
		System.out.println(parseMain.filedata);
		parseMain.parseFiledata();
		System.out.println(HumanReadable.represent(Data.things));
		System.out.println(HumanReadable.represent(Data.rooms));
		System.out.println(HumanReadable.representPlayer());
		System.out.println(StringFormatter.format(Data.things.get(0).desc, Data.things.get(0)));
		System.out.println(Data.methodcode);
		System.out.println(Data.labels);
		for (String x : Data.methods.keySet())
			System.out.printf("Method \"%s\": \"%s\"%n", x, HumanReadable.represent(Data.methods.get(x)));
		Data.localVars.push(new HashMap<>());
		Data.line = Data.labels.get("testall");
		Data.globalScanner = new Scanner(System.in);
		Methodcode.enter();
		System.out.println(Data.variables);
		System.out.println(Data.localVars.peek());
		System.out.println(Data.gotoStack);
		System.out.println(HumanReadable.represent(Data.hooks));
	}
}
