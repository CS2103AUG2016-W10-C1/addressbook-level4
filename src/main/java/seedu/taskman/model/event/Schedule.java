package seedu.taskman.model.event;

import com.google.common.base.Objects;

import org.ocpsoft.prettytime.PrettyTime;
import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.logic.parser.DateTimeParser;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@@author A0139019E
public class Schedule {
    // UG/DG: specify new datetime format
    // todo: indicate in example that format: "month-date-year time". there MUST be a space before time, not colon
    public static final String MESSAGE_SCHEDULE_CONSTRAINTS =
            "Task schedule should only contain dates and times in the format: " +
                    // DATETIME to DATETIME
                    DateTimeParser.DESCRIPTION_DATE_TIME_SHORT + " (a \",\" or \"to\") " +
                    DateTimeParser.DESCRIPTION_DATE_TIME_SHORT +
                    // DATETIME for DURATION
                    "\nOr the format:\n" + DateTimeParser.DESCRIPTION_DATE_TIME_SHORT + " for " +
                    DateTimeParser.DESCRIPTION_DURATION +
                    "\nDATETIME: " + DateTimeParser.DESCRIPTION_DATE_TIME_FULL;

    public static final String ERROR_NEGATIVE_DURATION = "Duration is negative!";
    public static final String ERROR_BAD_START_DATETIME = "Bad start datetime";
    public static final String ERROR_BAD_END_DATETIME = "Bad end datetime";

    public static final String SCHEDULE_DIVIDER_GROUP = "((?:, )|(?: to )|(?: for ))";
    public static final String SCHEDULE_VALIDATION_REGEX =
            "(.*)" + SCHEDULE_DIVIDER_GROUP + "(.*)";

    public static final String DURATION_STRING_MINUTES = "min";
    public static final String DURATION_STRING_HOURS = "hrs";
    public static final String DURATION_STRING_DAYS = "days";
    public static final String DURATION_STRING_WEEKS = "wks";
    public static final String DURATION_STRING_MONTHS = "mths";
    public static final String DURATION_STRING_YEARS = "yrs";

    public static final long DURATION_MINUTES_IN_HOUR = 60;
    public static final long DURATION_MINUTES_IN_DAY = DURATION_MINUTES_IN_HOUR * 24;
    public static final long DURATION_MINUTES_IN_WEEK = DURATION_MINUTES_IN_DAY * 7;
    public static final long DURATION_MINUTES_IN_MONTH = DURATION_MINUTES_IN_DAY * 30;
    public static final long DURATION_MINUTES_IN_YEAR = DURATION_MINUTES_IN_DAY * 365;
    public static final int MULTIPLIER_TIME_UNIX_TO_JAVA = 1000;
    public final long startEpochSecond;
    public final long endEpochSecond;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE");
    public final PrettyTime prettyTimeFormatter = new PrettyTime();

    public Schedule(long startEpochSecond, long endEpochSecond) throws IllegalValueException {

        boolean endIsBeforeStart = (endEpochSecond - startEpochSecond) < 0;
        if (startEpochSecond <= 0 || endEpochSecond <= 0 || endIsBeforeStart) {
            throw new IllegalValueException(ERROR_NEGATIVE_DURATION);
        }
        this.startEpochSecond = startEpochSecond;
        this.endEpochSecond = endEpochSecond;
    }

    public Schedule(String schedule) throws IllegalValueException {
        schedule = schedule.trim();
        Pattern pattern = Pattern.compile(SCHEDULE_VALIDATION_REGEX);
        Matcher matcher = pattern.matcher(schedule);
        if (!matcher.matches()) {
            throw new IllegalValueException(MESSAGE_SCHEDULE_CONSTRAINTS);
        } else {
            String start = matcher.group(1).trim();
            String divider = matcher.group(2).trim();
            boolean endingIsDuration = divider.contains("for");

            try {
                startEpochSecond = DateTimeParser.getUnixTime(start);
            } catch (DateTimeParser.IllegalDateTimeException e) {
                throw new IllegalValueException(
                        MESSAGE_SCHEDULE_CONSTRAINTS + "\n" +
                                ERROR_BAD_START_DATETIME + ", '" + start + "'");
            }

            if (endingIsDuration) {
                String duration = matcher.group(3).trim();
                endEpochSecond = DateTimeParser.naturalDurationToUnixTime(startEpochSecond, duration);
            } else {
                String endString = matcher.group(3).trim();
                long endEpochCandidate = DateTimeParser.getUnixTime(endString, ERROR_BAD_END_DATETIME);

                // user may have forgotten to type 'next' before the relative datetime
                // "sun 2359 to mon 2359" should be "... next mon 2359"
                endEpochSecond = (startEpochSecond > endEpochCandidate)
                        ? addNextToRelativeDateTime(endString)
                        : endEpochCandidate;
            }

            if (startEpochSecond > endEpochSecond) {
                throw new IllegalValueException(ERROR_NEGATIVE_DURATION);
            }
        }
    }

    private long addNextToRelativeDateTime(String dateTime) throws IllegalValueException {
        dateTime = "next " + dateTime;
        return DateTimeParser.getUnixTime(dateTime, ERROR_BAD_END_DATETIME);
    }

    public static boolean isValidSchedule(String test) {
        try {
            new Schedule(test);
            return true;
        } catch (IllegalValueException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return toStringDetailed();
    }

    public String toStringShort() {
        return String.format(
                "%s\n%s",
                DateTimeParser.epochSecondToShortDateTime(startEpochSecond),
                DateTimeParser.epochSecondToShortDateTime(endEpochSecond)
        );
    }

    /**
     * Formats a string for displaying the schedule IN DETAIL to the form of:
     *
     *      DATE TIME (elapsed) to DATE TIME (duration)
     *
     *      Example: 25-10-2016 23:15 (Moments from now) to 26-10-2016 04:00 (4 hours 45 minutes)
     *
     * For (duration), only pairs of time units are shown together
     *      (i.e. years and months, weeks and days, hours and minutes)
     *
     * If the time units are zero in value, it is not shown at all
     *      (i.e. 5 minutes instead of 0 hours 5 minutes)
     *
     * Assumes all months are 30 days long and all years are 365 days long
     *
     * @return String containing human-readable information for schedule (start, end, duration)
     */
    public String toStringDetailed() {
        long durationSeconds = endEpochSecond - startEpochSecond;
        long durationMinutes = TimeUnit.SECONDS.toMinutes(durationSeconds);
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        String durationString = "";

        if (durationMinutes >= DURATION_MINUTES_IN_YEAR) {
            years = (int) Math.floor(durationMinutes / DURATION_MINUTES_IN_YEAR);
            if (years > 0) {
                durationMinutes %= years * DURATION_MINUTES_IN_YEAR;
                durationString += String.format("%d %s ", years, DURATION_STRING_YEARS);
            }
        }
        if (durationMinutes >= DURATION_MINUTES_IN_MONTH) {
            months = (int) Math.floor(durationMinutes / DURATION_MINUTES_IN_MONTH);
            if (months > 0) {
                durationMinutes %= months * DURATION_MINUTES_IN_MONTH;
                durationString += String.format("%d %s ", months, DURATION_STRING_MONTHS);
            }
        }
        if (durationMinutes >= DURATION_MINUTES_IN_WEEK) {
            weeks = (int) Math.floor(durationMinutes / DURATION_MINUTES_IN_WEEK);
            if (weeks > 0) {
                durationMinutes %= weeks * DURATION_MINUTES_IN_WEEK;
                durationString += String.format("%d %s ", weeks, DURATION_STRING_WEEKS);
            }
        }
        if (durationMinutes >= DURATION_MINUTES_IN_DAY) {
            days = (int) Math.floor(durationMinutes / DURATION_MINUTES_IN_DAY);
            if (days > 0) {
                durationMinutes %= days * DURATION_MINUTES_IN_DAY;
                durationString += String.format("%d %s ", days, DURATION_STRING_DAYS);
            }
        }
        if (durationMinutes >= DURATION_MINUTES_IN_HOUR) {
            hours = (int) Math.floor(durationMinutes / DURATION_MINUTES_IN_HOUR);
            if (hours > 0) {
                durationMinutes %= hours * DURATION_MINUTES_IN_HOUR;
                durationString += String.format("%d %s ", hours, DURATION_STRING_HOURS);
            }
        }
        if (durationMinutes >= 1) {
            minutes = (int) Math.floor(durationMinutes);
            if (minutes > 0) {
                durationString += String.format("%d %s ", minutes, DURATION_STRING_MINUTES);
            }
        }

        return String.format(
                "%s\t(%s)\n%s\t(%s)",
                DateTimeParser.epochSecondToShortDateTime(startEpochSecond),
                prettyTimeFormatter.format(new Date(startEpochSecond * MULTIPLIER_TIME_UNIX_TO_JAVA)),
                DateTimeParser.epochSecondToShortDateTime(endEpochSecond),
                durationString.trim());
    }

    public String toFormalString(){
        return DateTimeParser.epochSecondToFormalDateTime(startEpochSecond)
                + " to "
                + DateTimeParser.epochSecondToFormalDateTime(endEpochSecond);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schedule schedule = (Schedule) o;
        return startEpochSecond == schedule.startEpochSecond &&
                endEpochSecond == schedule.endEpochSecond;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(startEpochSecond, endEpochSecond);
    }

}
