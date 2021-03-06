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
import java.util.logging.Level;
import java.util.Date;

/**
 * @brief Singleton class to output information to the application's log file.
 * @author Lluís Alemany Puig
 */
public class Logger {
	
	/** Instance of the logger. */
	private static Logger instance = null;
	/** Name of the file where to store the log messages. */
	private String loggerFile;
	/** Object used to write into file. */
	private FileWriter fileWriter;
	
	public void open() {
		SystemInfo sysinfo = SystemInfo.getInstance();
		if (sysinfo.isUnix() || sysinfo.isMac()) {
			loggerFile = "/tmp/todomanager.log";
		}
		else if (sysinfo.isWindows()) {
			loggerFile = sysinfo.userDir + "/todomanager.log";
		}
		else {
			System.out.println("Logger error:");
			System.out.println("    OS '" + sysinfo.OS + "'.");
			System.out.println("    Do not know what file to open for logger");
			loggerFile = "something.log";
		}
		
		File file = null;
		try {
			file = new File(loggerFile);
			file.createNewFile();
			fileWriter = new FileWriter(file, true);
		}
		catch (IOException ex) {
			ex.printStackTrace();
			java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	private String date() {
		return "[" + (new Date()).toString() + "] ";
	}
	
	private Logger() {
		open();
	}
	
	public static Logger getInstance() {
		if (instance == null) {
			instance = new Logger();
		}
		return instance;
	}
	
	public Logger printSystemInformation() {
		SystemInfo sysinfo = SystemInfo.getInstance();
		info("System information:");
		info("    Program executed from path: " + sysinfo.userDir);
		info("    Operative System: " + sysinfo.OS);
		info("    Language: " + sysinfo.langRaw);
		return this;
	}
	
	public Logger begin() {
		try {
			fileWriter.write("<---- Start log ---->" + SystemInfo.getInstance().newLine);
			fileWriter.flush();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
		}
		return this;
	}
	public Logger end() {
		try {
			fileWriter.write("<----  End  log ---->" + SystemInfo.getInstance().newLine);
			fileWriter.flush();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
		}
		return this;
	}
	public Logger warning(String msg) {
		try {
			fileWriter.write(date() + "WARNING " + msg + SystemInfo.getInstance().newLine);
			fileWriter.flush();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
		}
		return this;
	}
	public Logger error(String msg) {
		try {
			fileWriter.write(date() + "  ERROR " + msg + SystemInfo.getInstance().newLine);
			fileWriter.flush();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
		}
		return this;
	}
	public Logger info(String msg) {
		try {
			fileWriter.write(date() + "   INFO " + msg + SystemInfo.getInstance().newLine);
			fileWriter.flush();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
		}
		return this;
	}
}
