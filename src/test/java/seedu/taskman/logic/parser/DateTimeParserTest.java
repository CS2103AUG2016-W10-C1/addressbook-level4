package seedu.taskman.logic.parser;

import org.junit.Test;
import seedu.taskman.commons.exceptions.IllegalValueException;

import java.time.*;

import static java.time.temporal.TemporalAdjusters.next;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class DateTimeParserTest {
    private static final long timeDifferenceThreshold = 30L; // 30 seconds

    // specify time after date
    @Test
    public void parse_formalDateTime_success() throws Exception {
        String testDateTimeFormal = "07/05/16 2359";
        long testDateTimeUnix = 1467763140L;

        long unixDateTime = DateTimeParser.getUnixTime(testDateTimeFormal);
        assertEquals(testDateTimeUnix, unixDateTime);
    }

    @Test
    public void parse_formalTimeBeforeDate_exception() {
        String[] testCases = {"2359 07/05/16", "time 2359 07/05/16"};

        for (int i = 0; i < testCases.length; i++) {
            String testString = testCases[i];
            try {
                DateTimeParser.getUnixTime(testString);
            } catch (IllegalValueException e) {
                assertThat(e.getMessage(), is(DateTimeParser.TIME_BEFORE_DATE_ERROR));
            }
        }

    }

    @Test
    public void parse_relativeDate_success() throws Exception {
        long unixDateTime1 = DateTimeParser.getUnixTime("2 weeks from now");
        long unixDateTime2 = DateTimeParser.getUnixTime("in 2 weeks");

        long timeNow = Instant.now().getEpochSecond();
        long durationInSeconds = 2 * 7 * 24 * 60 * 60;

        assertEquals(timeNow + durationInSeconds, unixDateTime1);
        assertEquals(timeNow + durationInSeconds, unixDateTime2);
    }

    @Test
    public void parse_relativeDateTime_success() throws Exception {
        long parsedUnixTime = DateTimeParser.getUnixTime("wed 10am");

        ZonedDateTime input = OffsetDateTime.now().atZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime nextWed = input.with(next(DayOfWeek.WEDNESDAY))
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        assertEquals(nextWed.toEpochSecond(), parsedUnixTime);
    }

    @Test
    public void parse_durationSingle_success() throws Exception {
        String testDurationNatural = "3 days";
        long testDurationSeconds = 259200L;

        long timeNow = Instant.now().getEpochSecond();
        long expectedEndTime = timeNow + testDurationSeconds;
        long parsedTime = DateTimeParser.durationToUnixTime(timeNow, testDurationNatural);
        assertTrue(Math.abs(expectedEndTime - parsedTime) < timeDifferenceThreshold);
    }

    @Test
    public void parse_durationMultiple_success() throws Exception {
        String testDurationNatural = "3 days 3 hours";
        String testDurationNaturalComma = "3 days, 3 hours";

        long testDurationSeconds = 270000L;
        long timeNow = Instant.now().getEpochSecond();
        long expectedEndTime = timeNow + testDurationSeconds;

        long parsedTime = DateTimeParser.durationToUnixTime(timeNow, testDurationNatural);
        assertTrue(Math.abs(expectedEndTime - parsedTime) < timeDifferenceThreshold);

        long parsedTimeComma = DateTimeParser.durationToUnixTime(timeNow, testDurationNaturalComma);
        assertTrue(Math.abs(expectedEndTime - parsedTimeComma) < timeDifferenceThreshold);
    }

}
