import java.awt.*;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void start(ConsoleUI consoleUI) {
        consoleUI.showTasks();

        Thread taskThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        consoleUI.getTasks().checkTasks();
                        TimeUnit.MINUTES.sleep(1);
                    } catch (AWTException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        //Don't need to handle this
                    }

                }
            }
        });
        taskThread.start();

    }



    public static void main(String[] args) {
        ConsoleUI consoleUI = new ConsoleUI();

        start(consoleUI);
        System.out.println("Task app");
        while (true) {
            consoleUI.step();
        }
    }

}