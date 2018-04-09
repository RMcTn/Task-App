import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Tasks tasks = new Tasks();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 1);
        Task task = new Task("Test task", calendar);
        tasks.addTask(task);
        TaskTrayIcon taskTrayIcon = new TaskTrayIcon();
        while (true) {
            try {
                TimeUnit.MINUTES.sleep(1);
            } catch (Exception e) {

            }
            for (Task taskTemp : tasks.getTasks()) {
                if (taskTemp.isComplete())
                    return;
                if (taskTemp.hasNotified())
                    return;
                Calendar now = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date thisDate = taskTemp.getTaskDate().getTime();
                Date otherDate = now.getTime();
                String thisDateString = dateFormat.format(thisDate);
                String otherDateString = dateFormat.format(otherDate);
                if (thisDateString.equalsIgnoreCase(otherDateString)) {
                    try {
                        taskTrayIcon.display(taskTemp);
                        taskTrayIcon.beep();
                        taskTemp.setNotified(true);
                    } catch (AWTException e) {
                        System.out.println("AWTException hit here");
                    }
                }
            }
        }
    }
}