# Task management program for desktop. 

Command line program to create, remove and complete user set tasks. Supports desktop notifications to remind users of tasks that are due.

## Features
* Create tasks with a message, and a due date
* Complete tasks through the command line, or through double clicking the task icon in your desktop's equivalent to Windows hidden icons (systray)
* Checks if any tasks are due every minute
* Tasks are saved between sessions
* Desktop notifcations when a task is due
## Usage
### Command line
Run the jar file from your command prompt/terminal using __java -jar '*file*'.jar__
```
 list             Lists current tasks and their indexes
 add              Adds a new task
 remove <index>   Removes a task with given task index
 complete <index> Completes a task with given task index
 load             Loads all stored tasks (Done at startup)
 quit             Quits the program
 help             Shows this text
```
```
Date format - HH MM day month year
HH - Hour in 24 hour format
MM - Minutes (optional)				Defaults to 0
day - Day (optional)				Defaults to current day
month - Month (optional)			Defaults to current month
year - Year (optional)				Defaults to current year
```
### No UI
Running the jar file through other means other than a terminal will run the program in the background.
Tasks can't be added, but they can be completed using the task icon in your desktop's equivalent to Windows systray.
Notifications will still run and be presented.
