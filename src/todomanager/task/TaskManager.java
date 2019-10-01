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
	private String taskFile = "";
	
	private static TaskManager instance = null;
	private ArrayList<Task> highPriorTasks = new ArrayList<>();
	private ArrayList<Task> medPriorTasks = new ArrayList<>();
	private ArrayList<Task> lowPriorTasks = new ArrayList<>();
	private Integer numTasks = 0;
	
	private String makeId() {
		String id = Integer.toString(numTasks);
		int n_zeros = 6 - id.length();
		return (new String(new char[n_zeros]).replace('\0', '0')) + id;
	}
	
	private Task findTask(ArrayList<Task> ts, String id) {
		for (int i = 0; i < ts.size(); ++i) {
			if (ts.get(i).getId().equals(id)) { 
				return ts.get(i);
			}
		}
		return null;
	}
	
	private int deleteTask(ArrayList<Task> ts, String id) {
		int j = -1;
		Task t = null;
		// remove task from vector
		for (int i = 0; i < ts.size() && t == null; ++i) {
			if (ts.get(i).getId().equals(id)) { 
				t = ts.get(i);
				j = i;
				ts.remove(i);
			}
		}
		if (t != null) {
			// if the task was removed, its subtasks are
			// also found in the same vector -> remove them
			for (Task st : t.getSubtasks()) {
				TaskManager.this.deleteTask(ts, st.getId());
			}
		}
		return j;
	}
	
	private void taskToJSON(JSONWriter json, Task t) {
		json.object();
		
		json.key("id").value(t.getId());
		json.key("name").value(t.getName());
		json.key("description").value(t.getDescription());
		json.key("comparable_date").value(t.getCompDate());
		json.key("pretty_date").value(t.getPrettyDate());
		
		// write state changes
		json.key("changes");
		json.array();
			for (TaskState s : t.getChanges()) {
				json.object()
					.key("comparable_date").value(s.getComparableDate())
					.key("pretty_date").value(s.getPrettyDate())
					.key("reason_state").value(s.getReason())
					.key("state").value(s.getState())
					.endObject();
			}
		json.endArray();
		
		// write subtasks
		json.key("subtasks");
		json.array();
			t.getSubtasks().forEach((st) -> { taskToJSON(json, st); });
		json.endArray();
		
		json.endObject();
	}
	
	private void tasksToJSON(JSONWriter json, ArrayList<Task> ts) {
		json.array();
		ts.forEach((t) -> { taskToJSON(json, t); });
		json.endArray();
	}

	private TaskManager() {
		log = Logger.getInstance();
	}
	
	public static TaskManager get_instance() {
		if (instance == null) {
			instance = new TaskManager();
		}
		return instance;
	}
	
	public void setTaskFile(String filename) { taskFile = filename; }
	public String getTaskFile() { return taskFile; }
	
	private Task fromJSONtoTask(JSONObject obj) {
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
		t.hardSetChanges(changes);
		
		JSONArray arrtasks = obj.getJSONArray("subtasks");
		ArrayList<Task> subtasks = new ArrayList<>();
		for (int i = 0; i < arrtasks.length(); ++i) {
			subtasks.add(fromJSONtoTask( (JSONObject) arrtasks.get(i) ));
		}
		t.hardSetSubtasks(subtasks);
		return t;
	}
	private void parseTasks(JSONArray arr, ArrayList<Task> tasks) {
		for (int i = 0; i < arr.length(); ++i) {
			Task t = fromJSONtoTask((JSONObject) arr.get(i));
			tasks.add(t);
		}
	}
	public boolean readTasks() {
		String lines_file = Tools.readFile(taskFile);
		if (lines_file.equals("?")) {
			log.error("Could not open file '" + taskFile + "'.");
			return false;
		}
		
		// clear current contents
		highPriorTasks.clear();
		medPriorTasks.clear();
		lowPriorTasks.clear();
		
		// main JSON object
		JSONObject main = new JSONObject(lines_file);
		// low, medium, and high priority tasks
		JSONArray low = main.getJSONArray("low_prior_tasks");
		JSONArray med = main.getJSONArray("med_prior_tasks");
		JSONArray high = main.getJSONArray("high_prior_tasks");
		
		parseTasks(low, lowPriorTasks);
		parseTasks(med, medPriorTasks);
		parseTasks(high, highPriorTasks);
		return true;
	}
	
	public boolean writeTasks(boolean do_backup) {
		log.info("Writing tasks into file '" + taskFile + "'.");
		if (do_backup) {
			log.info("    Do a backup first...");
		}
		
		if (do_backup && Tools.backupFile(taskFile) != Tools.IOResult.Success) {
			log.error("Could not back up file '" + taskFile + "'.");
			return false;
		}
		
		File file = new File(taskFile);
		FileWriter writer = null;
		
		try {
			file.createNewFile();
			writer = new FileWriter(file, false);
		}
		catch (IOException ex) {
			ex.printStackTrace();
			java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
			log.error("Could not open file '" + taskFile + "' for writing.");
			return false;
		}
		
		log.info("    file == null ? " + (file == null));
		log.info("    writer == null ? " + (writer == null));
		
		JSONWriter json = new JSONWriter(writer);
		json.object();
		json.key("low_prior_tasks");
		tasksToJSON(json, lowPriorTasks);
		json.key("med_prior_tasks");
		tasksToJSON(json, medPriorTasks);
		json.key("high_prior_tasks");
		tasksToJSON(json, highPriorTasks);
		json.endObject();
		
		try {
			writer.flush();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
			log.error("Could not write into file '" + taskFile + "'");
			return false;
		}
		
		return true;
	}
	
	public ArrayList<Task> getHighPriorTasks() { return highPriorTasks; }
	public ArrayList<Task> getMedPriorTasks()  { return medPriorTasks; }
	public ArrayList<Task> getLowPriorTasks()  { return lowPriorTasks; }

	public Task getTask(String id) {
		Task t = findTask(highPriorTasks, id);
		if (t == null) { t = findTask(medPriorTasks, id); }
		if (t == null) { t = findTask(lowPriorTasks, id); }
		return t;
	}
	
	public boolean deleteTask(String id) {
		int i = TaskManager.this.deleteTask(highPriorTasks, id);
		if (i == -1) { i = TaskManager.this.deleteTask(medPriorTasks, id); }
		if (i == -1) { i = TaskManager.this.deleteTask(lowPriorTasks, id); }
		return i != -1;
	}
	
	public int deleteHighTask(String id) { return TaskManager.this.deleteTask(highPriorTasks, id); }
	public int deleteMedTask(String id) { return TaskManager.this.deleteTask(medPriorTasks, id); }
	public int deleteLowTask(String id) { return TaskManager.this.deleteTask(lowPriorTasks, id); }
	
	public void insertHighTask(int i, Task t) { highPriorTasks.add(i, t); }
	public void insertMedTask(int i, Task t) { medPriorTasks.add(i, t); }
	public void insertLowTask(int i, Task t) { lowPriorTasks.add(i, t); }
	
	public Task newTask(String name, String descr) {
		Task t = new Task(makeId(), name, descr, Tools.getComparableDate(), Tools.getPrettyDate());
		++numTasks;
		return t;
	}
}
