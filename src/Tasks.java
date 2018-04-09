import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Tasks {

    private List<Task> tasks;

    public Tasks() {
        tasks = new ArrayList<>();
    }
    
    public boolean addTask(Task task) {

        //Don't allow duplicate tasks
        //Duplicate task being anything with the same message and taskDate to the minute
        if (tasks.contains(task)) {
            return false;
        }

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

    public int size() {
        return tasks.size();
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
