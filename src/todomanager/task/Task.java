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

import todomanager.util.SystemInfo;
import todomanager.util.Tools;
import todomanager.util.Logger;

/**
 * @brief Class implementing a task.
 * @author Lluís Alemany Puig
 */
public class Task {
	
	private void addChangeState(String cdate, String pdate, String why, TaskStateEnum s) {
		changes.add(new TaskState(cdate, pdate, why, s));
	}
		
	/// Task's id (used to uniquely identify the task)
	private String id = "";
	/// Task's name
	private String name = "";
	/// Task's description
	private String description = "";
	/// Task's creation date (comparable)
	private String compDate = "";
	/// Task's creation date (pretty)
	private String prettyDate = "";
	/// List of state changes of this task
	private ArrayList<TaskState> changes = new ArrayList<>();
	/// The subtasks of this task
	private ArrayList<Task> subtasks = new ArrayList<>();
	/// Parent task
	private Task parentTask = null;
	
	public Task(String _id, String _name, String descr, String _cdate, String _pdate) {
		id = _id;
		name = _name;
		description = descr;
		compDate = _cdate;
		prettyDate = _pdate;
		
		changes = new ArrayList<>();
		subtasks = new ArrayList<>();
		parentTask = null;
		
		addChangeState(_cdate, _pdate, "Opened task", TaskStateEnum.Opened);
	}
	
	public String getId() { return id; }
	public String getName() { return name; }
	public void setName(String n) { name = n; }
	public String getDescription() { return description; }
	public void setDescription(String d) { description = d; }
	public String getCompDate() { return compDate; }
	public String getPrettyDate() { return prettyDate; }
	public ArrayList<TaskState> getChanges() { return changes; }
	public ArrayList<Task> getSubtasks() { return subtasks; }
	public Task getParent() { return parentTask; }
	@Override
	public String toString() { return name + " -- (id: " + getId() + ")"; }
	
	public void setParent(Task t) { parentTask = t; }
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
	
	/**
	 * Are all the substasks in a certain state?
	 * @param ls List of task states
	 */
	public boolean subtasksStateIsOneOf(ArrayList<TaskStateEnum> ls) {
		if (subtasks.size() == 0) { return true; }
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
		
		SystemInfo sysinfo = SystemInfo.getInstance();
		Logger log = Logger.getInstance();
		
		ArrayList<TaskStateEnum> cur_level = TaskStateEnum.precondCurtask(s);
		ArrayList<TaskStateEnum> sub_level = TaskStateEnum.precondSubtasks(s);
		
		if (!isOneOfState(cur_level)) {
			String r = "The state of task " + getId() + " is none of: " + cur_level;
			r += ". Its state is: " + currentState().getState() + "." + sysinfo.new_line;
			log.warning(r);
			return r;
		}
		
		String reason = "";
		for (Task t : subtasks) {
			if (!t.isOneOfState(sub_level)) {
				String r = 
					"Task " + t.getId() + " (subtask of " + getId() + "), " +
					"is not in any of the states: " + sub_level + sysinfo.new_line;
				reason += r;
				log.warning(r);
			}
		}
		return reason;
	}
	
	// -------------------------------------------------------------------------
	
	private void changeState(String cdate, String pdate, String why, TaskStateEnum s) {
		addChangeState(cdate, pdate, why, s);
		switch (s) {
			case Edited:
			case PriorityChanged:
			case AddedSubtask:
				return;
		}
		
		ArrayList<TaskStateEnum> cur_level = TaskStateEnum.precondCurtask(s);
		for (Task t : subtasks) {
			if (t.isOneOfState(cur_level)) {
				t.changeState(cdate, pdate, why, s);
			}
		}
	}
	public void changeState(String why, TaskStateEnum s) {
		String cdate = Tools.getComparableDate();
		String pdate = Tools.getPrettyDate();
		Task.this.changeState(cdate, pdate, why, s);
	}
	
	// -------------------------------------------------------------------------
	
	public void addSubtask(Task t) {
		if (isDone()) {
			addChangeState(t.getCompDate(),
				t.getPrettyDate(),
				"A subtask was added.",
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
		SystemInfo sysinfo = SystemInfo.getInstance();
		String c = "";
		for (TaskState ts : changes) {
			c += ts.getPrettyDate() + sysinfo.new_line;
			switch (ts.getState()) {
				case Edited:
				case PriorityChanged:
				case AddedSubtask:
					c += "    " + ts.getReason() + sysinfo.new_line;
					break;
				default:
					c += "    State of task set to: " + ts.getState() + sysinfo.new_line;
					c += "    Reason: " + ts.getReason() + sysinfo.new_line;
			}
		}
		return c;
	}
}
