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
        Calendar early = Calendar.getInstance();
        early.set(1990, Calendar.MAY, 12);
        Task earlyTask = new Task("EARLY", early);
        tasks.addTask(earlyTask);
        tasks.addTask(task);
        TaskTrayIcon taskTrayIcon = new TaskTrayIcon();

        Thread taskThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        tasks.checkTasks();
                        TimeUnit.MINUTES.sleep(1);
                    } catch (AWTException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
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