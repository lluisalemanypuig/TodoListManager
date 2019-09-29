/*********************************************************************
 *
 *  TodoListManager - Open-source Managing of todo lists
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
 *  along with Treeler.  If not, see <http://www.gnu.org/licenses/>.
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
	
	private static Logger instance = null;
	private String logger_file;
	private boolean started_line;
	private int n_prints;
	private File file;
	private FileWriter file_writer;
	
	public void open() {
		started_line = false;
		n_prints = 0;
		
		SystemInfo sysinfo = SystemInfo.get_instance();
		if (sysinfo.is_Unix() || sysinfo.is_Mac()) {
			logger_file = "/tmp/todomanager.log";
		}
		else if (SystemInfo.get_instance().is_Windows()) {
			logger_file = "C:\\todomanager.log";
		}
		else {
			System.out.println("Logger error:");
			System.out.println("    OS '" + sysinfo.OS + "'.");
			System.out.println("    Do not know what file to open for logger");
		}
		
		file = new File(logger_file);
		try {
			file.createNewFile();
			file_writer = new FileWriter(file, true);
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
	
	public static Logger get_instance() {
		if (instance == null) {
			instance = new Logger();
		}
		return instance;
	}
	
	public Logger begin() {
		try {
			file_writer.write("<---- Start log ---->" + SystemInfo.get_instance().new_line);
			file_writer.flush();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
		}
		return this;
	}
	public Logger end() {
		try {
			file_writer.write("<----  End  log ---->" + SystemInfo.get_instance().new_line);
			file_writer.flush();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
		}
		return this;
	}
	private Logger print(String msg) {
		try {
			if (!started_line) {
				file_writer.write(date() + " " + msg);
				started_line = true;
			}
			else {
				file_writer.write(msg);
			}
			
			// a slightly more controlled flush
			n_prints += 1;
			if (n_prints%10 == 0) {
				file_writer.flush();
				n_prints = 0;
			}
		}
		catch (IOException ex) {
			java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
		}
		return this;
	}
	private Logger println(String msg) {
		try {
			if (!started_line) {
				file_writer.write(date() + " " + msg + SystemInfo.get_instance().new_line);
			}
			else {
				file_writer.write(" " + msg + SystemInfo.get_instance().new_line);
			}
			file_writer.flush();
			started_line = false;
		}
		catch (IOException ex) {
			ex.printStackTrace();
			java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
		}
		return this;
	}
	
	public Logger warning(String msg) {
		try {
			file_writer.write(date() + "WARNING " + msg + SystemInfo.get_instance().new_line);
			file_writer.flush();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
		}
		return this;
	}
	public Logger error(String msg) {
		try {
			file_writer.write(date() + "  ERROR " + msg + SystemInfo.get_instance().new_line);
			file_writer.flush();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
		}
		return this;
	}
	public Logger info(String msg) {
		try {
			file_writer.write(date() + "   INFO " + msg + SystemInfo.get_instance().new_line);
			file_writer.flush();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
		}
		return this;
	}
}
