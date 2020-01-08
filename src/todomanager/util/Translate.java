/** *******************************************************************
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
 * Contact: Lluís Alemany Puig (lluis.alemany.puig@gmail.com)
 *
 *******************************************************************
 */
package todomanager.util;

import org.json.JSONObject;

/**
 *
 * @author lluis
 */
public class Translate {
	
	public String menuItemFile;
	public String menuItemNewTaskFile;
	public String menuItemOpenFile;
	public String menuItemSaveTasks;
	public String menuItemSaveTasksAs;
	public String menuItemExit;
	
	public String menuItemHelp;
	public String menuItemAbout;
	public String menuItemTranslations;
	
	public String buttonNewTaskFile;
	public String buttonOpenTasks;
	public String buttonSaveTasks;
	public String buttonSaveTasksAs;
	public String buttonSetAuthor;
	public String labelAuthorName;
	public String labelUnsavedChanges;
	
	public String highPriorNode;
	public String medPriorNode;
	public String lowPriorNode;
	
	public String labelTaskName;
	public String labelTaskAuthor;
	public String labelTaskDate;
	public String labelTaskDescription;
	public String labelTaskHistory;
	public String labelTaskStateText;
	
	public String buttonNewTask;
	public String buttonRemove;
	public String buttonUp;
	public String buttonDown;
	public String buttonPriority;
	public String buttonShowAll;
	public String buttonHideAll;
	
	public String buttonTaskDone;
	public String buttonTaskWorking;
	public String buttonTaskHold;
	public String buttonTaskOnRevision;
	public String buttonTaskPendingRevision;
	public String buttonTaskEdit;
	public String buttonTaskCancel;
	public String buttonTaskDelete;
	public String buttonTaskClear;
	public String buttonTaskReopen;
	
	public String error_FileIsLocked;
	public String error_CouldNotOpenFile;
	public String error_CouldNotDetermineNodePrior;
	public String error_NeedTaskSelectedDelete;
	public String error_NeedTaskSelectedMove;
	public String error_NeedTaskSelectedEdit;
	public String error_NeedTaskSelectedChangeState;
	public String error_CantChangeTaskState;
	public String error_SelectionMustBeTask;
	
	public String error_TaskStateNoneOf;
	public String error_SubtaskStateNoneOf;
	
	public String warning_AuthorNameNotSet;
	public String warning_CantDeleteRootHighMedLow;
	public String warning_CantAddTask;
	public String warning_CantMoveRootHighMedLow;
	public String warning_CantIncreasePriority;
	public String warning_CantDecreasePriority;
	public String warning_CantChangeTaskPriority;
	
	public String change_SubtaskAdded;
	
	public String stateChange_Date;
	public String stateChange_Edited;
	public String stateChange_PriorChanged;
	public String stateChange_AddedSubtask;
	public String stateChange_Opened;
	public String stateChange_Working;
	public String stateChange_Deleted;
	public String stateChange_PutOnHold;
	public String stateChange_Cancelled;
	public String stateChange_OnRevision;
	public String stateChange_PendingRevision;
	public String stateChange_Done;
	public String stateChange_StateSetTo;
	public String stateChange_Reason;
	
	public String state_Opened;
	public String state_Done;
	public String state_Working;
	public String state_PutOnHold;
	public String state_Deleted;
	public String state_Cancelled;
	public String state_OnRevision;
	public String state_PendingRevision;
	public String state_Edited;
	public String state_PriorityChanged;
	public String state_AddedSubtask;
	
	public String question_WhyChangeTaskState;
	public String dialogtitle_ChangeTaskState;
	
	public String question_DoYouWantToSaveChanges;
	public String dialogtitle_UnsavedChanges;
	
	public String dialogtitle_CreateNewTask;
	public String dialogtitle_EditTask;
	public String dialogtitle_About;
	public String dialogtitle_Translations;
	
	public String inputdialog_EnterAuthorName;
	
	public String GUIAbout_labelSoftwareDesigned;
	public String GUIAbout_labelContact;
	public String GUIAbout_labelLicense;
	
	public String GUINewTask_labelNameTask;
	public String GUINewTask_labelAuthorTask;
	public String GUINewTask_labelDescription;
	public String GUINewTask_buttonCreateTask;
	public String GUINewTask_buttonCancel;
	
	public String GUIEditTask_buttonSaveEdits;
	public String GUIEditTask_buttonCancelEdits;

	private void readLanguageFile(String f) {
		String contents = Tools.readFile(f);
		
		// main JSON object
		JSONObject main = new JSONObject(contents);
		
		menuItemFile = main.getString("menuItemFile");
		menuItemNewTaskFile = main.getString("menuItemNewTaskFile");
		menuItemOpenFile = main.getString("menuItemOpenFile");
		menuItemSaveTasks = main.getString("menuItemSaveTasks");
		menuItemSaveTasksAs = main.getString("menuItemSaveTasksAs");
		menuItemExit = main.getString("menuItemExit");
		
		menuItemHelp = main.getString("menuItemHelp");
		menuItemAbout = main.getString("menuItemAbout");
		menuItemTranslations = main.getString("menuItemTranslations");
	
		buttonNewTaskFile = main.getString("buttonNewTaskFile");
		buttonOpenTasks = main.getString("buttonOpenTasks");
		buttonSaveTasks = main.getString("buttonSaveTasks");
		buttonSaveTasksAs = main.getString("buttonSaveTasksAs");
		buttonSetAuthor = main.getString("buttonSetAuthor");
		labelAuthorName = main.getString("labelAuthorName");
		labelUnsavedChanges = main.getString("labelUnsavedChanges");
	
		highPriorNode = main.getString("highPriorNode");
		medPriorNode = main.getString("medPriorNode");
		lowPriorNode = main.getString("lowPriorNode");
	
		labelTaskName = main.getString("labelTaskName");
		labelTaskAuthor = main.getString("labelTaskAuthor");
		labelTaskDate = main.getString("labelTaskDate");
		labelTaskDescription = main.getString("labelTaskDescription");
		labelTaskHistory = main.getString("labelTaskHistory");
		labelTaskStateText = main.getString("labelTaskStateText");
	
		buttonNewTask = main.getString("buttonNewTask");
		buttonRemove = main.getString("buttonRemove");
		buttonUp = main.getString("buttonUp");
		buttonDown = main.getString("buttonDown");
		buttonPriority = main.getString("buttonPriority");
		buttonShowAll = main.getString("buttonShowAll");
		buttonHideAll = main.getString("buttonHideAll");
	
		buttonTaskDone = main.getString("buttonTaskDone");
		buttonTaskWorking = main.getString("buttonTaskWorking");
		buttonTaskHold = main.getString("buttonTaskHold");
		buttonTaskOnRevision = main.getString("buttonTaskOnRevision");
		buttonTaskPendingRevision = main.getString("buttonTaskPendingRevision");
		buttonTaskEdit = main.getString("buttonTaskEdit");
		buttonTaskCancel = main.getString("buttonTaskCancel");
		buttonTaskDelete = main.getString("buttonTaskDelete");
		buttonTaskClear = main.getString("buttonTaskClear");
		buttonTaskReopen = main.getString("buttonTaskReopen");
		
		error_FileIsLocked = main.getString("error_FileIsLocked");
		error_CouldNotOpenFile = main.getString("error_CouldNotOpenFile");
		error_CouldNotDetermineNodePrior = main.getString("error_CouldNotDetermineNodePrior");
		error_NeedTaskSelectedDelete = main.getString("error_NeedTaskSelectedDelete");
		error_NeedTaskSelectedMove = main.getString("error_NeedTaskSelectedMove");
		error_NeedTaskSelectedEdit = main.getString("error_NeedTaskSelectedEdit");
		error_NeedTaskSelectedChangeState = main.getString("error_NeedTaskSelectedChangeState");
		error_CantChangeTaskState = main.getString("error_CantChangeTaskState");
		error_SelectionMustBeTask = main.getString("error_SelectionMustBeTask");
		
		error_TaskStateNoneOf = main.getString("error_TaskStateNoneOf");
		error_SubtaskStateNoneOf = main.getString("error_SubtaskStateNoneOf");
	
		warning_AuthorNameNotSet = main.getString("warning_AuthorNameNotSet");
		warning_CantDeleteRootHighMedLow = main.getString("warning_CantDeleteRootHighMedLow");
		warning_CantAddTask = main.getString("warning_CantAddTask");
		warning_CantMoveRootHighMedLow = main.getString("warning_CantMoveRootHighMedLow");
		warning_CantIncreasePriority = main.getString("warning_CantIncreasePriority");
		warning_CantDecreasePriority = main.getString("warning_CantDecreasePriority");
		warning_CantChangeTaskPriority = main.getString("warning_CantChangeTaskPriority");
		
		change_SubtaskAdded = main.getString("change_SubtaskAdded");
		
		stateChange_Date = main.getString("stateChange_Date");
		stateChange_Edited = main.getString("stateChange_Edited");
		stateChange_PriorChanged = main.getString("stateChange_PriorChanged");
		stateChange_AddedSubtask = main.getString("stateChange_AddedSubtask");
		stateChange_Opened = main.getString("stateChange_Opened");
		stateChange_Working = main.getString("stateChange_Working");
		stateChange_Deleted = main.getString("stateChange_Deleted");
		stateChange_PutOnHold = main.getString("stateChange_PutOnHold");
		stateChange_Cancelled = main.getString("stateChange_Cancelled");
		stateChange_OnRevision = main.getString("stateChange_OnRevision");
		stateChange_PendingRevision = main.getString("stateChange_PendingRevision");
		stateChange_Done = main.getString("stateChange_Done");
		stateChange_StateSetTo = main.getString("stateChange_StateSetTo");
		stateChange_Reason = main.getString("stateChange_Reason");
		
		state_Opened = main.getString("state_Opened");
		state_Done = main.getString("state_Done");
		state_Working = main.getString("state_Working");
		state_PutOnHold = main.getString("state_PutOnHold");
		state_Deleted = main.getString("state_Deleted");
		state_Cancelled = main.getString("state_Cancelled");
		state_OnRevision = main.getString("state_OnRevision");
		state_PendingRevision = main.getString("state_PendingRevision");
		state_Edited = main.getString("state_Edited");
		state_PriorityChanged = main.getString("state_PriorityChanged");
		state_AddedSubtask = main.getString("state_AddedSubtask");
		
		question_WhyChangeTaskState = main.getString("question_WhyChangeTaskState");
		dialogtitle_ChangeTaskState = main.getString("dialogtitle_ChangeTaskState");
		
		question_DoYouWantToSaveChanges = main.getString("question_DoYouWantToSaveChanges");
		dialogtitle_UnsavedChanges = main.getString("dialogtitle_UnsavedChanges");
		
		dialogtitle_CreateNewTask = main.getString("dialogtitle_CreateNewTask");
		dialogtitle_EditTask = main.getString("dialogtitle_EditTask");
		dialogtitle_About = main.getString("dialogtitle_About");
		dialogtitle_Translations = main.getString("dialogtitle_Translations");
		
		inputdialog_EnterAuthorName = main.getString("inputdialog_EnterAuthorName");
		
		GUIAbout_labelSoftwareDesigned = main.getString("GUIAbout_labelSoftwareDesigned");
		GUIAbout_labelContact = main.getString("GUIAbout_labelContact");
		GUIAbout_labelLicense = main.getString("GUIAbout_labelLicense");
	
		GUINewTask_labelNameTask = main.getString("GUINewTask_labelNameTask");
		GUINewTask_labelAuthorTask = main.getString("GUINewTask_labelAuthorTask");
		GUINewTask_labelDescription = main.getString("GUINewTask_labelDescription");
		GUINewTask_buttonCreateTask = main.getString("GUINewTask_buttonCreateTask");
		GUINewTask_buttonCancel = main.getString("GUINewTask_buttonCancel");
		
		GUIEditTask_buttonSaveEdits = main.getString("GUIEditTask_buttonSaveEdits");
		GUIEditTask_buttonCancelEdits = main.getString("GUIEditTask_buttonCancelEdits");
	}
	
	private static Translate instance = null;
	private Translate() {
		SystemInfo sysinfo = SystemInfo.getInstance();
		readLanguageFile(sysinfo.langFilePath);
	}
	
	public static Translate getInstance() {
		if (instance == null) {
			instance = new Translate();
		}
		return instance;
	}
}
