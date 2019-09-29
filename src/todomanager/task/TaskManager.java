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

import java.io.IOException;
import java.io.File;
import java.io.FileWriter;

import java.util.logging.Level;
import java.util.ArrayList;

import org.json.*;

import todomanager.util.Logger;
import todomanager.util.Tools;

/**
 * @brief Singleton class that is used to create new tasks.
 * 
 * It is also used to read/write from/into disk.
 * @author Lluís Alemany Puig
 */
public class TaskManager {
	
	private Logger log;
	private String task_file = "";
	
	private static TaskManager instance = null;
	private ArrayList<Task> high_prior_tasks = new ArrayList<>();
	private ArrayList<Task> med_prior_tasks = new ArrayList<>();
	private ArrayList<Task> low_prior_tasks = new ArrayList<>();
	private Integer num_tasks = 0;
	
	private String make_id() {
		String id = Integer.toString(num_tasks);
		int n_zeros = 6 - id.length();
		return (new String(new char[n_zeros]).replace('\0', '0')) + id;
	}
	
	private Task find_task(ArrayList<Task> ts, String id) {
		for (int i = 0; i < ts.size(); ++i) {
			if (ts.get(i).get_id().equals(id)) { 
				return ts.get(i);
			}
		}
		return null;
	}
	
	private int delete_task(ArrayList<Task> ts, String id) {
		int j = -1;
		Task t = null;
		// remove task from vector
		for (int i = 0; i < ts.size() && t == null; ++i) {
			if (ts.get(i).get_id().equals(id)) { 
				t = ts.get(i);
				j = i;
				ts.remove(i);
			}
		}
		if (t != null) {
			// if the task was removed, its subtasks are
			// also found in the same vector -> remove them
			for (Task st : t.get_subtasks()) {
				delete_task(ts, st.get_id());
			}
		}
		return j;
	}
	
	private void write_task(JSONWriter json, Task t) {
		json.object();
		
		json.key("id").value(t.get_id());
		json.key("name").value(t.get_name());
		json.key("description").value(t.get_description());
		json.key("comparable_date").value(t.get_comp_date());
		json.key("pretty_date").value(t.get_pretty_date());
		
		// write state changes
		json.key("changes");
		json.array();
			for (TaskState s : t.get_changes()) {
				json.object()
					.key("comparable_date").value(s.get_comp_date())
					.key("pretty_date").value(s.get_pretty_date())
					.key("reason_state").value(s.get_reason())
					.key("state").value(s.get_state())
					.endObject();
			}
		json.endArray();
		
		// write subtasks
		json.key("subtasks");
		json.array();
			t.get_subtasks().forEach((st) -> { write_task(json, st); });
		json.endArray();
		
		json.endObject();
	}
	
	private void append_tasks(JSONWriter json, ArrayList<Task> ts) {
		json.array();
		ts.forEach((t) -> { write_task(json, t); });
		json.endArray();
	}

	private TaskManager() {
		log = Logger.get_instance();
	}
	
	public static TaskManager get_instance() {
		if (instance == null) {
			instance = new TaskManager();
		}
		return instance;
	}
	
	public void set_task_file(String filename) { task_file = filename; }
	public String get_task_file() { return task_file; }
	
	private Task parse_task(JSONObject obj) {
		String id = obj.getString("id");
		String name = obj.getString("name");
		String descr = obj.getString("description");
		String comp_date = obj.getString("comparable_date");
		String pretty_date = obj.getString("pretty_date");
		Task t = new Task(id, name, descr, comp_date, pretty_date);
		
		JSONArray arrchanges = obj.getJSONArray("changes");
		ArrayList<TaskState> changes = new ArrayList<>();
		for (int i = 0; i < arrchanges.length(); ++i) {
			JSONObject stobj = (JSONObject) arrchanges.get(i);
			String cdate = stobj.getString("comparable_date");
			String pdate = stobj.getString("pretty_date");
			String reason = stobj.getString("reason_state");
			String state = stobj.getString("state");
			TaskState ts = new TaskState(cdate, pdate, reason, TaskStateEnum.fromString(state));
			changes.add(ts);
		}
		t.hard_set_changes(changes);
		
		JSONArray arrtasks = obj.getJSONArray("subtasks");
		ArrayList<Task> subtasks = new ArrayList<>();
		for (int i = 0; i < arrtasks.length(); ++i) {
			subtasks.add(parse_task( (JSONObject) arrtasks.get(i) ));
		}
		t.hard_set_subtasks(subtasks);
		return t;
	}
	private void parse_tasks(JSONArray arr, ArrayList<Task> tasks) {
		for (int i = 0; i < arr.length(); ++i) {
			Task t = parse_task((JSONObject) arr.get(i));
			tasks.add(t);
		}
	}
	public boolean read_tasks() {
		String lines_file = Tools.read_file(task_file);
		if (lines_file.equals("?")) {
			log.error("Could not open file '" + task_file + "'.");
			return false;
		}
		
		// clear current contents
		high_prior_tasks.clear();
		med_prior_tasks.clear();
		low_prior_tasks.clear();
		
		// main JSON object
		JSONObject main = new JSONObject(lines_file);
		// low, medium, and high priority tasks
		JSONArray low = main.getJSONArray("low_prior_tasks");
		JSONArray med = main.getJSONArray("med_prior_tasks");
		JSONArray high = main.getJSONArray("high_prior_tasks");
		
		parse_tasks(low, low_prior_tasks);
		parse_tasks(med, med_prior_tasks);
		parse_tasks(high, high_prior_tasks);
		return true;
	}
	
	public boolean write_tasks(boolean do_backup) {
		log.info("Writing tasks into file '" + task_file + "'.");
		if (do_backup) {
			log.info("    Do a backup first...");
		}
		
		if (do_backup && Tools.backup_file(task_file) != Tools.IOResult.Success) {
			log.error("Could not back up file '" + task_file + "'.");
			return false;
		}
		
		File file = new File(task_file);
		FileWriter writer = null;
		
		try {
			file.createNewFile();
			writer = new FileWriter(file, false);
		}
		catch (IOException ex) {
			ex.printStackTrace();
			java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
			log.error("Could not open file '" + task_file + "' for writing.");
			return false;
		}
		
		log.info("    file == null ? " + (file == null));
		log.info("    writer == null ? " + (writer == null));
		
		JSONWriter json = new JSONWriter(writer);
		json.object();
		json.key("low_prior_tasks");
		append_tasks(json, low_prior_tasks);
		json.key("med_prior_tasks");
		append_tasks(json, med_prior_tasks);
		json.key("high_prior_tasks");
		append_tasks(json, high_prior_tasks);
		json.endObject();
		
		try {
			writer.flush();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
			log.error("Could not write into file '" + task_file + "'");
			return false;
		}
		
		return true;
	}
	
	public ArrayList<Task> get_high_prior_tasks() { return high_prior_tasks; }
	public ArrayList<Task> get_med_prior_tasks()  { return med_prior_tasks; }
	public ArrayList<Task> get_low_prior_tasks()  { return low_prior_tasks; }

	public Task get_task(String id) {
		Task t = find_task(high_prior_tasks, id);
		if (t == null) { t = find_task(med_prior_tasks, id); }
		if (t == null) { t = find_task(low_prior_tasks, id); }
		return t;
	}
	
	public boolean delete_task(String id) {
		int i = delete_task(high_prior_tasks, id);
		if (i == -1) { i = delete_task(med_prior_tasks, id); }
		if (i == -1) { i = delete_task(low_prior_tasks, id); }
		return i != -1;
	}
	
	public int del_high_task(String id) { return delete_task(high_prior_tasks, id); }
	public int del_med_task(String id) { return delete_task(med_prior_tasks, id); }
	public int del_low_task(String id) { return delete_task(low_prior_tasks, id); }
	
	public void insert_high_task(int i, Task t) { high_prior_tasks.add(i, t); }
	public void insert_med_task(int i, Task t) { med_prior_tasks.add(i, t); }
	public void insert_low_task(int i, Task t) { low_prior_tasks.add(i, t); }
	
	public Task new_task(String name, String descr) {
		Task t = new Task(make_id(), name, descr, Tools.get_comp_date(), Tools.get_pretty_date());
		++num_tasks;
		return t;
	}
}
