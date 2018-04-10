import org.junit.jupiter.api.Test;

import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskTest {

    @Test
    public void testEquals() {

        Calendar earlier = Calendar.getInstance();
        Calendar later = Calendar.getInstance();
        //Arbitrary date
        earlier.set(2000, Calendar.APRIL, 16, 1, 1);
        later.set(2001, Calendar.APRIL, 16, 1, 1);

        //Message is different, time is different. Should be false
        Task earlierTask = new Task("Earlier task", earlier);
        Task laterTask = new Task("Later task", later);
        assertFalse(earlierTask.equals(laterTask));

        //Message is same, time is different. Should be false
        Task sameTaskMessage = new Task("Earlier task", later);
        assertFalse(earlierTask.equals(sameTaskMessage));

        //Message is different, time is the same. Should be false
        Task sameCalendar = new Task("Different message", earlier);
        assertFalse(earlierTask.equals(sameCalendar));

        //Message and time to the minute are the same. Should be true
        Calendar sameCalendarAsEarlier = Calendar.getInstance();
        sameCalendarAsEarlier.set(2000, Calendar.APRIL, 16, 1, 1);
        Task sameTask = new Task("Earlier task", sameCalendarAsEarlier);
        assertTrue(earlierTask.equals(sameTask));
    }

}
