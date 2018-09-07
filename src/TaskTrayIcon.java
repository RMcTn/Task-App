import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TaskTrayIcon implements TaskListener {

    public void display(Task task) throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();

        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        TrayIcon trayIcon = new TrayIcon(image, task.getMessage());
        trayIcon.setImageAutoSize(true);
        tray.add(trayIcon);

        trayIcon.displayMessage(task.getMessage(), task.getMessage(), TrayIcon.MessageType.INFO);
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                task.completeTask();
            }
        };

        //Use a MouseListener if double clicking isn't desired
        trayIcon.addActionListener(actionListener);
    }

    public void beep() {
        Toolkit.getDefaultToolkit().beep();
    }
}
