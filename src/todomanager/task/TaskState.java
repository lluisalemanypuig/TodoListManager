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

import todomanager.util.SystemInfo;
import todomanager.util.Translate;

/**
 * @brief Class to represent a task's state.
 * @author Lluís Alemany Puig
 */
public class TaskState {
	
	/** Date of task's state change (comparable). */
	private final String compDate;
	/** Date of task's state change (pretty). */
	private final String prettyDate;
	/** Why did this task change state?. */
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
	/** Name of the change's author. */
	private final String authorName;
	
	/**
	 * @brief Sets the date, reason and state of the task's state change.
	 * @param a Name of the task's author
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
		String a,
		String cdate, String pdate, String why,
		String pTN, String nTN, String pTD, String nTD,
		TaskStateEnum s
	)
	{
		authorName = a;
		compDate = cdate;
		prettyDate = pdate;
		reason = why;
		state = s;
		prevTaskName = pTN;
		nextTaskName = nTN;
		prevTaskDescr = pTD;
		nextTaskDescr = nTD;
	}
	
	public String getAuthor() { return authorName; }
	public String getComparableDate() { return compDate; }
	public String getPrettyDate() { return prettyDate; }
	public String getReason() { return reason; }
	public TaskStateEnum getState() { return state; }
	public String getPreviousTaskName() { return prevTaskName; }
	public String getNextTaskName() { return nextTaskName; }
	public String getPreviousTaskDescription() { return prevTaskDescr; }
	public String getNextTaskDescription() { return nextTaskDescr; }
	
	@Override
	public String toString() {
		String nL = SystemInfo.getInstance().newLine;
		Translate tr = Translate.getInstance();
		
		String msg = tr.stateChange_Date;
		msg = msg.replace("%s1", getPrettyDate()) + nL;
		
		switch (getState()) {
			case Edited:
				msg += "    " + tr.stateChange_Edited.replace("%s1", getAuthor()) + nL;
				break;
			case PriorityChanged:
				msg += "    " + tr.stateChange_PriorChanged.replace("%s1", getAuthor()) + nL;
				break;
			case AddedSubtask:
				msg += "    " + tr.stateChange_AddedSubtask.replace("%s1", getAuthor()) + nL;
				break;
			case Opened:
				msg += "    " + tr.stateChange_Opened.replace("%s1", getAuthor()) + nL;
				break;
			case Working:
				msg += "    " + tr.stateChange_Working.replace("%s1", getAuthor()) + nL;
				break;
			case Deleted:
				msg += "    " + tr.stateChange_Deleted.replace("%s1", getAuthor()) + nL;
				break;
			case PutOnHold:
				msg += "    " + tr.stateChange_PutOnHold.replace("%s1", getAuthor()) + nL;
				break;
			case Cancelled:
				msg += "    " + tr.stateChange_Cancelled.replace("%s1", getAuthor()) + nL;
				break;
			case OnRevision:
				msg += "    " + tr.stateChange_OnRevision.replace("%s1", getAuthor()) + nL;
				break;
			case PendingRevision:
				msg += "    " + tr.stateChange_PendingRevision.replace("%s1", getAuthor()) + nL;
				break;
			case Done:
				msg += "    " + tr.stateChange_Done.replace("%s1", getAuthor()) + nL;
				break;
		}
		
		switch (getState()) {
			case Opened:
			case Working:
			case Deleted:
			case PutOnHold:
			case Cancelled:
			case OnRevision:
			case PendingRevision:
			case Done:
				String stateTranslated = TaskStateEnum.translateState(getState());
				msg += "    " +
					tr.stateChange_StateSetTo.replace("%s1", stateTranslated) + nL;
		}
		
		switch (getState()) {
			case Deleted:
			case PutOnHold:
			case Cancelled:
			case PendingRevision:
				msg += "    " + tr.stateChange_Reason.replace("%s1", getReason()) + nL;
		}
		msg += nL;
		return msg;
	}
}
