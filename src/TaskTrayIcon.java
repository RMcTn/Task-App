import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class TaskTrayIcon {
    private static Map<Task, TrayIcon> taskTrayIconMap;

    public TaskTrayIcon() {
        taskTrayIconMap = new HashMap<>();
    }

    public static void remove(Task task) {
        SystemTray tray = SystemTray.getSystemTray();
        TrayIcon trayIcon = taskTrayIconMap.get(task);
        tray.remove(trayIcon);
    }

    public void display(Task task) throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();

        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        TrayIcon trayIcon = new TrayIcon(image, task.getMessage());
        taskTrayIconMap.put(task, trayIcon);
        trayIcon.setImageAutoSize(true);
        tray.add(trayIcon);

        trayIcon.displayMessage(task.getMessage(), task.getMessage(), TrayIcon.MessageType.INFO);
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                if (!task.isComplete()) {
                    task.completeTask();
                }
            }
        };

        //Use a MouseListener if double clicking isn't desired
        trayIcon.addActionListener(actionListener);
    }

    public void beep() {
        Toolkit.getDefaultToolkit().beep();
    }
}
