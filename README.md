# Todo List Manager

This is a stand-alone program (browser-less and server-less) written in Java to create and edit simple Todo Lists, consisiting of high, medium and low priority tasks.

This software allows adding subtasks to every task and implements several states a task can be in. For example, after being _Opened_, a task can have somebody _Working_ on it who, after a while, might get tired of it, and mark it as _Put on hold_. A task can be _Cancelled_ and be marked as _Deleted_ after being completed (complete a task and realise it was a bad idea, so you delete whatever changes you did to a project). If a task needs somebody to revise it, the user can mark it as _Pending revision_, or that it is already being _On revision_. Depending on what state the task will be marked as, the software will prompt the user to give a reason for such a change.

Since a task might be edited several times, this software can handle an unlimited amount of changes and keeps track of the history of changes.

The tasks are saved as a JSON-formatted file. The library used to read and write these files can be found [here](https://github.com/stleary/JSON-java), which has been included in this repository for convenience (its LICENSE has been included also in this repository in the [LICENSES](https://github.com/lluisalemanypuig/TodoListManager/blob/master/LICENSES/) directory).

## Execute

Copy the _.jar_ file and _lib_ directory wherever you like. You will also need to copy the [TodoListManagerData/](https://github.com/lluisalemanypuig/TodoListManager/blob/master/TodoListManagerData/) directory into your user directory. Finally double-click on the _.jar_ file in order to execute this software.

## Sample

![How the software looks like when the user opens the file _sample_ file](https://github.com/lluisalemanypuig/TodoListManager/blob/master/screenshot.png)
