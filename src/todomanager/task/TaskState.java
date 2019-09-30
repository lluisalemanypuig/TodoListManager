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

package todomanager.task;

import java.util.ArrayList;

/**
 * @brief Class to represent a task's state.
 * @author Lluís Alemany Puig
 */
public class TaskState {
	
	/// Date of task's state change (comparable)
	private final String comp_date;
	/// Date of task's state change (pretty)
	private final String pretty_date;
	/// Why did this task change state?
	private final String reason;
	/// The new task's state.
	private final TaskStateEnum state;
	
	/**
	 * Sets the date, reason and state of the task's state change.
	 * @param cdate Date of state in format YYYY.MM.DD.HH.MM.SS.
	 * @param pdate Date of state in a prettier format.
	 * @param why Why did this happen (optional for 'Opened', 'Done', 'Working'
	 * @param s The new task's state.
	 */
	public TaskState(String cdate, String pdate, String why, TaskStateEnum s) {
		comp_date = cdate;
		pretty_date = pdate;
		reason = why;
		state = s;
	}
	
	public String get_comp_date() { return comp_date; }
	public String get_pretty_date() { return pretty_date; }
	public String get_reason() { return reason; }
	public TaskStateEnum get_state() { return state; }
	
	
}
