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
	private String comp_date;
	/// Date of task's state change (pretty)
	private String pretty_date;
	/// Why did this task change state?
	private String reason;
	/// The new task's state.
	private TaskStateEnum state;
	
	/**
	 * Sets the date, reason and state of the task's state change.
	 * @param date When did this happen
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
	
	/// The states a task needs to be in for it to be changed to state 's'.
	public static ArrayList<TaskStateEnum> precond_curtask(TaskStateEnum s) {
		ArrayList<TaskStateEnum> arr = new ArrayList<>();
		switch (s) {
			case Done:
				arr.add(TaskStateEnum.Working);
				arr.add(TaskStateEnum.OnRevision);
				break;
			case Working:
				arr.add(TaskStateEnum.Opened);
				arr.add(TaskStateEnum.OnRevision);
				arr.add(TaskStateEnum.PutOnHold);
				break;
			case PutOnHold:
				arr.add(TaskStateEnum.Working);
				break;
			case Deleted:
				arr.add(TaskStateEnum.Done);
				break;
			case Cancelled:
				arr.add(TaskStateEnum.Opened);
				arr.add(TaskStateEnum.Working);
				arr.add(TaskStateEnum.OnRevision);
				break;
			case OnRevision:
				arr.add(TaskStateEnum.PendingRevision);
				arr.add(TaskStateEnum.Working);
				break;
			case PendingRevision:
				arr.add(TaskStateEnum.Working);
				break;
		}
		return arr;
	}
	
	/// The states a task's subtasks need to be in for it to be changed to state 's'.
	public static ArrayList<TaskStateEnum> precond_subtasks(TaskStateEnum s) {
		ArrayList<TaskStateEnum> arr = new ArrayList<>();
		switch (s) {
			case Done:
				arr.add(TaskStateEnum.Done);
				arr.add(TaskStateEnum.Cancelled);
				arr.add(TaskStateEnum.Deleted);
				break;
			case Working:
				arr.add(TaskStateEnum.Opened);
				arr.add(TaskStateEnum.Working);
				arr.add(TaskStateEnum.OnRevision);
				arr.add(TaskStateEnum.PutOnHold);
				break;
			case PutOnHold:
				arr.add(TaskStateEnum.Working);
				arr.add(TaskStateEnum.PutOnHold);
				break;
			case Deleted:
				arr.add(TaskStateEnum.Done);
				arr.add(TaskStateEnum.Deleted);
				break;
			case Cancelled:
				arr.add(TaskStateEnum.Opened);
				arr.add(TaskStateEnum.Cancelled);
				arr.add(TaskStateEnum.Working);
				arr.add(TaskStateEnum.OnRevision);
				break;
			case OnRevision:
				arr.add(TaskStateEnum.OnRevision);
				arr.add(TaskStateEnum.PendingRevision);
				arr.add(TaskStateEnum.Working);
				break;
			case PendingRevision:
				arr.add(TaskStateEnum.Working);
				arr.add(TaskStateEnum.PendingRevision);
				break;
		}
		return arr;
	}
	
}
