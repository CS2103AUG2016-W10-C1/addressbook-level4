package seedu.taskman.model.event;

import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.logic.parser.DateTimeParser;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeSlot {
    // todo: indicate in example that format: "month-date-year time". there MUST be a space before time, not colon
    // todo: remove all traces of schedule
    public static final String MESSAGE_TIMESLOT_CONSTRAINTS =
            "Task schedule should only contain dates and times in the format: " +

                    // DATETIME to DATETIME
                    DateTimeParser.DESCRIPTION_DATE_TIME_SHORT + " (a \",\" or \"to\") " +
                    DateTimeParser.DESCRIPTION_DATE_TIME_SHORT +

                    "\nOr the format:\n"

                    // DATETIME for DURATION
                    + DateTimeParser.DESCRIPTION_DATE_TIME_SHORT + " for " +
                    DateTimeParser.DESCRIPTION_DURATION +
                    "\nDATETIME: " + DateTimeParser.DESCRIPTION_DATE_TIME_FULL;

    public static final String ERROR_BAD_START_DATETIME = "Invalid start datetime";
    public static final String ERROR_BAD_END_DATETIME = "Invalid end datetime";

    public static final String SCHEDULE_DIVIDER_GROUP = "((?:, )|(?: to )|(?: for ))";
    public static final String TIMESLOT_VALIDATION_REGEX = "(.*)" + SCHEDULE_DIVIDER_GROUP + "(.*)";
    public static final String ERROR_NEGATIVE_DURATION = "Duration is negative";
    public final long startEpochSecond;
    public final long endEpochSecond;

    public TimeSlot(long startEpochSecond, long endEpochSecond) throws IllegalValueException {
        if (startEpochSecond > endEpochSecond) {
            throw new IllegalValueException(ERROR_NEGATIVE_DURATION);
        }
        this.startEpochSecond = startEpochSecond;
        this.endEpochSecond = endEpochSecond;
    }

    public TimeSlot(String timeSlot) throws IllegalValueException {
        timeSlot = timeSlot.trim();
        Pattern pattern = Pattern.compile(TIMESLOT_VALIDATION_REGEX);
        Matcher matcher = pattern.matcher(timeSlot);
        if (!matcher.matches()) {
            throw new IllegalValueException(MESSAGE_TIMESLOT_CONSTRAINTS);
        } else {
            String start = matcher.group(1).trim();
            String divider = matcher.group(2).trim();
            boolean endingIsDuration = divider.contains("for");

            try {
                startEpochSecond = DateTimeParser.getUnixTime(start);
            } catch (DateTimeParser.IllegalDateTimeException e) {
                throw new IllegalValueException(
                        MESSAGE_TIMESLOT_CONSTRAINTS + "\n" +
                        ERROR_BAD_START_DATETIME + ", '" + start + "'");
            }

            if (endingIsDuration) {
                String duration = matcher.group(3).trim();
                endEpochSecond = DateTimeParser.durationToUnixTime(startEpochSecond, duration);
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
        dateTime = "next " + dateTime ;
        return DateTimeParser.getUnixTime(dateTime , ERROR_BAD_END_DATETIME);
    }

    @Override
    public String toString() {
        return DateTimeParser.epochSecondToShortDateTime(startEpochSecond) +
                " to\n" +
                DateTimeParser.epochSecondToDetailedDateTime(endEpochSecond);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return startEpochSecond == timeSlot.startEpochSecond &&
                endEpochSecond == timeSlot.endEpochSecond;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startEpochSecond, endEpochSecond);
    }
}
