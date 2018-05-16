import java.io.EOFException;

import java.io.IOException;

public abstract class UI {
    protected Tasks tasks;

    public UI() {
        try {
            tasks = new Tasks();
            tasks.loadTasks();
        } catch (EOFException e) {
            //Do nothing
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Tasks getTasks() {
        return tasks;
    }

    public abstract void addTask();
    public abstract void removeTask();
    public abstract void showTasks();
    public abstract void completeTask();
}
