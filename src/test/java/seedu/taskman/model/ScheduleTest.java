package seedu.taskman.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.model.event.Frequency;
import seedu.taskman.model.event.Schedule;

import static junit.framework.TestCase.assertEquals;

public class ScheduleTest {

    @Test
    public void create_dateTimeDateTime_success() throws IllegalValueException {
        String start = "05/07/2016 0001";
        String end = "07/07/2016 0002";

        Schedule schedule = new Schedule(start + ", " + end);
        new Schedule(start + " to " + end);

        assertEquals(schedule.startEpochSecond, 1462579260);
        assertEquals(schedule.endEpochSecond, 1467849720);
    }

    @Test
    public void create_dateTimeDuration_success() throws IllegalValueException {
        String start = "05/07/2016 0001";
        String duration = "2 hours";

        new Schedule(start + " for " + duration);
    }

    @Test
    public void create_relativeDateTimeForgotNext_success() throws IllegalValueException {
        String start = "sun 2359";
        String end = "mon 2359";

        new Schedule(start + " to " + end);
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void create_dadDivider_exception() throws IllegalValueException {
        String start = "05/07/2016 0001";
        String end = "07/07/2016 0002";

        exception.expect(IllegalValueException.class);
        new Schedule(start + " bad div " + end);
    }
}
