import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class ConsoleUI extends UI implements TaskListener {
    private Scanner scanner;

    public ConsoleUI() {
        super();
        if (!successfulTaskLoad) {
            System.out.println("Could not loads tasks. New tasks will still be attempted to be saved");
            System.out.println("Try 'load' to try again");
        }
        scanner = new Scanner(System.in);
    }

    public void step() {
        String input = getInput("Action:");
        String[] tokens = input.split(" ");
        switch (tokens[0]) {
            case "list":
                showTasks();
                break;
            case "add":
                addTask();
                break;
            case "remove":
                try {
                    int taskIndex = Integer.parseInt(tokens[1]);
                    removeTask(taskIndex);
                } catch (NumberFormatException e) {
                    System.out.println(input + " is not a integer");
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Remove requires 1 integer parameter");
                }
                break;
            case "complete":
                try {
                    int taskIndex = Integer.parseInt(tokens[1]);
                    completeTask(taskIndex);
                } catch (NumberFormatException e) {
                    System.out.println(input + " is not a integer");
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Complete requires 1 integer parameter");
                }
                break;
            case "load":
                try {
                    tasks.loadTasks();
                    System.out.println("Loaded tasks successfully");
                } catch (SQLException e) {
                    System.out.println("Could not load tasks. " + e.getMessage());
                }
                break;
            case "quit":
                System.exit(0);
            default:
                System.out.println("Invalid command");
                //TODO: Should print out valid commands or "help" command
        }
    }

    private String getInput(String message) {
        System.out.print(message + " ");
        return scanner.nextLine().toLowerCase();
    }

    private int getHour(String string) throws NumberFormatException {
        int hour = Integer.parseInt(string);
        if (hour < 0 || hour > 24) {
            //Should we be checking for 23 instead?
            return -1;
        }
        return hour;
    }

    private int getMinute(String string) throws NumberFormatException {
        int minute = Integer.parseInt(string);
        if (minute < 0 || minute > 60) {
            //Should we be checking for 59 instead?
            return -1;
        }
        return minute;
    }

    @Override
    public void addTask() {
        String message = getInput("Task message:");
        String dateString = getInput("Due date of task \"HH mm d(ay) M(onth) yyyy (year)\" (all integers)");
        String[] tokens = dateString.split(" ");
        //TODO: Add checking for dates entered before current time
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        if (tokens.length == 1) {
            //Only hour has been entered, create a date with current date, and hour rounded to HH:00
            try {
                int hour = getHour(tokens[0]);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, 0);
            } catch (NumberFormatException e) {
                System.out.println("Time must be in integer form");
                return;
            }
        } else if (tokens.length == 2) {
            //Hour and minute have been entered. Create date with current date, with hour and minute changed
            try {
                int hour = getHour(tokens[0]);
                int minute = getMinute(tokens[1]);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
            } catch (NumberFormatException e) {
                System.out.println("Time must be in integer form");
                return;
            }
        } else if (tokens.length < 5) {
            //All fields entered except year, so use current year for date
            try {
                int hour = getHour(tokens[0]);
                int minute = getMinute(tokens[1]);
                //Days and months CAN be < 0 or > than their respective maxes (this will just rollover)
                int day = Integer.parseInt(tokens[2]);
                int month = Integer.parseInt(tokens[3]);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.set(Calendar.MONTH, month);
            } catch (NumberFormatException e) {
                System.out.println("Hour, minute, day and month must be an integer");
                return;
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Not enough arguments. Month has been missed out");
                return;
            }
        } else {
            //Parse the string to create a date
            try {
                Date date;
                //No checks are made here for logical out of bounds values
                //TODO: Should checks be added?
                DateFormat dateFormat = new SimpleDateFormat("HH mm d M yyyy");
                date = dateFormat.parse(dateString);
                calendar.setTime(date);
            } catch (ParseException e) {
                System.out.println(dateString + " is not in the correct format");
                return;
            }
        }
        Task task = new Task(message, calendar);
        addTask(task);
    }

    public void addTask(Task task) {
        try {
            if (tasks.addTask(task))
                System.out.println("Added task " + task.getMessage() + " for " + task.getDateFormatted());
            else
                System.out.println("Could not add task " + task.getMessage() + ", already exists");
        } catch (SQLException e) {
            System.out.println("Could not add task " + e.getMessage());
        }
    }

    @Override
    public void removeTask(int taskIndex) {
        try {
            Task taskToRemove = tasks.findTask(taskIndex);
            tasks.removeTask(taskToRemove);
            System.out.println("Removed task with index " + taskIndex + " (" + taskToRemove.getMessage() + ")");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Could not remove task with index " + taskIndex + ". Out of bounds");
        } catch (SQLException e) {
            System.out.println("Could not remove task with index " + taskIndex + e.getMessage());
        }
    }

    @Override
    public void showTasks() {
        if (tasks.size() == 0) {
            System.out.println("No tasks to display");
            return;
        }
        int i = 0;
        for (Task task : tasks.getTasks()) {
            System.out.printf("%d: %s, %tc, Created: %tc Complete: %b, Notified: %b\n", i++, task.getMessage(), task.getTaskDate(), task.getCreationDate(), task.isComplete(), task.hasNotified());
        }

    }

    @Override
    public void completeTask(int taskIndex) {
        Task taskToComplete = tasks.findTask(taskIndex);
        taskToComplete.completeTask();
        System.out.println("Completed task " + taskToComplete.getMessage());
    }

    @Override
    public void taskCompleted(Task task) {
        System.out.println(task.getMessage() + " has completed");
    }

    @Override
    public void taskNotified(Task task) {
        System.out.println("Task " + task.getMessage() + " " + task.getDateFormatted() + " is due!!!");

    }
}
