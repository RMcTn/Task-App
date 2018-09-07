import java.io.EOFException;

import java.io.IOException;

public abstract class UI implements UiListener {

    //TODO: This may be problematic if multiple UI's are used at the same time
    //Could have a Tasks instance be created at program startup that loads up previous tasks, and all UI's
    //that one instead of each UI having its own
    protected Tasks tasks;

    public UI() {
        try {
            tasks = new Tasks();
            tasks.addUiListener(this);
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
    public abstract void removeTask(int taskIndex);
    public abstract void showTasks();
    public abstract void completeTask(int taskIndex);
}
