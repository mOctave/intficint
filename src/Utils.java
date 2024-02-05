package intficint.src;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Utils {
	// Helper methods that are used by multiple other classes should be stored here
	public static Stack<Integer> flipStack(Stack<Integer> stack) {
		Stack<Integer> newstack = new Stack<>();
		while (stack.size() > 0)
			newstack.push(stack.pop());

		return newstack;
	}



	public static List<String> lowerCase(List<String> al) {
		List<String> newal = new ArrayList<>();
		for (String s : al) {
			newal.add(s.toLowerCase());
		}
		return newal;
	}
}
