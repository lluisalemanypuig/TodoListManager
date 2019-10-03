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

package todomanager.task;

/**
 * @brief Class to represent a task's state.
 * @author Lluís Alemany Puig
 */
public class TaskState {
	
	/** Date of task's state change (comparable) */
	private final String compDate;
	/** Date of task's state change (pretty) */
	private final String prettyDate;
	/** Why did this task change state? */
	private final String reason;
	/** The new task's state. */
	private final TaskStateEnum state;
	/** The task's name before the change. */
	private final String prevTaskName;
	/** The task's name after the change. */
	private final String nextTaskName;
	/** The task's description before the change. */
	private final String prevTaskDescr;
	/** The task's description after the change. */
	private final String nextTaskDescr;
	
	/**
	 * Sets the date, reason and state of the task's state change.
	 * @param cdate Date of state in format YYYY.MM.DD.HH.MM.SS.
	 * @param pdate Date of state in a prettier format.
	 * @param why Why did this happen (optional for 'Opened', 'Done', 'Working'
	 * @param pTN Task's name before the change.
	 * @param nTN Task's name after the change.
	 * @param pTD Task's description before the change.
	 * @param nTD Task's description after the change.
	 * @param s The new task's state.
	 */
	public TaskState(
		String cdate, String pdate, String why,
		String pTN, String nTN, String pTD, String nTD,
		TaskStateEnum s
	)
	{
		compDate = cdate;
		prettyDate = pdate;
		reason = why;
		state = s;
		prevTaskName = pTN;
		nextTaskName = nTN;
		prevTaskDescr = pTD;
		nextTaskDescr = nTD;
	}
	
	public String getComparableDate() { return compDate; }
	public String getPrettyDate() { return prettyDate; }
	public String getReason() { return reason; }
	public TaskStateEnum getState() { return state; }
	public String getPreviousTaskName() { return prevTaskName; }
	public String getNextTaskName() { return nextTaskName; }
	public String getPreviousTaskDescription() { return prevTaskDescr; }
	public String getNextTaskDescription() { return nextTaskDescr; }
}
