public abstract class UI {
    protected Tasks tasks;

    public UI() {
        tasks = new Tasks();
    }

    public Tasks getTasks() {
        return tasks;
    }

    public abstract void addTask();
    public abstract void removeTask();
    public abstract void showTasks();
    public abstract void completeTask();
}
