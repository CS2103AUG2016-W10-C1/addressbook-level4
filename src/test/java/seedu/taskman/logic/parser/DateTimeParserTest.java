package seedu.taskman.logic.parser;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void getEpochTime_formalDateTime_expectedEpochTime() throws Exception {
        String testDateTimeFormal = "07-05-16 2359";

        Calendar cal = new GregorianCalendar(2016, 6, 5, 23, 59);
        long unixDateTime = DateTimeParser.getEpochTime(testDateTimeFormal);
        assertEquals(cal.toInstant().getEpochSecond() ,unixDateTime);
    }

    @Test
    public void getEpochTime_unknownDateTime_exceptionAboutUnknownDateTime()
            throws DateTimeParser.IllegalDateTimeException {

        String testDateTime = "unknown";
        exception.expectMessage(DateTimeParser.MESSAGE_ERROR_UNKNOWN_DATETIME);
        DateTimeParser.getEpochTime(testDateTime);
    }

    @Test
    public void getEpochTime_dateTimesWithTimeZone_exceptionAboutTimeZone() {
        String[] testCases = {"07-05-2016 UTC+3 ",
                "07-05-2016 CST",
        };

        for (String testString : testCases) {
            try {
                DateTimeParser.getEpochTime(testString);
            } catch (DateTimeParser.IllegalDateTimeException e) {
                assertTrue(e.getMessage().contains(DateTimeParser.MESSAGE_ERROR_TIMEZONE_NOT_SUPPORTED));
            }
        }
    }

    @Test
    public void getEpochTime_dateTimeWithoutTimezone_success() throws Exception {
        String testCase = "yesterday 4pm"; // contains 'est', but should not throw exception

        // assume that parsing gives the correct result, it's not our job here
        DateTimeParser.getEpochTime(testCase);
    }

    @Test
    public void getEpochTime_ambiguousDateTime_exceptionAboutAmbiguousTime() {
        String[] testCases = {"2359 07-05-2016", "2359 06 Dec 2016"};

        for (String testString : testCases) {
            try {
                DateTimeParser.getEpochTime(testString);
            } catch (DateTimeParser.IllegalDateTimeException e) {
                assertEquals(DateTimeParser.MESSAGE_ERROR_AMBIGUOUS_TIME, e.getMessage());
            }
        }
    }

    @Test
    public void getEpochTime_relativeDateOnly_expectedMachineTime() throws Exception {
        long unixDateTime1 = DateTimeParser.getEpochTime("2 weeks from now");
        long unixDateTime2 = DateTimeParser.getEpochTime("in 2 weeks");

        long timeNow = Instant.now().getEpochSecond();
        long durationInSeconds = TimeUnit.DAYS.toSeconds(14);

        assertEquals(timeNow + durationInSeconds, unixDateTime1);
        assertEquals(timeNow + durationInSeconds, unixDateTime2);
    }

    @Test
    public void getEpochTime_relativeTimeOnly_expectedMachineTime() throws Exception {
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
    public void getEpochTime_relativeDateAndTime_expectedMachineTime() throws Exception {
        long parsedEpochTime = DateTimeParser.getEpochTime("wed 10am");

        ZonedDateTime timeNow = OffsetDateTime.now().atZoneSameInstant(ZoneOffset.systemDefault());
        ZonedDateTime nextWed = timeNow.with(next(DayOfWeek.WEDNESDAY))
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        assertEquals(nextWed.toEpochSecond(), parsedEpochTime);
    }

    @Test
    public void getDuration_durationWithSingleTemporalUnit_expectedDuration() throws Exception {
        String testDurationNatural = "3 days";
        long testDurationSeconds = 259200L;

        long parsedTime = DateTimeParser.naturalDurationToSeconds(testDurationNatural);

        assertTrue(Math.abs(testDurationSeconds - parsedTime) < timeDifferenceAllowance);
    }

    @Test
    public void getDuration_durationWithMultipleTemporalUnits_expectedDuration() throws Exception {
        String testDurationNatural = "3 days 3 hours";
        String testDurationNaturalComma = "3 days, 3 hours";
        long testDurationSeconds = 270000L;

        long parsedTime = DateTimeParser.naturalDurationToSeconds(testDurationNatural);
        assertTrue(Math.abs(testDurationSeconds - parsedTime) < timeDifferenceAllowance);

        long parsedTimeComma = DateTimeParser.naturalDurationToSeconds(testDurationNaturalComma);
        assertTrue(Math.abs(testDurationSeconds - parsedTimeComma) < timeDifferenceAllowance);
    }

    @Test
    public void getEndTime_startTimeAndDuration_expectedEndTime() throws Exception {
        // use a start time & duration to get end time

        String testDurationNatural = "3 days 3 hours";
        long testDurationSeconds = 270000L;
        long timeNow = Instant.now().getEpochSecond();
        long expectedEndTime = timeNow + testDurationSeconds;

        long parsedTime = DateTimeParser.toEndTime(timeNow, testDurationNatural);
        assertTrue(Math.abs(expectedEndTime - parsedTime) < timeDifferenceAllowance);
    }


    @Test
    public void getDuration_nonConformingDuration_exceptionWithDurationConformance()
            throws DateTimeParser.IllegalDateTimeException {

        String testDurationNatural = "3 days and 3 hours";

        exception.expectMessage(DateTimeParser.MESSAGE_ERROR_NON_CONFORMING_DURATION);
        DateTimeParser.naturalDurationToSeconds(testDurationNatural);
    }

}
