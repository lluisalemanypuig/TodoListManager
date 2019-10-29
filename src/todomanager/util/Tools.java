/*********************************************************************
 *
 *  TodoListManager - Open-source manager of todo lists
 *
 *  Copyright (C) 2019
 *
 *  This file is part of TodoListManager.
 *
 *  TodoListManager is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  TodoListManager is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with TodoListManager.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Contact: Lluís Alemany Puig (lluis.alemany.puig@gmail.com)
 *
 ********************************************************************/

package todomanager.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.stream.Stream;

/**
 * @brief Singleton class with a series of useful functions.
 * @author Lluís Alemany Puig
 */
public class Tools {
	public static String getComparableDate() {
		return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
	}
	public static String getPrettyDate() {
		return (new Date()).toString();
	}
	
	public enum IOResult {
		ErrorOpen,
		ErrorRead,
		ErrorWrite,
		Success
	}
	
	public static String readFile(String filePath) {
		SystemInfo sysinfo = SystemInfo.getInstance();
		StringBuilder contentBuilder = new StringBuilder();
		try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
			stream.forEach(s -> contentBuilder.append(s).append(sysinfo.newLine));
		}
		catch (IOException e) {
			return "?";
		}
		return contentBuilder.toString();
    }
	public static IOResult backupFile(String filename) {
		String old_contents = readFile(filename);
		if (old_contents.equals("?")) { return IOResult.ErrorRead; }
		
		File file = new File(filename + ".backup");
		FileWriter writer = null;
		
		try {
			file.createNewFile();
			writer = new FileWriter(file, false);
		}
		catch (IOException ex) {
			ex.printStackTrace();
			java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
			return IOResult.ErrorOpen;
		}
		
		try {
			writer.write(old_contents);
			writer.flush();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
			return IOResult.ErrorWrite;
		}
		return IOResult.Success;
	}
	
	public static boolean fileExists(String fileName) {
		File f = new File(fileName);
		return f.exists();
	}
	
	public static String getLockFileName(String basefile) {
		String folderTaskFile = Paths.get(basefile).getParent().toString();
		String baseFileName = Paths.get(basefile).getFileName().toString();
		return folderTaskFile + "/" + baseFileName + ".lock";
	}
}
