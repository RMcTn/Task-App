import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Task implements Serializable {

    private List<TaskListener> listeners;

    private static final AtomicInteger count = new AtomicInteger(0);
    private int ID;
    private String message;
    private Calendar creationDate;
    private Calendar taskDate;
    private boolean completed = false;
    private boolean notified = false;

    public Task(String message, Calendar taskDate) {
        this.message = message;
        this.taskDate = taskDate;
        this.creationDate = Calendar.getInstance();
        ID = count.incrementAndGet();
        listeners = new ArrayList<>();
    }

    public void addListener(TaskListener listener) {
        listeners.add(listener);
    }

    public void completeTask() {
        completed = true;
        for (TaskListener listener: listeners)
            listener.taskCompleted(this);
    }

    public boolean isComplete() {
        return completed;
    }

    public Calendar getCreationDate() {
        return creationDate;
    }

    public Calendar getTaskDate() {
        return taskDate;
    }

    public String getDateFormatted() {
        //Anything under a minute isn't important
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy");
        Date thisDate = this.taskDate.getTime();
        return dateFormat.format(thisDate);
    }

    public String getMessage() {
        return message;
    }

    public boolean hasNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
        //TODO: have a notify for task listeners, just like completion? allows displaying of tasks on ui etc
        for (TaskListener taskListener: listeners) {
            taskListener.taskNotified(this);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Task) {
            Task otherTask = (Task) obj;
            String thisDateString = this.getDateFormatted();
            String otherDateString = otherTask.getDateFormatted();
            return thisDateString.equalsIgnoreCase(otherDateString) && this.message.equalsIgnoreCase(otherTask.message);
        }
        return false;
    }
}
