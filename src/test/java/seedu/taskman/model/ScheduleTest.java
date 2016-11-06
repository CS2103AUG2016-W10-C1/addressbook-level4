package seedu.taskman.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.model.event.Schedule;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.TemporalAdjusters.next;
import static junit.framework.TestCase.assertEquals;

public class ScheduleTest {

    //@@author A0139019E
    @Test
    public void schedule_twoDateTimes_success() throws IllegalValueException {

        String start = "05/07/2016 00:01";
        String end = "07/07/2016 00:02";

        Schedule schedule = new Schedule(start + " " +
                Schedule.ScheduleDivider.SCHEDULE + " " +
                end);

        // create without storing object to test for exception
        new Schedule(start + " " +
                Schedule.ScheduleDivider.SCHEDULE_ALTERNATIVE + " "
                + end);

        // note that month value is zero based
        Calendar startCal = new GregorianCalendar(2016, 4, 7, 0, 1);
        Calendar endCal = new GregorianCalendar(2016, 6, 7, 0, 2);

        assertEquals("Something went wrong with parsing the start time",
                startCal.toInstant().getEpochSecond(), schedule.startEpochSecond);
        assertEquals("Something went wrong with parsing the end time",
                endCal.toInstant().getEpochSecond(), schedule.endEpochSecond);
    }

    @Test
    public void schedule_dateTimeWithDuration_success() throws IllegalValueException {
        String start = "05/07/2016 00:01";
        int numHours = 2, numMinutes = 1;
        String duration =
                numHours + " hours, " +
                numMinutes + " min";

        Schedule schedule = new Schedule(start + " for " + duration);

        Calendar startCal = new GregorianCalendar(2016, 4, 7, 0, 1);
        long expectedStartTime = startCal.toInstant().getEpochSecond();
        long expectedEndTime = expectedStartTime +
                TimeUnit.HOURS.toSeconds(numHours) +
                TimeUnit.MINUTES.toSeconds(numMinutes);

        assertEquals("Something went wrong with parsing the start time",
                expectedStartTime, schedule.startEpochSecond);
        assertEquals("Something went wrong with parsing the end time",
                expectedEndTime, schedule.endEpochSecond);
    }

    /**
     * User might be implicitly referring to the next occurrence of the endTime
     * Ensure this case is handled (refer to end time handling in actual class for specifics)
     */
    @Test
    public void schedule_implicitNextOccurrenceForEndTime_success() throws IllegalValueException {
        String start = "sun 2359";
        String end = "mon 0000";

        ZonedDateTime timeNow = OffsetDateTime.now().atZoneSameInstant(ZoneOffset.systemDefault());
        ZonedDateTime expectedStartDateTime = timeNow.with(next(DayOfWeek.SUNDAY))
                .withHour(23)
                .withMinute(59)
                .withSecond(0)
                .withNano(0);
        ZonedDateTime expectedEndDateTime = expectedStartDateTime.with(next(DayOfWeek.MONDAY))
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        Schedule schedule = new Schedule(start + " to " + end);

        assertEquals("Something went wrong with parsing the start time",
                expectedStartDateTime.toEpochSecond(), schedule.startEpochSecond);
        assertEquals("Something went wrong with parsing the end time",
                expectedEndDateTime.toEpochSecond(), schedule.endEpochSecond);
    }

    @Test
    public void create_convenienceConstructor_success() throws IllegalValueException {

    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void schedule_useBadDivider_failureWithCorrectMessage() throws IllegalValueException {
        String start = "05/07/2016 0001";
        String end = "07/07/2016 0002";

        exception.expect(IllegalValueException.class);
        new Schedule(start + " bad divider " + end);
    }

    // TODO: write tests
    @Test
    public void schedule_negativeDurationMainConstructor_failureWithCorrectMessage() {

    }

    @Test
    public void schedule_negativeDurationConvenienceConstructor_failureWithCorrectMessage() {

    }

    @Test
    public void schedule_unknownDateTime_failureWithCorrectMessage() {

    }

    @Test
    public void schedule_unknownDuration_failureWithCorrectMessage() {

    }

    //@@author
}
