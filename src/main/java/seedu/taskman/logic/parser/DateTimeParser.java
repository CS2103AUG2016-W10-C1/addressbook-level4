package seedu.taskman.logic.parser;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;
import seedu.taskman.commons.exceptions.IllegalValueException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generates machine readable datetime from DateTimes or Durations in natural language
 *
 * A DateTime is defined as a Date, followed by a Time (optional).
 * Examples: 05-07-1994 05:00, Tuesday 7pm, next Monday
 * Durations are defined as X min/hour/day/week/month/years, X being a number
 *
 * Uses Natty internally to do the heavy lifting.
 */
public class DateTimeParser {
    public static final String DESCRIPTION_DATE_TIME_FULL =
            "can use natural language, eg: 2nd Wed from now, 9pm";
    public static final String DESCRIPTION_DATE_TIME_SHORT = "DATE & TIME";
    public static final String SINGLE_DURATION =
            "(?:[1-9]+[0-9]*) (?:(?:min)|(?:hour)|(?:day)|(?:week)|(?:month)|(?:year))s?";
    public static final String MULTIPLE_DURATION =
            "(" + SINGLE_DURATION + ",? ?)+";
    public static final String DESCRIPTION_DURATION = "<number> <min/hour/day/week/month/year(s)>";
    public static final String TIME_BEFORE_DATE_ERROR = "Do not enter time before date";
    public static final String SPECIFYING_TIMEZONE_NOT_SUPPORTED = "Currently does not support specifying of timezones.";
    private static final String GENERIC_ERROR_DATETIME = "Invalid date time";
    private static final String GENERIC_ERROR_DURATION = "Invalid duration";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-YYYY HH:mm");
    private static final Parser parser = new Parser();

    /**
     * Converts a date & time in natural language to unix time (seconds)
     * Does not support specifying of timezones
     */
    public static long getUnixTime(String naturalDateTime, String errorMessage) throws IllegalDateTimeException {
        if (timeIsBeforeDate(naturalDateTime)) {
            throw new IllegalDateTimeException(TIME_BEFORE_DATE_ERROR);
        }

        if (hasTimeZoneSpecified(naturalDateTime)) {
            throw new IllegalDateTimeException(SPECIFYING_TIMEZONE_NOT_SUPPORTED);
        }

        String timeZoneCorrected = appendLocalTimeZone(naturalDateTime);
        List<DateGroup> groups = parser.parse(timeZoneCorrected);

        // assumes the first DateGroup & Date object in the group provided by Natty is correct
        try {
            if (groups.isEmpty()) {
                throw new IllegalDateTimeException();
            } else {
                DateGroup group = groups.get(0);
                Date date = getFirstDate(group.getDates());
                return date.toInstant().getEpochSecond();
            }
        } catch (IllegalDateTimeException e) {
            throw new IllegalDateTimeException(errorMessage);
        }
    }

    private static boolean hasTimeZoneSpecified(String naturalDateTime) {
        Set<String> immutableTimezones = ZoneId.SHORT_IDS.keySet();
        Set<String> timezones = new HashSet<>(immutableTimezones);
        timezones.add("UTC");
        timezones.add("GMT");

        for (String timezone : timezones) {
            if (naturalDateTime.contains(timezone) ||
                    naturalDateTime.contains(timezone.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    private static boolean timeIsBeforeDate(String naturalDateTime) {
        // simple check to ensure time (hour:mins:seconds) is present before Date
        return naturalDateTime.matches(".* \\d{4}.*")  &&
                !naturalDateTime.matches(".* \\d{4}$");
    }

    private static String appendLocalTimeZone(String dateTime) {
        return dateTime + " " + TimeZone.getDefault().getID();
    }

    public static long getUnixTime(String naturalDateTime) throws IllegalDateTimeException {
        return getUnixTime(naturalDateTime, GENERIC_ERROR_DATETIME);
    }

    private static Date getFirstDate(List<Date> dates) throws IllegalDateTimeException {
        if (dates.isEmpty()) {
            throw new IllegalDateTimeException("No first date");
        } else {
            return dates.get(0);
        }
    }

    /**
     * Uses a start time to convert a natural duration to an end time
     * Start & End time are in unix time, in seconds
     */
    public static long naturalDurationToUnixTime(long startUnixTime, String naturalDuration) throws IllegalDateTimeException {
        long endUnixTime = startUnixTime + naturalDurationToSeconds(naturalDuration);
        if (endUnixTime < startUnixTime) {
            throw new IllegalDateTimeException(GENERIC_ERROR_DURATION);
        } else {
            return endUnixTime;
        }
    }

    public static long naturalDurationToSeconds(String naturalDuration) throws IllegalDateTimeException {
        if (!naturalDuration.matches(MULTIPLE_DURATION)) {
            throw new IllegalDateTimeException("failed to match regex");
        } else {
            // Natty does not have support for natural durations
            // Parse durations as relative DateTimes into Natty & subtract from current time

            long unixTimeNow = Instant.now().getEpochSecond();
            long actualDurationSeconds = 0;

            Pattern firstDuration = Pattern.compile(SINGLE_DURATION);
            Matcher matcher = firstDuration.matcher(naturalDuration);
            while (matcher.find()) {
                actualDurationSeconds += getUnixTime(matcher.group()) - unixTimeNow;
            }

            return actualDurationSeconds;
        }
    }

    public static String epochSecondToDetailedDateTime(long epochSecond) {
        Instant instant = Instant.ofEpochSecond(epochSecond);
        return ZonedDateTime
        		.ofInstant(instant, ZoneId.systemDefault())
        		.format(formatter);
    }

    public static String epochSecondToShortDateTime(long epochSecond) {
        Instant instant = Instant.ofEpochSecond(epochSecond);
        return LocalDateTime
        		.ofInstant(instant, ZoneId.systemDefault())
        		.format(formatter);
    }

    public static class IllegalDateTimeException extends IllegalValueException {
        public IllegalDateTimeException() {
            super();
        }

        public IllegalDateTimeException(String message) {
            super(message);
        }
    }
}
