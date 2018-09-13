import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Tasks implements TaskListener, Serializable {

    private volatile List<Task> tasks;
    private List<UiListener> uiListeners;
    private boolean usingSystemTray;

    public Tasks() {
        tasks = new ArrayList<>();
        uiListeners = new ArrayList<>();
        //NOTE: Setting to true by default for now. Allow for changing later
        usingSystemTray = true;
    }

    public void setUsingSystemTray(boolean usingSystemTray) {
        this.usingSystemTray = usingSystemTray;
    }

    public void addUiListener(UiListener uiListener) {
        uiListeners.add(uiListener);
    }
    
    public boolean addTask(Task task) {

        //Don't allow duplicate tasks
        //Duplicate task being anything with the same message and taskDate to the minute
        if (tasks.contains(task)) {
            return false;
        }

        task.addListener(this);
        return tasks.add(task);
    }

    public boolean removeTask(Task task) {
        return tasks.remove(task);
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

    public void saveTasks() throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream("tasks.ser");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(tasks);
        objectOutputStream.close();
        System.out.println("Wrote to tasks.ser");
    }

    public void loadTasks() throws IOException, ClassNotFoundException {
        File file = new File("Tasks.ser");
        file.createNewFile();
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        tasks = (List<Task>) objectInputStream.readObject();
        objectInputStream.close();
        System.out.println("Read from tasks.ser");
    }

    @Override
    public void taskCompleted(Task task) {
        //TODO: Should tasks be removed when completed?
        for (UiListener uiListener: uiListeners) {
            uiListener.taskCompleted(task);
        }
        removeTask(task);
        if (usingSystemTray)
            TaskTrayIcon.remove(task);
    }

    @Override
    public void taskNotified(Task task) {
        //TODO: Decide what to do here (system icon stuff? nothing?)
        for (UiListener uiListener: uiListeners) {
            uiListener.taskNotified(task);
        }
    }

    public void checkTasks() throws AWTException {
        for (Task task: getTasks()) {
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
