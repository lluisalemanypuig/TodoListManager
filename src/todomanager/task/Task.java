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

import todomanager.util.SystemInfo;
import todomanager.util.Tools;
import todomanager.util.Logger;
import todomanager.util.Translate;

/**
 * @brief Class implementing a task.
 * @author Lluís Alemany Puig
 */
public class Task {
	
	private void addChangeState(
		String authorName,
		String cdate, String pdate, String why,
		String pTN, String nTN, String pTD, String nTD,
		TaskStateEnum s
	)
	{
		changes.add(new TaskState(authorName, cdate, pdate, why, pTN, nTN, pTD, nTD, s));
	}
	
	/** Task's id (used to uniquely identify the task) */
	private String id = "";
	/** Task's name */
	private String name = "";
	/** Task's description */
	private String description = "";
	/** Task's creation date (comparable) */
	private String compDate = "";
	/** Task's creation date (pretty) */
	private String prettyDate = "";
	/** List of state changes of this task */
	private ArrayList<TaskState> changes = new ArrayList<>();
	/** The subtasks of this task */
	private ArrayList<Task> subtasks = new ArrayList<>();
	/** Parent task */
	private Task parentTask = null;
	
	/**
	 * Task constructor.
	 * @param _author Name of the person constructing the task.
	 * @param _id Task's id (job of the Task Manager to create an id)
	 * @param _name Task's name.
	 * @param descr Task's description.
	 * @param _cdate Date in comparable format.
	 * @param _pdate Date in a pretty format.
	 */
	public Task(String _author, String _id, String _name, String descr, String _cdate, String _pdate) {
		id = _id;
		name = _name;
		description = descr;
		compDate = _cdate;
		prettyDate = _pdate;
		
		changes = new ArrayList<>();
		subtasks = new ArrayList<>();
		parentTask = null;
		
		// the task's name and description changed from "nothing"
		// to "something". However, no need to capture this change
		addChangeState(_author, _cdate, _pdate, "Opened task", null, null, null, null, TaskStateEnum.Opened);
	}
	
	public String getCreator() { return changes.get(0).getAuthor(); }
	public String getId() { return id; }
	public String getName() { return name; }
	public void setName(String n) { name = n; }
	public String getDescription() { return description; }
	public void setDescription(String d) { description = d; }
	public String getCompDate() { return compDate; }
	public String getPrettyDate() { return prettyDate; }
	public ArrayList<TaskState> getChanges() { return changes; }
	public ArrayList<Task> getSubtasks() { return subtasks; }
	public void setParent(Task t) { parentTask = t; }
	public Task getParentTask() { return parentTask; }
	
	@Override
	public String toString() { return name + " -- (id: " + getId() + ")"; }
	
	public void hardSetChanges(ArrayList<TaskState> c) { changes = c; }
	public void hardSetSubtasks(ArrayList<Task> s) { subtasks = s; }
	
	public TaskState currentState() {
		ArrayList<TaskStateEnum> null_states = new ArrayList<>();
		null_states.add(TaskStateEnum.Edited);
		null_states.add(TaskStateEnum.AddedSubtask);
		null_states.add(TaskStateEnum.PriorityChanged);
		int i = changes.size() - 1;
		while (i >= 0 && null_states.contains(changes.get(i).getState())) { --i; }
		return changes.get(i);
	}
	
	public boolean deleteSubtask(String id) {
		int j = -1;
		for (int i = 0; i < subtasks.size() && j == -1; ++i) {
			if (subtasks.get(i).getId().equals(id)) {
				j = i;
			}
		}
		if (j != -1) { subtasks.remove(j); }
		return j != -1;
	}
	
	public void deleteSubtasks() {
		for (Task st : getSubtasks()) {
			st.deleteSubtasks();
		}
		subtasks.clear();
	}
	
	public void constructParentRelationships() {
		ArrayList<Task> list = getSubtasks();
		list.stream().forEach((t) -> {
			t.setParent(this);
			t.constructParentRelationships();
		});
	}
	
	/**
	 * Are all the substasks in a certain state?
	 * @param ls List of task states
	 */
	public boolean subtasksStateIsOneOf(ArrayList<TaskStateEnum> ls) {
		if (subtasks.isEmpty()) { return true; }
		return subtasks.stream().allMatch( (t) -> (t.isOneOfState(ls)) );
	}
	/**
	 * A task is in a state only if it is marked to be in that state
	 * and so are all of its subtasks.
	 * @param ls List of task states
	 */
	public boolean isOneOfState(ArrayList<TaskStateEnum> ls) {
		return ls.contains(currentState().getState());
	}
	
	public boolean isDone() {
		return currentState().getState() == TaskStateEnum.Done;
	}
	
	// -------------------------------------------------------------------------
	
	public String askChangeState(TaskStateEnum s) {
		// always "yes" for these changes
		switch (s) {
			case Edited:
			case PriorityChanged:
			case AddedSubtask:
				return "";
		}
		
		Translate tr = Translate.getInstance();
		SystemInfo sysinfo = SystemInfo.getInstance();
		Logger log = Logger.getInstance();
		
		ArrayList<TaskStateEnum> cur_level = TaskStateEnum.precondCurtask(s);
		ArrayList<TaskStateEnum> sub_level = TaskStateEnum.precondAskStateChangeSubtasks(s);
		
		if (!isOneOfState(cur_level)) {
			// translate current level states' names
			ArrayList<String> cur_level_trans = new ArrayList<String>();
			cur_level.forEach((e) -> {
				cur_level_trans.add(TaskStateEnum.translateState(e));
			});
			String cur_state_trans = TaskStateEnum.translateState(currentState().getState());
			
			String msg = tr.error_TaskStateNoneOf;
			msg = msg.replace("%s1", getId());
			msg = msg.replace("%s2", cur_level_trans.toString());
			msg = msg.replace("%s3", cur_state_trans);
			msg += sysinfo.newLine;
			log.warning(msg);
			return msg;
		}
		
		String reason = "";
		for (Task t : subtasks) {
			if (!t.isOneOfState(sub_level)) {
				// translate sub level states' names
				ArrayList<String> sub_level_trans = new ArrayList<String>();
				sub_level.forEach((e) -> {
					sub_level_trans.add(TaskStateEnum.translateState(e));
				});
				
				String msg = tr.error_SubtaskStateNoneOf;
				msg = msg.replace("%s1", t.getId());
				msg = msg.replace("%s2", getId());
				msg = msg.replace("%s3", sub_level_trans.toString());
				msg += sysinfo.newLine;
				reason += msg;
				log.warning(msg);
			}
		}
		return reason;
	}
	
	// -------------------------------------------------------------------------
	
	/**
	 * @brief Change the state of a task.
	 * @param author Author's name
	 * @param cdate Comparable date
	 * @param pdate Pretty date
	 * @param why Reason of change
	 * @param prevName Previous name of the taks. If the change is not 'Edited',
	 * set it to null.
	 * @param prevDescr Previous description of the taks. If the change is not
	 * 'Edited', set it to null.
	 * @param s New task's state.
	 */
	private void changeState(
		String author,
		String cdate, String pdate, String why,
		String prevName, String prevDescr,
		TaskStateEnum s
	)
	{
		if (why == null) {
			why = "null";
		}
		switch (s) {
			case Edited:
				addChangeState(
					author, cdate, pdate, why,
					prevName, getName(), prevDescr, getDescription(),
					s
				);
				return;
			default:
				// no change of name or description
				addChangeState(author, cdate, pdate, why, null, null, null, null, s);
		}
		
		// in these cases, no more work to do
		switch (s) {
			case PriorityChanged:
			case AddedSubtask:
				return;
		}
		
		ArrayList<TaskStateEnum> cur_level = TaskStateEnum.precondStateChangeSubtasks(s);
		for (Task t : subtasks) {
			if (t.isOneOfState(cur_level)) {
				t.changeState(author, cdate, pdate, null,null, why, s);
			}
		}
	}
	/**
	 * Change the state of a task when @e s is not 'Edited'.
	 * @param author Author's name.
	 * @param why Reason of change
	 * @param s New state of the task
	 */
	public void changeState(String author, String why, TaskStateEnum s) {
		if (why == null) {
			why = "null";
		}
		String cdate = Tools.getComparableDate();
		String pdate = Tools.getPrettyDate();
		changeState(author, cdate, pdate, why, null,null, s);
	}
	
	// -------------------------------------------------------------------------
	
	/**
	 * Change the state of a task its name and/or description have been edited.
	 * @param author Author's name.
	 * @param why Reason of change.
	 * @param prevName Task's previous name.
	 * @param prevDescr Task's previous description.
	 * @param s New state of the task
	 */
	public void taskWasEdited(String author, String why, String prevName, String prevDescr, TaskStateEnum s) {
		if (why == null) {
			why = "null";
		}
		String cdate = Tools.getComparableDate();
		String pdate = Tools.getPrettyDate();
		changeState(author, cdate, pdate, why, prevName, prevDescr, s);
	}
	
	/**
	 * Add a subtask to this task.
	 * @param t The new task to be added.
	 */
	public void addSubtask(Task t) {
		if (isDone()) {
			addChangeState(
				t.getCreator(),
				t.getCompDate(),
				t.getPrettyDate(),
				Translate.getInstance().change_SubtaskAdded,
				null, null, null, null,
				TaskStateEnum.Opened
			);
		}
		subtasks.add(0, t);
		t.parentTask = this;
	}
	
	public boolean moveSubtaskBy(String id, int incr) {
		Task t = null;
		int j = -1;
		for (int i = 0; i < subtasks.size() && t == null; ++i) {
			if (subtasks.get(i).getId().equals(id)) {
				t = subtasks.get(i);
				j = i;
				subtasks.remove(i);
			}
		}
		if (t == null) { return false; }
		subtasks.add(j + incr, t);
		return true;
	}
	
	// -------------------------------------------------------------------------
	
	public String changesToString() {
		String c = "";
		c = changes.stream()
			.map(		(ts) -> ts.toString()	)
			.reduce(	c, String::concat		);
		return c;
	}
}
