import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        Tasks tasks = new Tasks();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 1);
        Task task = new Task("Test task", calendar);
        tasks.addTask(task);
        TaskTrayIcon taskTrayIcon = new TaskTrayIcon();

        Thread taskThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        TimeUnit.MINUTES.sleep(1);
                    } catch (Exception e) {

                    }
                    System.out.println("Checking all tasks at " + Calendar.getInstance().getTime());
                    for (Task taskTemp : tasks.getTasks()) {
                        if (taskTemp.isComplete())
                            continue;
                        if (taskTemp.hasNotified())
                            continue;
                        System.out.println("Checking " + taskTemp.getMessage());
                        Calendar now = Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date otherDate = now.getTime();
                        String thisDateString = taskTemp.getDateFormatted();
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
        });
        taskThread.start();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("input: ");
            String input = scanner.nextLine();
            System.out.println("Line was " + input);
            if (input.equalsIgnoreCase("add")) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.add(Calendar.MINUTE, 1);
                Task task1 = new Task("Test add", calendar1);
                if (tasks.addTask(task1))
                    System.out.println("added Test add task at:" + calendar1.getTime());
                else
                    System.out.println("Couldn't add task");
            }
            if (input.equalsIgnoreCase("view")) {
                for (Task taskToPrint : tasks.getTasks()) {
                    System.out.println("Task: " + taskToPrint.getMessage() + " Time: " + taskToPrint.getDateFormatted());
                }
            }
        }

    }

}