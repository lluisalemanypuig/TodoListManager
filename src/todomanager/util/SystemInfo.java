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

import java.util.Locale;

/**
 * @brief A singleton class to gather information about the system (Operative System and language).
 * @author Lluís Alemany Puig
 */
public class SystemInfo {
	// static variable single_instance of type Singleton
	private static SystemInfo instance = null;

	/// ISO name for the OS's language.
	public String lang_raw;
	/// Name of the OS.
	public String OS;
	/// New line character (OS-dependent).
	public String new_line;
	
	protected final void extract_system_info() {
		Locale locale = Locale.getDefault();
		lang_raw = locale.getLanguage();
		
		// get OS type
		OS = System.getProperty("os.name").toLowerCase();
		if (is_Windows()) {
			new_line = "\r\n";
		}
		else if (is_Unix() || is_Mac()) {
			new_line = "\n";
		}
		else {
			System.out.println("SystemInfo error:");
			System.out.println("    Do not know how to process OS '" + OS + "'");
			System.out.println("    Do not know what character to use for an endline character.");
			System.out.println("    Please, report this error to developers.");
			assert(false);
		}
	}
	
	private SystemInfo() {
		extract_system_info();
	}

	public static SystemInfo get_instance() {
		if (instance == null) {
			instance = new SystemInfo();
		}
		return instance;
	}
	
	/// Is Windows?
	public boolean is_Windows() {
		return OS.contains("win");
	}
	/// Is Mac?
	public boolean is_Mac() {
		return OS.contains("mac");
	}
	/// Is Unix?
	public boolean is_Unix() {
		return OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0;
	}
	/// Is Solaris?
	public boolean is_Solaris() {
		return OS.contains("sunos");
	}
} 
