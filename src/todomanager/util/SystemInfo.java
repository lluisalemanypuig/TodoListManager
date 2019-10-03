/*********************************************************************
 *
 *  TodoListManager - Open-source Manager of todo lists
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

	/** ISO name for the OS's language. */
	public String langRaw;
	/** Name of the OS. */
	public String OS;
	/** New line character (OS-dependent). */
	public String newLine;
	
	protected final void extractSystemInfo() {
		Locale locale = Locale.getDefault();
		langRaw = locale.getLanguage();
		
		// get OS type
		OS = System.getProperty("os.name").toLowerCase();
		if (isWindows()) {
			newLine = "\r\n";
		}
		else if (isUnix() || isMac()) {
			newLine = "\n";
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
		extractSystemInfo();
	}

	public static SystemInfo getInstance() {
		if (instance == null) {
			instance = new SystemInfo();
		}
		return instance;
	}
	
	public boolean isWindows() {
		return OS.contains("win");
	}
	public boolean isMac() {
		return OS.contains("mac");
	}
	public boolean isUnix() {
		return OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0;
	}
	public boolean isSolaris() {
		return OS.contains("sunos");
	}
} 
