import java.awt.*;
import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Tasks implements TaskListener, Serializable {

    private volatile List<Task> tasks;
    private List<UiListener> uiListeners;
    private boolean usingSystemTray;
    private boolean usingDatabase;

    public Tasks() {
        tasks = new ArrayList<>();
        uiListeners = new ArrayList<>();
        //NOTE: Setting to true by default for now. Allow for changing later
        usingSystemTray = true;
        //TODO: Add setting for using database as well
        usingDatabase = true;
    }

    public void setUsingSystemTray(boolean usingSystemTray) {
        this.usingSystemTray = usingSystemTray;
    }

    public void setUsingDatabase(boolean usingDatabase) {
        this.usingDatabase = usingDatabase;
    }

    public void addUiListener(UiListener uiListener) {
        uiListeners.add(uiListener);
    }

    public boolean addTask(Task task) throws SQLException {

        //Don't allow duplicate tasks
        //Duplicate task being anything with the same message and taskDate to the minute
        if (tasks.contains(task)) {
            return false;
        }

        task.addListener(this);
        boolean success = tasks.add(task);
        if (usingDatabase) {
            if (success) {
                SQLiteDBConnection sqLiteDBConnection = new SQLiteDBConnection();
                sqLiteDBConnection.insertTask(task.getMessage(), task.getTaskDate(), task.getCreationDate(), task.hasNotified(), task.isComplete());
            }
        }
        return success;
    }

    public boolean removeTask(Task task) throws SQLException {
        boolean success = tasks.remove(task);
        if (usingDatabase) {
            if (success) {
                try {
                    SQLiteDBConnection sqLiteDBConnection = new SQLiteDBConnection();
                    sqLiteDBConnection.removeTask(task);
                } catch (SQLException e) {
                    //Couldn't get a connection to the database
                    //Add task back
                    tasks.add(task);
                    throw new SQLException(e);
                }
            }
        }
        return success;
    }

    public Task findTask(String message, Calendar calendar) {
        Task taskToFind = new Task(message, calendar);
        int index = tasks.indexOf(taskToFind);
        if (index == -1) {
            return null;
        }
        return tasks.get(index);
    }

    public Task findTask(int index) throws IndexOutOfBoundsException {
        return tasks.get(index);
    }

    public int size() {
        return tasks.size();
    }

    public List<Task> getTasks() {
        return tasks;
    }

    //Should only be called once at startup
    public void loadTasks() throws SQLException {
        if (!usingDatabase) {
            //Can't load tasks if there is no database being used
            throw new SQLException("No database in use");
        }
        SQLiteDBConnection sqLiteDBConnection = new SQLiteDBConnection();
        tasks = sqLiteDBConnection.loadTasks();
        //Re-add listeners
        for (Task task : tasks) {
            System.out.println(task.getMessage());
            task.addListener(this);
        }
    }

    @Override
    public void taskCompleted(Task task) {
        try {
            //TODO: Should tasks be removed when completed?
            for (UiListener uiListener : uiListeners) {
                uiListener.taskCompleted(task);
            }
            removeTask(task);
            if (usingSystemTray)
                TaskTrayIcon.remove(task);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void taskNotified(Task task) {
        //TODO: Decide what to do here (system icon stuff? nothing?)
        for (UiListener uiListener : uiListeners) {
            uiListener.taskNotified(task);
        }
    }

    public void checkTasks() throws AWTException {
        for (Task task : getTasks()) {
            if (task.isComplete())
                continue;
            if (task.hasNotified())
                continue;
            Calendar now = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date otherDate = now.getTime();
            String thisDateString = task.getDateFormatted();
            String otherDateString = dateFormat.format(otherDate);
            boolean taskIsBeforeNow = task.getTaskDate().compareTo(now) < 0;
            if (thisDateString.equalsIgnoreCase(otherDateString) || taskIsBeforeNow) {
                if (usingSystemTray) {
                    //TODO: Move system tray code away from here?
                    TaskTrayIcon taskTrayIcon = new TaskTrayIcon();
                    taskTrayIcon.display(task);
                    taskTrayIcon.beep();
                }
                task.setNotified(true);
            }
        }
    }
}
