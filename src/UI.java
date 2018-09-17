import java.sql.SQLException;

public abstract class UI implements UiListener {

    //TODO: This may be problematic if multiple UI's are used at the same time
    //Could have a Tasks instance be created at program startup that loads up previous tasks, and all UI's
    //that one instead of each UI having its own
    protected Tasks tasks;
    //Used to allow UI subclasses to deal with tasks not being loaded.
    //Using boolean since Java requires super() be first statement in constructor, so can't use try/catch
    protected boolean successfulTaskLoad = true;

    public UI() {
        try {
            tasks = new Tasks();
            tasks.addUiListener(this);
            tasks.loadTasks();
        } catch (SQLException e) {
            successfulTaskLoad = false;
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
