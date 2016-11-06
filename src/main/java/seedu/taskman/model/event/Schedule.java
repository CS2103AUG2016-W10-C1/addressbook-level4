package seedu.taskman.model.event;

import com.google.common.base.Objects;

import seedu.taskman.commons.core.Messages;
import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.logic.Formatter;
import seedu.taskman.logic.parser.DateTimeParser;

import java.sql.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Schedule {

    public static final String MESSAGE_SCHEDULE_CONSTRAINTS =
            String.format(
                    "Date and time for scheduling should be in any of the format:\n" +
                    "1. DATETIME, DATETIME\n" +
                    "2. DATETIME %1$s DATETIME\n" +
                    "3. DATETIME %2$s DATETIME\n" +
                    "Refer to help for the full suite of date time parameter parsing.\n",
                    Formatter.ScheduleDivider.SCHEDULE.string,
                    Formatter.ScheduleDivider.DURATION.string);

    public static final String ERROR_NEGATIVE_DURATION = String.format(Messages.MESSAGE_INVALID_ARGUMENTS,
            "Duration is negative.");
    public static final String ERROR_BAD_DATETIME_START = String.format(Messages.MESSAGE_INVALID_ARGUMENTS,
            "Bad start datetime: %1$s");
    public static final String ERROR_BAD_DATETIME_END = String.format(Messages.MESSAGE_INVALID_ARGUMENTS,
            "Bad end datetime.");

    public static final String SCHEDULE_DIVIDER_GROUP = "((?:, )|(?: to )|(?: for ))";
    public static final String SCHEDULE_VALIDATION_REGEX = "(.*)" + SCHEDULE_DIVIDER_GROUP + "(.*)";

    public static final String STRING_NEXT_WEEK = "next";

    public final long startEpochSecond;
    public final long endEpochSecond;

    public Schedule(long startEpochSecond, long endEpochSecond) throws IllegalValueException {
        boolean isNegativeDuration = (endEpochSecond - startEpochSecond) < 0;
        if (startEpochSecond <= 0 || endEpochSecond <= 0 || isNegativeDuration) {
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
            boolean hasDuration = divider.contains(Formatter.ScheduleDivider.DURATION.string);

            try {
                startEpochSecond = DateTimeParser.getEpochTime(start);
            } catch (DateTimeParser.IllegalDateTimeException e) {
                throw new IllegalValueException(
                        String.format(
                                Formatter.FORMAT_TWO_LINES,
                                MESSAGE_SCHEDULE_CONSTRAINTS,
                                String.format(ERROR_BAD_DATETIME_START, start))
                );
            }

            try {
                if (hasDuration) {
                    String duration = matcher.group(3).trim();
                    endEpochSecond = DateTimeParser.toEndTime(startEpochSecond, duration);
                } else {
                    String endString = matcher.group(3).trim();
                    long endEpochCandidate = DateTimeParser.getEpochTime(endString);

                    endEpochSecond = (startEpochSecond > endEpochCandidate)
                            ? addNextToRelativeDateTime(endString)
                            : endEpochCandidate;
                }
            } catch (DateTimeParser.IllegalDateTimeException e) {
                throw new IllegalValueException(ERROR_BAD_DATETIME_END);
            }

            if (startEpochSecond > endEpochSecond) {
                throw new IllegalValueException(ERROR_NEGATIVE_DURATION);
            }
        }
    }

    private long addNextToRelativeDateTime(String dateTime) throws IllegalValueException {
        dateTime = String.format(Formatter.FORMAT_TWO_TERMS_SPACED_WITHIN_AFTER, STRING_NEXT_WEEK, dateTime).trim();
        try {
            return DateTimeParser.getEpochTime(dateTime);
        } catch (DateTimeParser.IllegalDateTimeException e) {
            throw new IllegalValueException(ERROR_BAD_DATETIME_END);
        }
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
        return String.format(
                Formatter.FORMAT_TWO_LINES,
                DateTimeParser.epochSecondToShortDateTime(startEpochSecond),
                DateTimeParser.epochSecondToShortDateTime(endEpochSecond)
        );
    }

    /**
     * Formats a string for displaying the schedule IN DETAIL to the form of:
     *
     *      DATE TIME (elapsed)
     *      DATE TIME
     *      Duration: DURATION
     *
     *      Example: Sat 05 Nov 16 3:25PM (Moments from now)
     *               Sat 05 Nov 16 8:00PM
     *               Duration: 4 hours 35 minutes
     *
     * If the time units are zero in value, it is not shown at all
     *      (i.e. 5 minutes instead of 0 hours 5 minutes)
     *
     * Assumes all months are 30 days long and all years are 365 days long
     *
     * @return String containing human-readable information for schedule (start, end, duration)
     */
    public String toStringSelected() {
        long durationSeconds = endEpochSecond - startEpochSecond;
        long durationMinutes = TimeUnit.SECONDS.toMinutes(durationSeconds);
        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        String durationString = "";

        if (durationMinutes >= Formatter.Duration.YEAR.count) {
            years = (int) Math.floor(durationMinutes / Formatter.Duration.YEAR.count);
            if (years > 0) {
                durationMinutes %= years * Formatter.Duration.YEAR.count;
                durationString += String.format(Formatter.FORMAT_TWO_TERMS_SPACED_WITHIN_AFTER, years, Formatter.Duration.YEAR.string);
            }
        }
        if (durationMinutes >= Formatter.Duration.MONTH.count) {
            months = (int) Math.floor(durationMinutes / Formatter.Duration.MONTH.count);
            if (months > 0) {
                durationMinutes %= months * Formatter.Duration.MONTH.count;
                durationString += String.format(Formatter.FORMAT_TWO_TERMS_SPACED_WITHIN_AFTER, months, Formatter.Duration.MONTH.string);
            }
        }
        if (durationMinutes >= Formatter.Duration.WEEK.count) {
            weeks = (int) Math.floor(durationMinutes / Formatter.Duration.WEEK.count);
            if (weeks > 0) {
                durationMinutes %= weeks * Formatter.Duration.WEEK.count;
                durationString += String.format(Formatter.FORMAT_TWO_TERMS_SPACED_WITHIN_AFTER, weeks, Formatter.Duration.WEEK.string);
            }
        }
        if (durationMinutes >= Formatter.Duration.DAY.count) {
            days = (int) Math.floor(durationMinutes / Formatter.Duration.DAY.count);
            if (days > 0) {
                durationMinutes %= days * Formatter.Duration.DAY.count;
                durationString += String.format(Formatter.FORMAT_TWO_TERMS_SPACED_WITHIN_AFTER, days, Formatter.Duration.DAY.string);
            }
        }
        if (durationMinutes >= Formatter.Duration.HOUR.count) {
            hours = (int) Math.floor(durationMinutes / Formatter.Duration.HOUR.count);
            if (hours > 0) {
                durationMinutes %= hours * Formatter.Duration.HOUR.count;
                durationString += String.format(Formatter.FORMAT_TWO_TERMS_SPACED_WITHIN_AFTER, hours, Formatter.Duration.HOUR.string);
            }
        }
        if (durationMinutes >= Formatter.Duration.MINUTE.count) {
            minutes = (int) Math.floor(durationMinutes);
            if (minutes > 0) {
                durationString += String.format(Formatter.FORMAT_TWO_TERMS_SPACED_WITHIN_AFTER, minutes, Formatter.Duration.MINUTE.string);
            }
        }

        return String.format(
                "%s (%s)\n\t%s\nDuration:\n\t%s",
                DateTimeParser.epochSecondToShortDateTime(startEpochSecond),
                Formatter.PRETTY_TIME.format(new Date(startEpochSecond * Formatter.MULTIPLIER_TIME_UNIX_TO_JAVA)),
                DateTimeParser.epochSecondToShortDateTime(endEpochSecond),
                durationString.trim());
    }

    public String toFormalString(){
        return String.format(Formatter.FORMAT_THREE_TERMS_SPACED_WITHIN,
                getFormalStartString(),
                Formatter.ScheduleDivider.SCHEDULE.string,
                getFormalEndString());
    }

    public String getFormalStartString(){
        return DateTimeParser.epochSecondToFormalDateTime(startEpochSecond);
    }

    public String getFormalEndString(){
        return  DateTimeParser.epochSecondToFormalDateTime(endEpochSecond);
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
