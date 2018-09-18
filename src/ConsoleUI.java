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
        System.out.println("Use 'help' for commands");
    }

    private void showHelp() {
        System.out.print("list\t\t\tLists current tasks and their indexes\n" +
                "add\t\t\tAdds a new task\n" +
                "remove <index>\t\tRemoves a task with given task index\n" +
                "complete <index>\tCompletes a task with given task index\n" +
                "load\t\t\tLoads all stored tasks (Done at startup)\n" +
                "quit\t\t\tQuits the program\n" +
                "help\t\t\tShows this text\n");

        System.out.println("");
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
            case "help":
                showHelp();
                break;
            case "quit":
                System.exit(0);
            default:
                System.out.println("Invalid command");
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

    private int getDay(String string) throws NumberFormatException {
        int day = Integer.parseInt(string);
        if (day < 1 || day > 31) {
            return -1;
        }
        return day;
    }

    private int getMonth(String string) throws NumberFormatException {
        int month = Integer.parseInt(string);
        if (month < 1 || month > 12) {
            return -1;
        }
        return month;
    }

    private Calendar getTaskDateInput() throws ParseException, NumberFormatException {
        String dateString = getInput("Due date of task \"HH mm d(ay) M(onth) yyyy (year)\" (all integers)");
        String[] tokens = dateString.split(" ");
        //TODO: Add checking for dates entered before current time
        Calendar calendar = Calendar.getInstance();
        //Second doesn't matter, set to 0
        calendar.set(Calendar.SECOND, 0);
        //Default minute to 0
        calendar.set(Calendar.MINUTE, 0);
        int hour;
        int minute;
        int day;
        int month;
        int year;
        if (tokens.length > 5) {
            System.out.println("Too many arguments");
            return null;
        }
        if (tokens.length > 0) {
            hour = getHour(tokens[0]);
            if (hour == -1) {
                System.out.println("Hour must be between 0 and 60");
                return null;
            }
            calendar.set(Calendar.HOUR_OF_DAY, hour);
        }
        if (tokens.length > 1) {
            minute = getMinute(tokens[1]);
            if (minute == -1) {
                System.out.println("Minute must be between 0 and 60");
                return null;
            }
            calendar.set(Calendar.MINUTE, minute);
        }
        if (tokens.length > 2) {
            day = getDay(tokens[2]);
            if (day == -1) {
                System.out.println("Day must be between 1 and 30");
                return null;
            }
            calendar.set(Calendar.DAY_OF_MONTH, day);
        }
        if (tokens.length > 3) {
            month = getMonth(tokens[3]);
            if (month == -1) {
                System.out.println("Month must be between 1 and 12");
                return null;
            }
            calendar.set(Calendar.MONTH, month);
        }
        if (tokens.length == 5) {
            year = Integer.parseInt(tokens[4]);
            calendar.set(Calendar.YEAR, year);
        }

        return calendar;
    }

    @Override
    public void addTask() {
        try {
            String message = getInput("Task message:");
            Calendar calendar = getTaskDateInput();
            if (calendar == null) {
                //Some input was incorrect, don't create a task
                return;
            }
            Task task = new Task(message, calendar);
            addTask(task);
        } catch (NumberFormatException e) {
            System.out.println("Input must be in integer form");
        } catch (ParseException e) {
            System.out.println("Could not parse the input. " + e.getMessage());
        }
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
        try {
            Task taskToComplete = tasks.findTask(taskIndex);
            taskToComplete.completeTask();
            System.out.println("Completed task " + taskToComplete.getMessage());
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Could not complete task with index " + taskIndex + ". Out of bounds");
        }
    }

    @Override
    public void taskCompleted(Task task) {
        System.out.println(task.getMessage() + " has completed");
    }

    @Override
    public void taskNotified(Task task) {
        System.out.println("\nTask " + task.getMessage() + " " + task.getDateFormatted() + " is due!");
        System.out.print("Action:  ");
    }
}
