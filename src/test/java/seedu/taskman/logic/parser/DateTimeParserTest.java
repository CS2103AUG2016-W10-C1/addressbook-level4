package seedu.taskman.logic.parser;

import org.junit.Test;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.TemporalAdjusters.next;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

//@@author A0139019E

/**
 * Tests for DateTimeParser.java
 * DateTime parser is only accurate to the nearest minute
 * Some allowance is required when comparing durations
 */
public class DateTimeParserTest {
    private static final long timeDifferenceAllowance = 30L;

    @Test
    public void parse_formalDateTime_expectedMachineTime() throws Exception {
        String testDateTimeFormal = "07-05-16 2359";

        Calendar cal = new GregorianCalendar(2016, 6, 5, 23, 59);
        long unixDateTime = DateTimeParser.getEpochTime(testDateTimeFormal);
        assertEquals(cal.toInstant().getEpochSecond() ,unixDateTime);
    }


    @Test
    public void parse_dateTimeWithTimeZone_exceptionWithAppropriateMessage() {
        String[] testCases = {"07-05-2016 UTC+3 ", "07-05-2016 CST"};

        for (String testString : testCases) {
            try {
                DateTimeParser.getEpochTime(testString);
            } catch (DateTimeParser.IllegalDateTimeException e) {
                assertEquals(DateTimeParser.MESSAGE_ERROR_TIMEZONE_NOT_SUPPORTED, e.getMessage());
            }
        }
    }

    @Test
    public void parse_ambiguousDateTime_exceptionWithAppropriateMessage() {
        String[] testCases = {"2359 07-05-2016", "2359 06 Dec 2016"};

        for (String testString : testCases) {
            try {
                DateTimeParser.getEpochTime(testString);
            } catch (DateTimeParser.IllegalDateTimeException e) {
                assertEquals(DateTimeParser.MESSAGE_ERROR_AMBIGIOUS_TIME, e.getMessage());
            }
        }
    }

    @Test
    public void parse_relativeDateOnly_expectedMachineTime() throws Exception {
        long unixDateTime1 = DateTimeParser.getEpochTime("2 weeks from now");
        long unixDateTime2 = DateTimeParser.getEpochTime("in 2 weeks");

        long timeNow = Instant.now().getEpochSecond();
        long durationInSeconds = TimeUnit.DAYS.toSeconds(14);

        assertEquals(timeNow + durationInSeconds, unixDateTime1);
        assertEquals(timeNow + durationInSeconds, unixDateTime2);
    }

    @Test
    public void parse_relativeTimeOnly_expectedMachineTime() throws Exception {
        long parsedUnixTime = DateTimeParser.getEpochTime("10pm");

        ZonedDateTime now = OffsetDateTime.now().atZoneSameInstant(ZoneOffset.systemDefault());
        ZonedDateTime today10pm = now
                .withHour(22)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        assertEquals(today10pm.toEpochSecond(), parsedUnixTime);
    }

    @Test
    public void parse_relativeDateAndTime_expectedMachineTime() throws Exception {
        long parsedUnixTime = DateTimeParser.getEpochTime("wed 10am");

        ZonedDateTime timeNow = OffsetDateTime.now().atZoneSameInstant(ZoneOffset.systemDefault());
        ZonedDateTime nextWed = timeNow.with(next(DayOfWeek.WEDNESDAY))
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        assertEquals(nextWed.toEpochSecond(), parsedUnixTime);
    }

    @Test
    public void parse_durationSingleTemporalUnit_expectedMachineTime() throws Exception {
        String testDurationNatural = "3 days";
        long testDurationSeconds = 259200L;

        long parsedTime = DateTimeParser.naturalDurationToSeconds(testDurationNatural);

        assertTrue(Math.abs(testDurationSeconds - parsedTime) < timeDifferenceAllowance);
    }

    @Test
    public void parse_durationMultipleTemporalUnits_expectedMachineTime() throws Exception {
        String testDurationNatural = "3 days 3 hours";
        String testDurationNaturalComma = "3 days, 3 hours";
        long testDurationSeconds = 270000L;

        long parsedTime = DateTimeParser.naturalDurationToSeconds(testDurationNatural);
        assertTrue(Math.abs(testDurationSeconds - parsedTime) < timeDifferenceAllowance);

        long parsedTimeComma = DateTimeParser.naturalDurationToSeconds(testDurationNaturalComma);
        assertTrue(Math.abs(testDurationSeconds - parsedTimeComma) < timeDifferenceAllowance);
    }

    @Test
    public void parse_startTimeAndDuration_expectedEndTime() throws Exception {
        String testDurationNatural = "3 days 3 hours";
        long testDurationSeconds = 270000L;
        long timeNow = Instant.now().getEpochSecond();
        long expectedEndTime = timeNow + testDurationSeconds;

        long parsedTime = DateTimeParser.toEndTime(timeNow, testDurationNatural);
        assertTrue(Math.abs(expectedEndTime - parsedTime) < timeDifferenceAllowance);
    }

}
