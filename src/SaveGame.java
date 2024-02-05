package src;

import java.sql.Timestamp;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.zip.*;

public class SaveGame {
	public static boolean save(String name) {
		try {
			String fp = "saves/" + generateFilename(name);
			File f = new File(fp);
			if (f.createNewFile()) {
				System.out.println("New save file created: " + f.getName());
			} else {
				System.out.println("File already exists! Overwrite existing file?");
				String cmd = Data.globalScanner.nextLine().trim().toLowerCase();
				if (!(cmd.equals("y") || cmd.equals("yes"))) {
					System.out.println("Save cancelled.");
					return false;
				}
			}

			write("SAVE");
			write("Sumatra 1");
			write("save name: " + name);
			write("timestamp: " + System.currentTimeMillis());
			write("THINGS");
			for (Thing t : Data.things)
				write(HumanReadable.represent(t));
			write("ROOMS");
			for (Room r : Data.rooms)
				write(HumanReadable.represent(r));
			write("HOOKS");
			for (Hook h : Data.hooks)
				write(HumanReadable.represent(h));
			write("GLOBAL VARIABLES");
			for (String key : Data.variables.keySet())
				write(key + ": " + Data.variables.get(key));
			write("METADATA");
			for (String key : Data.metadata.keySet())
				write(key + ": " + Data.metadata.get(key));
			write("SPECIAL");
			write("catch point: " + Data.catchPoint);
			write("command prompt: " + Data.prompt);
			write("print error messages: " + Data.printErrorMessages);
			write("attached object: " + Data.attachedObject);
			write("END");

			ByteArrayOutputStream b = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(b);
			gzip.write(buffer.getBytes());
			gzip.close();

			FileOutputStream fout = new FileOutputStream(fp);
			b.writeTo(fout);
			fout.close();

			return true;
		} catch (IOException e) {
			System.out.println("Save failed.");
			e.printStackTrace();

			return false;
		}
	}

	static String generateFilename(String s) {
		return s + ".gz";
	}

	static String buffer = "";

	static void write(String s) {
		buffer += s;
		buffer += "\n";
	}
}
