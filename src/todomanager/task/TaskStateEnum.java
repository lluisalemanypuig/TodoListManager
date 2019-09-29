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

/**
 * @brief The different states a task can be in.
 * @author Lluís Alemany Puig
 */
public enum TaskStateEnum {
	Opened,				// The task has been created
	Done,				// The task was completed
	Working,			// Somebody is working on the task
	PutOnHold,			// The task has been put on hold (do not work on it, for now)
	Deleted,			// The task has been deleted (after completing it)
	Cancelled,			// The task has been deleted (before completing it)
	OnRevision,			// The task is being revised
	PendingRevision,	// The task needs revision
	
	Edited,				// The name and or the description were edited
	PriorityChanged,	// The task priority changed
	SubtaskAdded;		// A subtask was added
	
	public static TaskStateEnum fromString(String s) {
		switch (s) {
			case "Opened": return Opened;
			case "Done": return Done;
			case "Working": return Working;
			case "PutOnHold": return PutOnHold;
			case "Deleted": return Deleted;
			case "Cancelled": return Cancelled;
			case "OnRevision": return OnRevision;
			case "PendingRevision": return PendingRevision;
			case "Edited": return Edited;
			case "PriorityChanged": return PriorityChanged;
			case "SubtaskAdded": return SubtaskAdded;
			default:
				return Opened;
		}
	}
}
