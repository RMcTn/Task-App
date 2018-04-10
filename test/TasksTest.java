import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

public class TasksTest {
    private Tasks tasks;

    @BeforeEach
    public void setup() {
        tasks = new Tasks();
    }

    @Test
    void testAddTask() {
        Calendar firstDate = Calendar.getInstance();
        //Arbitrary date
        firstDate.set(2000, Calendar.MAY, 9, 10, 10);
        Task firstTask = new Task("Duplicate task test", firstDate);
        //Task isn't already in tasks. Should be true
        assertTrue(tasks.addTask(firstTask));

        //firstTask is already in tasks. Should be false
        assertFalse(tasks.addTask(firstTask));

        //duplicateTask is equal to firstTask and shouldn't be added. Should be false
        Calendar secondDate = Calendar.getInstance();
        secondDate.set(2000, Calendar.MAY, 9, 10, 10);
        Task duplicateTask = new Task("Duplicate task test", secondDate);
        assertFalse(tasks.addTask(duplicateTask));

        //duplicateTask is no longer equal to firstTask. Should be true
        secondDate.add(Calendar.MINUTE, 1);
        assertTrue(tasks.addTask(duplicateTask));
    }

    @Test
    void testRemoveTask() {
        Task firstTask = new Task("Task message", Calendar.getInstance());
        tasks.addTask(firstTask);

        //firstTask is in tasks. Should be true
        assertTrue(tasks.removeTask(firstTask));

        //firstTask isn't in tasks. Should be false
        assertFalse(tasks.removeTask(firstTask));

    }

    @Test
    void testFindTask() {
        String message = "Test find task";
        Calendar calendar = Calendar.getInstance();
        Task task = new Task(message, calendar);
        tasks.addTask(task);

        //Task exists in tasks with these values. Should be equal
        assertEquals(task, tasks.findTask(message, calendar));

        //No task exists in tasks with these values. Should be null
        Calendar laterCalendar = Calendar.getInstance();
        laterCalendar.add(Calendar.MINUTE, 10);
        assertNull(tasks.findTask(message, laterCalendar));
    }

    @Test
    void testTaskCompleted() {
        Calendar now = Calendar.getInstance();
        Task task = new Task("Test completion", now);
        tasks.addTask(task);

        //task should be removed when completed, should be null when trying to find it
        task.completeTask();
        assertNull(tasks.findTask("Test completion", now));
    }
}
