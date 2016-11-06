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
    public enum ScheduleDivider {
        DURATION    ("for"),
        SCHEDULE    ("to"),
        SCHEDULE_ALTERNATIVE(",");

        public String string;

        ScheduleDivider(String string) {
            this.string = string;
        }

        @Override
        public String toString() {
            return string;
        }
    }

    public static final String MESSAGE_SCHEDULE_CONSTRAINTS =
            String.format(
                    "Date and time for scheduling should be in any of the following formats:\n" +
                    "1. DATETIME %1$s DATETIME\n" +
                    "2. DATETIME %2$s DATETIME\n" +
                    "3. DATETIME %3$s DATETIME\n",
                    ScheduleDivider.SCHEDULE.string,
                    ScheduleDivider.SCHEDULE_ALTERNATIVE.string,
                    ScheduleDivider.DURATION.string);

    public static final String ERROR_NEGATIVE_DURATION = String.format(Messages.MESSAGE_INVALID_ARGUMENTS,
            "Duration is negative");
    public static final String ERROR_BAD_DATETIME_START = String.format(Messages.MESSAGE_INVALID_ARGUMENTS,
            "Bad start datetime, %1$s");
    public static final String ERROR_BAD_DATETIME_END = String.format(Messages.MESSAGE_INVALID_ARGUMENTS,
            "Bad end datetime, %1$s");
    public static final String ERROR_BAD_DURATION = String.format(Messages.MESSAGE_INVALID_ARGUMENTS,
            "Bad duration, %1$s");

    private static final String SCHEDULE_DIVIDER_GROUP = "((?:, )|(?: to )|(?: for ))";
    private static final String SCHEDULE_VALIDATION_REGEX = "(.*?)" + SCHEDULE_DIVIDER_GROUP + "(.*)";

    public final long startEpochSecond;
    public final long endEpochSecond;

    /**
     * Convenience method for creation from machine readable start & end times
     */
    public Schedule(long startEpochSecond, long endEpochSecond) throws IllegalValueException {
        boolean isDurationNegative = (endEpochSecond - startEpochSecond) < 0;
        if (isDurationNegative) {
            throw new IllegalValueException(ERROR_NEGATIVE_DURATION);
        }

        this.startEpochSecond = startEpochSecond;
        this.endEpochSecond = endEpochSecond;
    }

    /**
     * Parses a string in natural language to create a schedule object
     * A schedule consists of a start & end time
     *
     * Three formats are accepted:
     * "start time, end time",
     * "start time to end time",
     * "start time for duration"
     *
     * @throws IllegalValueException when input strays from the formats,
     * or when the start/end time/duration cannot be parsed
     */
    public Schedule(String schedule) throws IllegalValueException {
        schedule = schedule.trim();
        Pattern pattern = Pattern.compile(SCHEDULE_VALIDATION_REGEX);
        Matcher matcher = pattern.matcher(schedule);
        if (!matcher.matches()) {
            throw new IllegalValueException(MESSAGE_SCHEDULE_CONSTRAINTS);
        } else {
            String rawStartTime = matcher.group(1).trim();
            startEpochSecond = parseRawStartTime(rawStartTime);

            String divider = matcher.group(2).trim();
            boolean scheduleUsesDuration = divider.contains(ScheduleDivider.DURATION.string);

            String rawScheduleEnding = matcher.group(3).trim();
            endEpochSecond = scheduleUsesDuration
                    ? convertRawDurationToEndTime(rawScheduleEnding, startEpochSecond)
                    : parseRawEndTime(rawScheduleEnding, startEpochSecond);

            if (startEpochSecond > endEpochSecond) {
                throw new IllegalValueException(ERROR_NEGATIVE_DURATION);
            }
        }
    }

    private long parseRawStartTime(String rawStartTime) throws IllegalValueException {
        try {
            return DateTimeParser.getEpochTime(rawStartTime);
        } catch (DateTimeParser.IllegalDateTimeException e) {

            String errorMessage = Formatter.appendWithNewlines(
                    String.format(ERROR_BAD_DATETIME_START, rawStartTime),
                    e.getMessage()
            );
            throw new IllegalValueException(errorMessage);
        }
    }

    private long parseRawEndTime(String rawEndTime, long startEpochSecond) throws IllegalValueException {
        // User could be referring to a future occurrence of the specified end time
        // but did not specify as such. Explicitly indicate 'next' in end time if required

        // Illustration of problem:
        // Time now - Sunday 10pm,
        // Inputs: Start Time - Friday, End Time - Monday
        // Since next Monday comes before next Friday, this input is illegal if not rectified

        try {
            long candidateResult = DateTimeParser.getEpochTime(rawEndTime);

            boolean tryNextOccurrenceOfEndTime = startEpochSecond > candidateResult;
            if (tryNextOccurrenceOfEndTime) {
                String revisedEndTime = "next " + rawEndTime;
                return DateTimeParser.getEpochTime(revisedEndTime);
            } else {
                return candidateResult;
            }

        } catch (DateTimeParser.IllegalDateTimeException e) {

            String errorMessage = Formatter.appendWithNewlines(
                    String.format(ERROR_BAD_DATETIME_END, rawEndTime),
                    e.getMessage()
            );
            throw new IllegalValueException(errorMessage);
        }
    }

    private long convertRawDurationToEndTime(String rawDuration, long startEpochSecond) throws IllegalValueException {
        try {
            return DateTimeParser.toEndTime(startEpochSecond, rawDuration);
        } catch (DateTimeParser.IllegalDateTimeException e) {

            String errorMessage = Formatter.appendWithNewlines(
                    String.format(ERROR_BAD_DURATION, rawDuration),
                    e.getMessage()
            );
            throw new IllegalValueException(errorMessage);
        }
    }

    @Override
    public String toString() {
        return Formatter.appendWithNewlines(
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
                ScheduleDivider.SCHEDULE.string,
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
