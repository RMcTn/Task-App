public interface TaskListener {
    void taskCompleted(Task task);
    void taskNotified(Task task);
}