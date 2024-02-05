import src.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

public class Stylecheck {
	static final Pattern LEADING_WHITESPACE = Pattern.compile("^[^\\S\\t\\n\\r]");
	static final Pattern TRAILING_WHITESPACE = Pattern.compile("[^\\S\\n\\r]$");
	static final Pattern FINAL_NEWLINE = Pattern.compile("[^\\n\\r]$");
	static int errorsThrown = 0;

	public static void main(String[] args) throws FileNotFoundException {
		System.out.println(Constants.ANSI_BLUE+"\nChecking Style...\n"+Constants.ANSI_RESET);
		File dir = new File("../src");
		File[] dList = dir.listFiles();
		if (dList != null) {
			for (File f : dList) {
				check(f);
			}
		}

		dir = new File("../tests");
		dList = dir.listFiles();
		if (dList != null) {
			for (File f : dList) {
				check(f);
			}
		}

		if (errorsThrown > 0) {
			System.out.printf(
				"%s\nStyle check failed with %d errors.\n%s%n",
				Constants.ANSI_RED, errorsThrown, Constants.ANSI_RESET
			);
			System.exit(errorsThrown);
		}

		System.out.println(Constants.ANSI_GREEN+"\nAll files passed style check!\n"+Constants.ANSI_RESET);
	}

	static void check(File f) throws FileNotFoundException {
		String fname = f.getName();
		if (!fname.endsWith(".java"))
			return;
		int line = 0;
		Scanner s = new Scanner(f);
		System.out.println(Constants.ANSI_BLUE+"\nChecking "+fname+"\n"+Constants.ANSI_RESET);
		String l = "";
		Matcher m;
		while (s.hasNextLine()) {
			line ++;
			l = s.nextLine();

			// Check for leading spaces
			m = LEADING_WHITESPACE.matcher(l);
			if (m.find())
				error("Indentation with spaces", fname, line);

			// Check for trailing whitespace
			m = TRAILING_WHITESPACE.matcher(l);
			if (m.find())
				error("Trailing whitespace", fname, line);

			// Check for too-long lines
			long tabs = l.chars().filter(ch -> ch == '\t').count();
			if ((l.length() + tabs * 3) > 120) // Tabs count as 4 characters
				error("Line longer than 120 chars", fname, line);
		}
		// Check for final newline
		try {
			if (!finalNewline(f))
				error("Missing final newline", fname, line);
		} catch (IOException e) {
			e.printStackTrace();
		}

		s.close();
	}

	static void error(String s, String f, int ln) {
		System.out.printf("Error on line %d of %s: %s.%n", ln, f, s);
		errorsThrown ++;
	}

	static boolean finalNewline(File f) throws IOException {
		try (RandomAccessFile fileHandler = new RandomAccessFile(f, "r")) {
			long fileLength = fileHandler.length() - 1;
			if (fileLength < 0)
				return true;
			fileHandler.seek(fileLength);
			byte readByte = fileHandler.readByte();

			return readByte == 0xA || readByte == 0xD;
		}
	}
}
