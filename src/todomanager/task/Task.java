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
	
	private void add_change_state(String cdate, String pdate, String why, TaskStateEnum s) {
		changes.add(new TaskState(cdate, pdate, why, s));
	}
		
	/// Task's id (used to uniquely identify the task)
	private String id = "";
	/// Task's name
	private String name = "";
	/// Task's description
	private String description = "";
	/// Task's creation date (comparable)
	private String comp_date = "";
	/// Task's creation date (pretty)
	private String pretty_date = "";
	/// List of state changes of this task
	private ArrayList<TaskState> changes = new ArrayList<>();
	/// The subtasks of this task
	private ArrayList<Task> subtasks = new ArrayList<>();
	/// Parent task
	private Task parent_task = null;
	
	public Task(String _id, String _name, String descr, String _cdate, String _pdate) {
		id = _id;
		name = _name;
		description = descr;
		comp_date = _cdate;
		pretty_date = _pdate;
		
		changes = new ArrayList<>();
		subtasks = new ArrayList<>();
		parent_task = null;
		
		add_change_state(_cdate, _pdate, "Opened task", TaskStateEnum.Opened);
	}
	
	public String get_id() { return id; }
	public String get_name() { return name; }
	public void set_name(String n) { name = n; }
	public String get_description() { return description; }
	public void set_description(String d) { description = d; }
	public String get_comp_date() { return comp_date; }
	public String get_pretty_date() { return pretty_date; }
	public ArrayList<TaskState> get_changes() { return changes; }
	public ArrayList<Task> get_subtasks() { return subtasks; }
	public Task get_parent() { return parent_task; }
	@Override
	public String toString() { return name + " -- (id: " + get_id() + ")"; }
	
	public void set_parent(Task t) { parent_task = t; }
	public void hard_set_changes(ArrayList<TaskState> c) { changes = c; }
	public void hard_set_subtasks(ArrayList<Task> s) { subtasks = s; }
	
	public TaskState current_state() {
		ArrayList<TaskStateEnum> null_states = new ArrayList<>();
		null_states.add(TaskStateEnum.Edited);
		null_states.add(TaskStateEnum.AddedSubtask);
		null_states.add(TaskStateEnum.PriorityChanged);
		int i = changes.size() - 1;
		while (i >= 0 && null_states.contains(changes.get(i).get_state())) { --i; }
		return changes.get(i);
	}
	
	/**
	 * Are all the substasks in a certain state?
	 * @param ls List of task states
	 */
	public boolean subtasks_state_is_one_of(ArrayList<TaskStateEnum> ls) {
		if (subtasks.size() == 0) { return true; }
		return subtasks.stream().allMatch( (t) -> (t.is_one_of_state(ls)) );
	}
	/**
	 * A task is in a state only if it is marked to be in that state
	 * and so are all of its subtasks.
	 * @param ls List of task states
	 */
	public boolean is_one_of_state(ArrayList<TaskStateEnum> ls) {
		return ls.contains(current_state().get_state());
	}
	
	public boolean is_done() {
		return current_state().get_state() == TaskStateEnum.Done;
	}
	
	// -------------------------------------------------------------------------
	
	public String ask_change_state(TaskStateEnum s) {
		// always "yes" for these changes
		switch (s) {
			case Edited:
			case PriorityChanged:
			case AddedSubtask:
				return "";
		}
		
		Logger log = Logger.get_instance();
		ArrayList<TaskStateEnum> cur_level = TaskStateEnum.precond_curtask(s);
		ArrayList<TaskStateEnum> sub_level = TaskStateEnum.precond_subtasks(s);
		
		if (!is_one_of_state(cur_level)) {
			String r = "The state of task " + get_id() + " is none of: " + cur_level;
			r += ". Its state is: " + current_state().get_state() + ".";
			log.warning(r);
			return r;
		}
		
		String reason = "";
		for (Task t : subtasks) {
			if (!t.is_one_of_state(sub_level)) {
				String r = 
					"Task " + t.get_id() + " (subtask of " + get_id() + "), " +
					"is not in any of the states: " + sub_level;
				reason += r;
				log.warning(r);
			}
		}
		return reason;
	}
	
	// -------------------------------------------------------------------------
	
	private void change_state(String cdate, String pdate, String why, TaskStateEnum s) {
		add_change_state(cdate, pdate, why, s);
		switch (s) {
			case Edited:
			case PriorityChanged:
			case AddedSubtask:
				return;
		}
		
		ArrayList<TaskStateEnum> cur_level = TaskStateEnum.precond_curtask(s);
		for (Task t : subtasks) {
			if (t.is_one_of_state(cur_level)) {
				t.change_state(cdate, pdate, why, s);
			}
		}
	}
	public void change_state(String why, TaskStateEnum s) {
		String cdate = Tools.get_comp_date();
		String pdate = Tools.get_pretty_date();
		change_state(cdate, pdate, why, s);
	}
	
	// -------------------------------------------------------------------------
	
	public void add_subtask(Task t) {
		if (is_done()) {
			add_change_state(
				t.get_comp_date(),
				t.get_pretty_date(),
				"A subtask was added.",
				TaskStateEnum.Opened
			);
		}
		subtasks.add(0, t);
		t.parent_task = this;
	}
	
	public boolean move_subtask_by(String id, int incr) {
		Task t = null;
		int j = -1;
		for (int i = 0; i < subtasks.size() && t == null; ++i) {
			if (subtasks.get(i).get_id().equals(id)) {
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
	
	public String changes_to_string() {
		SystemInfo sysinfo = SystemInfo.get_instance();
		String c = "";
		for (TaskState ts : changes) {
			c += ts.get_pretty_date() + sysinfo.new_line;
			c += "    State of task set to: " + ts.get_state() + sysinfo.new_line;
			c += "    Reason: " + ts.get_reason() + sysinfo.new_line;
		}
		return c;
	}
}
