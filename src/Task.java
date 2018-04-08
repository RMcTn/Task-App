import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class Task {

    private static final AtomicInteger count = new AtomicInteger(0);
    private int ID;
    private String message;
    private Calendar creationDate;
    private Calendar taskDate;
    private boolean completed = false;

    public Task(String message, Calendar taskDate) {
        this.message = message;
        this.taskDate = taskDate;
        this.creationDate = Calendar.getInstance();
        ID = count.incrementAndGet();
    }

    public void completeTask() {
        completed = true;
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

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Task) {
            Task otherTask = (Task) obj;
            //Compare the time to the minute, anything below that isn't important
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date thisDate = this.taskDate.getTime();
            Date otherDate = otherTask.taskDate.getTime();
            String thisDateString = dateFormat.format(thisDate);
            String otherDateString = dateFormat.format(otherDate);
            return thisDateString.equalsIgnoreCase(otherDateString) && this.message.equalsIgnoreCase(otherTask.message);
        }
        return false;
    }
}
