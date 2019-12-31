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

package todomanager.task;

import java.util.ArrayList;
import todomanager.util.Translate;

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
	AddedSubtask;		// A subtask was added// A subtask was added
	
	public static String translateState(TaskStateEnum e) {
		Translate tr = Translate.getInstance();
		switch (e) {
			case Opened: return tr.state_Opened;
			case Done: return tr.state_Done;
			case Working: return tr.state_Working;
			case PutOnHold: return tr.state_PutOnHold;
			case Deleted: return tr.state_Deleted;
			case Cancelled: return tr.state_Cancelled;
			case OnRevision: return tr.state_OnRevision;
			case PendingRevision: return tr.state_PendingRevision;
			case Edited: return tr.state_Edited;
			case PriorityChanged: return tr.state_PriorityChanged;
			case AddedSubtask: return tr.state_AddedSubtask;
		}
		// should never happen
		return "**ERROR**";
	}
	
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
			case "AddedSubtask": return AddedSubtask;
			default:
				return Opened;
		}
	}
	
	/**
	 * @brief The states a task needs to be in for it to be changed to state 's'.
	 * @param s A task state
	 * @return Returns the preconditional states to state @e s.
	 */
	public static ArrayList<TaskStateEnum> precondCurtask(TaskStateEnum s) {
		ArrayList<TaskStateEnum> arr = new ArrayList<>();
		switch (s) {
			case Opened:
				arr.add(TaskStateEnum.Done);
				break;
			case Done:
				arr.add(TaskStateEnum.Working);
				arr.add(TaskStateEnum.OnRevision);
				break;
			case Working:
				arr.add(TaskStateEnum.Opened);
				arr.add(TaskStateEnum.OnRevision);
				arr.add(TaskStateEnum.PutOnHold);
				arr.add(TaskStateEnum.Done);
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
	
	/**
	 * @brief The states that the subtasks of a task need to be in for
	 * the task to be changed to state 's'.
	 * @param s A task state
	 * @return Returns the preconditional states to state @e s.
	 */
	public static ArrayList<TaskStateEnum> precondAskStateChangeSubtasks(TaskStateEnum s) {
		ArrayList<TaskStateEnum> arr = new ArrayList<>();
		switch (s) {
			case Opened:
				// no preconditions, so add all stats
				arr.add(TaskStateEnum.Opened);
				arr.add(TaskStateEnum.Done);
				arr.add(TaskStateEnum.Working);
				arr.add(TaskStateEnum.PutOnHold);
				arr.add(TaskStateEnum.Deleted);
				arr.add(TaskStateEnum.Cancelled);
				arr.add(TaskStateEnum.OnRevision);
				arr.add(TaskStateEnum.PendingRevision);
				arr.add(TaskStateEnum.Edited);
				arr.add(TaskStateEnum.PriorityChanged);
				arr.add(TaskStateEnum.AddedSubtask);
				break;
			case Done:
				arr.add(TaskStateEnum.Done);
				arr.add(TaskStateEnum.Cancelled);
				arr.add(TaskStateEnum.Deleted);
				break;
			case Working:
				arr.add(TaskStateEnum.Done);
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
	
	/**
	 * @brief The states a subtask needs to be in for the subtask to be
	 * changed to state 's'.
	 * @param s A task state
	 * @return Returns the preconditional states to state @e s.
	 */
	public static ArrayList<TaskStateEnum> precondStateChangeSubtasks(TaskStateEnum s) {
		ArrayList<TaskStateEnum> arr = new ArrayList<>();
		switch (s) {
			case Opened:
				// do not change
				break;
			case Done:
				arr.add(TaskStateEnum.Done);
				arr.add(TaskStateEnum.Cancelled);
				arr.add(TaskStateEnum.Deleted);
				break;
			case Working:
				//arr.add(TaskStateEnum.Done);
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
