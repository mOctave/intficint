package intficint.src;

import java.lang.reflect.Array;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringFormatter {
	public static String format_player(String string) {
		String s = string;
		Pattern p;
		Matcher m;

		// Replace name, description, and location
		s = s.replaceAll("«N»", Player.name);
		s = s.replaceAll("«n»", Player.name.toLowerCase());
		s = s.replaceAll("«D»", Player.desc);
		s = s.replaceAll("«d»", Player.desc.toLowerCase());
		s = s.replaceAll("«L»", Player.location);
		s = s.replaceAll("«l»", Player.location.toLowerCase());

		// Replace aliases
		p = Pattern.compile("«(A|a)\\((\\d+)\\)»");
		m = p.matcher(s);
		while (m.find()) {
			int i = Integer.parseInt(m.group(2));
			String a = Player.aliases.get(i);
			if (m.group(1).equals("A"))
				s = s.replaceFirst("«A\\("+i+"\\)»", a);
			else
				s = s.replaceFirst("«a\\("+i+"\\)»", a.toLowerCase());
		}

		// Replace custom attributes
		p = Pattern.compile("«(C|c)\\(([^»]+)\\)»");
		m = p.matcher(s);
		while (m.find()) {
			String a = m.group(2);
			String b = Player.attributes.get(a);
			if (m.group(1).equals("C"))
				s = s.replaceFirst("«C\\("+a+"\\)»", b);
			else
				s = s.replaceFirst("«c\\("+a+"\\)»", b.toLowerCase());
		}

		s = general_format(s);
		return s;
	}



	public static String format(String string, Thing obj) {
		String s = string;
		Pattern p;
		Matcher m;

		// Replace name, description, and location
		s = s.replaceAll("«N»", obj.name);
		s = s.replaceAll("«n»", obj.name.toLowerCase());
		s = s.replaceAll("«D»", obj.desc);
		s = s.replaceAll("«d»", obj.desc.toLowerCase());
		s = s.replaceAll("«L»", obj.location);
		s = s.replaceAll("«l»", obj.location.toLowerCase());

		// Replace aliases
		p = Pattern.compile("«(A|a)\\((\\d+)\\)»");
		m = p.matcher(s);
		while (m.find()) {
			int i = Integer.parseInt(m.group(2));
			String a = (String)Array.get(obj.aliases, i);
			if (m.group(1).equals("A"))
				s = s.replaceFirst("«A\\("+i+"\\)»", a);
			else
				s = s.replaceFirst("«a\\("+i+"\\)»", a.toLowerCase());
		}

		// Replace custom attributes
		p = Pattern.compile("«(C|c)\\(([^»]+)\\)»");
		m = p.matcher(s);
		while (m.find()) {
			String a = m.group(2);
			String b = obj.attributes.get(a);
			if (m.group(1).equals("C"))
				s = s.replaceFirst("«C\\("+a+"\\)»", b);
			else
				s = s.replaceFirst("«c\\("+a+"\\)»", b.toLowerCase());
		}

		s = general_format(s);
		return s;
	}



	public static String format(String string, Room obj) {
		String s = string;
		Pattern p;
		Matcher m;

		// Replace name, description, and location
		s = s.replaceAll("«N»", obj.name);
		s = s.replaceAll("«n»", obj.name.toLowerCase());
		s = s.replaceAll("«D»", obj.desc);
		s = s.replaceAll("«d»", obj.desc.toLowerCase());

		// Replace aliases
		p = Pattern.compile("«(A|a)\\((\\d+)\\)»");
		m = p.matcher(s);
		while (m.find()) {
			int i = Integer.parseInt(m.group(2));
			String a = (String)Array.get(obj.aliases, i);
			if (m.group(1).equals("A"))
				s = s.replaceFirst("«A\\("+i+"\\)»", a);
			else
				s = s.replaceFirst("«a\\("+i+"\\)»", a.toLowerCase());
		}

		// Replace exits
		p = Pattern.compile("«(X|x)\\((\\d+)\\)»");
		m = p.matcher(s);
		while (m.find()) {
			String a = obj.exits.get(m.group(2).trim());
			if (m.group(1).equals("X"))
				s = s.replaceFirst("«X\\("+m.group(2)+"\\)»", a);
			else
				s = s.replaceFirst("«x\\("+m.group(2)+"\\)»", a.toLowerCase());
		}

		// Replace custom attributes
		p = Pattern.compile("«(C|c)\\(([^»]+)\\)»");
		m = p.matcher(s);
		while (m.find()) {
			String a = m.group(2);
			String b = obj.attributes.get(a);
			if (m.group(1).equals("C"))
				s = s.replaceFirst("«C\\("+a+"\\)»", b);
			else
				s = s.replaceFirst("«c\\("+a+"\\)»", b.toLowerCase());
		}

		s = general_format(s);
		return s;
	}



	public static String format(String string, Exit obj) {
		String s = string;
		Pattern p;
		Matcher m;

		// Replace name, description, and location
		s = s.replaceAll("«N»", obj.name);
		s = s.replaceAll("«n»", obj.name.toLowerCase());

		// Replace aliases
		p = Pattern.compile("«(A|a)\\((\\d+)\\)»");
		m = p.matcher(s);
		while (m.find()) {
			int i = Integer.parseInt(m.group(2));
			String a = (String)Array.get(obj.aliases, i);
			if (m.group(1).equals("A"))
				s = s.replaceFirst("«A\\("+i+"\\)»", a);
			else
				s = s.replaceFirst("«a\\("+i+"\\)»", a.toLowerCase());
		}

		// Replace custom attributes
		p = Pattern.compile("«(C|c)\\(([^»]+)\\)»");
		m = p.matcher(s);
		while (m.find()) {
			String a = m.group(2);
			String b = obj.attributes.get(a);
			if (m.group(1).equals("C"))
				s = s.replaceFirst("«C\\("+a+"\\)»", b);
			else
				s = s.replaceFirst("«c\\("+a+"\\)»", b.toLowerCase());
		}

		s = general_format(s);
		return s;
	}



	public static String general_format(String string) {
		String s = string;
		Pattern p;
		Matcher m;

		// Convert to uppercase
		p = Pattern.compile("«(?i)U\\( ([^»]+) \\)»");
		m = p.matcher(s);
		while (m.find()) {
			String a = m.group(1);
			String b = m.group(1).toUpperCase();
			s = s.replaceFirst("«(?i)U\\( "+a+" \\)»", b);
		}

		// ANSI stuff
		s = s.replaceAll("«»", Constants.ANSI_RESET);

		s = s.replaceAll("«(?i)red letters»", Constants.ANSI_RED);
		s = s.replaceAll("«(?i)yellow letters»", Constants.ANSI_YELLOW);
		s = s.replaceAll("«(?i)green letters»", Constants.ANSI_GREEN);
		s = s.replaceAll("«(?i)cyan letters»", Constants.ANSI_CYAN);
		s = s.replaceAll("«(?i)blue letters»", Constants.ANSI_BLUE);
		s = s.replaceAll("«(?i)purple letters»", Constants.ANSI_PURPLE);
		s = s.replaceAll("«(?i)black letters»", Constants.ANSI_BLACK);
		s = s.replaceAll("«(?i)white letters»", Constants.ANSI_WHITE);
		s = s.replaceAll("«(?i)default letters»", Constants.ANSI_DEFAULT);

		s = s.replaceAll("«(?i)bright red letters»", Constants.ANSI_BRIGHT_RED);
		s = s.replaceAll("«(?i)bright yellow letters»", Constants.ANSI_BRIGHT_YELLOW);
		s = s.replaceAll("«(?i)bright green letters»", Constants.ANSI_BRIGHT_GREEN);
		s = s.replaceAll("«(?i)bright cyan letters»", Constants.ANSI_BRIGHT_CYAN);
		s = s.replaceAll("«(?i)bright blue letters»", Constants.ANSI_BRIGHT_BLUE);
		s = s.replaceAll("«(?i)bright purple letters»", Constants.ANSI_BRIGHT_PURPLE);
		s = s.replaceAll("«(?i)bright black letters»", Constants.ANSI_BRIGHT_BLACK);
		s = s.replaceAll("«(?i)bright white letters»", Constants.ANSI_BRIGHT_WHITE);

		s = s.replaceAll("«(?i)red background»", Constants.ANSI_RED_BACKGROUND);
		s = s.replaceAll("«(?i)yellow background»", Constants.ANSI_YELLOW_BACKGROUND);
		s = s.replaceAll("«(?i)green background»", Constants.ANSI_GREEN_BACKGROUND);
		s = s.replaceAll("«(?i)cyan background»", Constants.ANSI_CYAN_BACKGROUND);
		s = s.replaceAll("«(?i)blue background»", Constants.ANSI_BLUE_BACKGROUND);
		s = s.replaceAll("«(?i)purple background»", Constants.ANSI_PURPLE_BACKGROUND);
		s = s.replaceAll("«(?i)black background»", Constants.ANSI_BLACK_BACKGROUND);
		s = s.replaceAll("«(?i)white background»", Constants.ANSI_WHITE_BACKGROUND);
		s = s.replaceAll("«(?i)default background»", Constants.ANSI_DEFAULT_BACKGROUND);

		s = s.replaceAll("«(?i)bright red background»", Constants.ANSI_BRIGHT_RED_BACKGROUND);
		s = s.replaceAll("«(?i)bright yellow background»", Constants.ANSI_BRIGHT_YELLOW_BACKGROUND);
		s = s.replaceAll("«(?i)bright green background»", Constants.ANSI_BRIGHT_GREEN_BACKGROUND);
		s = s.replaceAll("«(?i)bright cyan background»", Constants.ANSI_BRIGHT_CYAN_BACKGROUND);
		s = s.replaceAll("«(?i)bright blue background»", Constants.ANSI_BRIGHT_BLUE_BACKGROUND);
		s = s.replaceAll("«(?i)bright purple background»", Constants.ANSI_BRIGHT_PURPLE_BACKGROUND);
		s = s.replaceAll("«(?i)bright black background»", Constants.ANSI_BRIGHT_BLACK_BACKGROUND);
		s = s.replaceAll("«(?i)bright white background»", Constants.ANSI_BRIGHT_WHITE_BACKGROUND);

		s = s.replaceAll("«(?i)bold on»", Constants.ANSI_BOLD);
		s = s.replaceAll("«(?i)dim on»", Constants.ANSI_DIM);
		s = s.replaceAll("«(?i)bold off»", Constants.ANSI_BOLD_RESET);

		s = s.replaceAll("«(?i)italic on»", Constants.ANSI_ITALIC);
		s = s.replaceAll("«(?i)italic off»", Constants.ANSI_ITALIC_RESET);

		s = s.replaceAll("«(?i)underline on»", Constants.ANSI_UNDERLINE);
		s = s.replaceAll("«(?i)underline off»", Constants.ANSI_UNDERLINE_RESET);

		s = s.replaceAll("«(?i)strikethrough on»", Constants.ANSI_STRIKETHROUGH);
		s = s.replaceAll("«(?i)strikethrough off»", Constants.ANSI_STRIKETHROUGH_RESET);

		s = s.replaceAll("«(?i)blinking on»", Constants.ANSI_BLINKING);
		s = s.replaceAll("«(?i)blinking off»", Constants.ANSI_BLINKING_RESET);

		s = s.replaceAll("«(?i)inverse on»", Constants.ANSI_INVERSE);
		s = s.replaceAll("«(?i)inverse off»", Constants.ANSI_INVERSE_RESET);

		s = s.replaceAll("«(?i)hidden on»", Constants.ANSI_HIDDEN);
		s = s.replaceAll("«(?i)hidden off»", Constants.ANSI_HIDDEN_RESET);

		s = s.replaceAll("«ansi»", Constants.ANSI_ESC);

		// Other substitutions
		s = s.replaceAll("«newline»", "\n");
		s = s.replaceAll("tab", "\t");

		return s.replaceFirst("^\\s++", "").replaceFirst("\\s++$","");
	}
}
