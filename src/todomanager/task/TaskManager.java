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
	
	private final Logger log;
	private String tasksFile = "";
	
	private static TaskManager instance = null;
	private final ArrayList<Task> highPriorTasks;
	private final ArrayList<Task> medPriorTasks;
	private final ArrayList<Task> lowPriorTasks;
	private Integer numTasks;
	
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
		return j;
	}
	
	private TaskManager() {
		log = Logger.getInstance();
		highPriorTasks = new ArrayList<>();
		medPriorTasks = new ArrayList<>();
		lowPriorTasks = new ArrayList<>();
		numTasks = 0;
	}
	
	public static TaskManager getInstance() {
		if (instance == null) {
			instance = new TaskManager();
		}
		return instance;
	}
	
	public void setTaskFile(String filename) { tasksFile = filename; }
	public String getTaskFile() { return tasksFile; }
	
	private String __ifHasKeyReturnString(JSONObject obj, String k) {
		if (obj.has(k)) { return obj.getString(k); }
		return null;
	}
	
	private Task fromJSONtoTask(JSONObject obj) {
		String id = obj.getString("id");
		String name = obj.getString("name");
		String descr = obj.getString("description");
		String comp_date = obj.getString("comparable_date");
		String pretty_date = obj.getString("pretty_date");
		
		JSONArray arrchanges = obj.getJSONArray("changes");
		ArrayList<TaskState> changes = new ArrayList<>();
		for (int i = 0; i < arrchanges.length(); ++i) {
			JSONObject stobj = (JSONObject) arrchanges.get(i);
			String cdate = stobj.getString("comparable_date");
			String pdate = stobj.getString("pretty_date");
			String reason = stobj.getString("reason");
			String state = stobj.getString("state");
			String author = __ifHasKeyReturnString(stobj, "author");
			String pTN = __ifHasKeyReturnString(stobj, "pTN");
			String nTN = __ifHasKeyReturnString(stobj, "nTN");
			String pTD = __ifHasKeyReturnString(stobj, "pTD");
			String nTD = __ifHasKeyReturnString(stobj, "nTD");
			TaskState ts = new TaskState(
				author, cdate, pdate, reason,
				pTN, nTN, pTD, nTD,
				TaskStateEnum.fromString(state)
			);
			changes.add(ts);
		}
		
		String creator = changes.get(0).getAuthor();
		Task t = new Task(creator, id, name, descr, comp_date, pretty_date);
		t.hardSetChanges(changes);
		
		JSONArray arrtasks = obj.getJSONArray("subtasks");
		ArrayList<Task> subtasks = new ArrayList<>();
		for (int i = 0; i < arrtasks.length(); ++i) {
			subtasks.add(fromJSONtoTask( (JSONObject) arrtasks.get(i) ));
		}
		t.hardSetSubtasks(subtasks);
		numTasks += subtasks.size();
		return t;
	}
	private void parseTasks(JSONArray arr, ArrayList<Task> tasks) {
		for (int i = 0; i < arr.length(); ++i) {
			Task t = fromJSONtoTask((JSONObject) arr.get(i));
			tasks.add(t);
		}
		numTasks += tasks.size();
	}
	public boolean readTasks() {
		log.info("Reading tasks from file '" + tasksFile + "'");
		
		String lines_file = Tools.readFile(tasksFile);
		if (lines_file.equals("?")) {
			log.error("Could not open file '" + tasksFile + "'.");
			return false;
		}
		
		// clear current contents
		highPriorTasks.clear();
		medPriorTasks.clear();
		lowPriorTasks.clear();
		numTasks = 0;
		
		// main JSON object
		JSONObject main = new JSONObject(lines_file);
		// low, medium, and high priority tasks
		JSONArray low = main.getJSONArray("low_prior_tasks");
		JSONArray med = main.getJSONArray("med_prior_tasks");
		JSONArray high = main.getJSONArray("high_prior_tasks");
		
		parseTasks(low, lowPriorTasks);
		parseTasks(med, medPriorTasks);
		parseTasks(high, highPriorTasks);
		
		// set the parent of each task appropriately
		lowPriorTasks.forEach((t) -> { t.constructParentRelationships(); });
		medPriorTasks.forEach((t) -> { t.constructParentRelationships(); });
		highPriorTasks.forEach((t) -> { t.constructParentRelationships(); });
		
		log.info("File '" + tasksFile + "' read successfully.");
		return true;
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
		t.getChanges().forEach((ts) -> {
			json.object()
				.key("comparable_date").value(ts.getComparableDate())
				.key("pretty_date").value(ts.getPrettyDate())
				.key("reason").value(ts.getReason())
				.key("state").value(ts.getState())
				.key("author").value(ts.getAuthor())
				.key("pTN").value(ts.getPreviousTaskName() == null ? "" : ts.getPreviousTaskName())
				.key("nTN").value(ts.getNextTaskName() == null ? "" : ts.getNextTaskName())
				.key("pTD").value(ts.getPreviousTaskDescription() == null ? "" : ts.getPreviousTaskDescription())
				.key("nTD").value(ts.getNextTaskDescription() == null ? "" : ts.getNextTaskDescription())
			.endObject();
		});
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
	public boolean writeTasks(boolean do_backup) {
		log.info("Writing tasks into file '" + tasksFile + "'.");
		if (do_backup) {
			log.info("    Do a backup first...");
			if (Tools.backupFile(tasksFile) != Tools.IOResult.Success) {
				log.error("    Could not back up file '" + tasksFile + "'.");
				return false;
			}
		}
		
		File file = new File(tasksFile);
		FileWriter writer = null;
		
		try {
			file.createNewFile();
			writer = new FileWriter(file, false);
		}
		catch (IOException ex) {
			ex.printStackTrace();
			java.util.logging.Logger.getLogger(Logger.class.getName()).log(Level.SEVERE, null, ex);
			log.error("Could not open file '" + tasksFile + "' for writing.");
			return false;
		}
		
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
			log.error("Could not write into file '" + tasksFile + "'.");
			return false;
		}
		log.info("Tasks written into file '" + tasksFile + "' successfully.");
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
		int i = deleteTask(highPriorTasks, id);
		if (i == -1) { i = deleteTask(medPriorTasks, id); }
		if (i == -1) { i = deleteTask(lowPriorTasks, id); }
		return i != -1;
	}
	
	public int deleteHighTask(String id) { return TaskManager.this.deleteTask(highPriorTasks, id); }
	public int deleteMedTask(String id) { return TaskManager.this.deleteTask(medPriorTasks, id); }
	public int deleteLowTask(String id) { return TaskManager.this.deleteTask(lowPriorTasks, id); }
	
	public void insertHighTask(int i, Task t) { highPriorTasks.add(i, t); }
	public void insertMedTask(int i, Task t) { medPriorTasks.add(i, t); }
	public void insertLowTask(int i, Task t) { lowPriorTasks.add(i, t); }
	
	public Task newTask(String creator, String taskName, String taskDescr) {
		Task t = new Task(
			creator, makeId(),
			taskName, taskDescr,
			Tools.getComparableDate(), Tools.getPrettyDate()
		);
		++numTasks;
		return t;
	}
}
