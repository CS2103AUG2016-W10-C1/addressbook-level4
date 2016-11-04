package seedu.taskman.logic.parser;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@@author A0139019E
/**
 * Generates Unix time (seconds) from "DateTimes" or "Durations" in natural language
 * Examples: "2nd Wed from now, 9pm" , "09-07-2015 23:45"
 *
 * A DateTime is defined as a Date and a Time. Only one field needs to be present.
 * Durations are defined as X min/hour/day/week/month/years, X being a number
 *
 * Durations & times returned are only accurate to the nearest minute.
 * Uses Natty internally to do the heavy lifting
 */
public class DateTimeParser {
    public static final String REGEX_DURATION_SINGLE =  "(?:[1-9]+[0-9]*) " + // quantity
            "(?:(?:minute)|(?:min)|(?:hrs)|(?:hour)|(?:day)|(?:week)|(?:month)|(?:year))s?"; // temporal units
    public static final String REGEX_DURATION_MULTIPLE = "(" + REGEX_DURATION_SINGLE + ",? ?)+";

    public static final String DATE_TIME_EXAMPLES =
            "Examples: '2nd Wed from now, 9pm', '09-07-15 23:45', '3pm' \n" +
            "Both natural & formal date times are accepted" + " (formal dates in MM-DD-YY format)";

    public static final String MESSAGE_ERROR_AMBIGIOUS_TIME = "DateTime entered is ambiguous. Please enter the time in the HH:MM format instead";
    public static final String MESSAGE_ERROR_TIMEZONE_NOT_SUPPORTED = "Please omit timezones. Specifying timezones is currently not supported";
    public static final String MESSAGE_ERROR_UNKNOWN_DATETIME = "Invalid DateTime, please try the following: " + DATE_TIME_EXAMPLES;
    public static final String MESSAGE_ERROR_END_IS_BEFORE_START = "Invalid Duration, end is before start";
    public static final String MESSAGE_ERROR_NON_CONFORMING_DURATION =
            "Invalid Duration, please use the following temporal units: min/hour/day/week/month/year(s)";

    private static final Parser NATTY_PARSER = new Parser();
    //@@author

    private static final DateTimeFormatter FORMATTER_DISPLAY = DateTimeFormatter.ofPattern("EEE, dd MMM YY, h.mma");
    private static final DateTimeFormatter FORMATTER_FORMAL = DateTimeFormatter.ofPattern("dd MMM YYYY HH:mm");
    private enum TimeZoneEnum {
        LOCAL, ZONED
    }

    //@@author A0139019E

    /**
     * Converts a date & time in natural language to unix time (seconds)
     * Disallows specifying of timezones to improve accuracy in conversion
     */
    public static long getUnixTime(String naturalDateTime) throws IllegalDateTimeException {
        String preProcessedDateTime = preProcessNaturalDateTime(naturalDateTime);

        Optional<Date> dateOptional = parseNaturalDateTime(preProcessedDateTime);
        Date date = dateOptional.orElseThrow(() -> new IllegalDateTimeException(MESSAGE_ERROR_UNKNOWN_DATETIME));

        return date.toInstant().getEpochSecond();
    }

    /**
     * Pre-processes a user entered DateTime, making modifications to improve parsing results
     * Also rejects early if the DateTime fails to meet requirements
     */
    private static String preProcessNaturalDateTime(String rawNaturalDateTime) throws IllegalDateTimeException {

        // reject multiple groups of 4 consecutive digits, might result in inaccurate conversion
        // eg: "2016 2359", "2016 05 07 2017", where it is difficult to guess what the user wants

        boolean isAmbiguous = hasMultipleGroupsOfFourDigits(rawNaturalDateTime);
        if (isAmbiguous) {
            throw new IllegalDateTimeException(MESSAGE_ERROR_AMBIGIOUS_TIME);
        }

        // TimeZones are currently not supported, due to difficulty in specifying UTC & GMT for Natty
        if (containsTimeZone(rawNaturalDateTime)) {
            throw new IllegalDateTimeException(MESSAGE_ERROR_TIMEZONE_NOT_SUPPORTED);
        }

        // Append local timezone to eliminate timezone ambiguity for relative DateTimes,
        // formal DateTimes and relaxed DateTimes (refer to Natty documentation for more info)
        return appendLocalTimeZone(rawNaturalDateTime);
    }

    private static boolean hasMultipleGroupsOfFourDigits(String str) {
        return str.matches(".*\\d{4}.*\\d{4}.*");
    }

    /**
     * Parse a natural DateTime string & return a Date
     */
    private static Optional<Date> parseNaturalDateTime(String naturalDateTime) throws IllegalDateTimeException {
        List<DateGroup> dateGroups = NATTY_PARSER.parse(naturalDateTime);

        // Assume the first DateGroup object provided by Natty contains the desired result
        if (dateGroups.isEmpty()) {
            return Optional.empty();
        }

        DateGroup group = dateGroups.get(0);

        // Check for ignored digits, rejecting early if they exist
        // Ignored digits give a high probability of an inaccurate conversion
        boolean resultIgnoresDigits = group.getFullText().replace(group.getText(), "").matches(".*\\d+.*");
        if (resultIgnoresDigits) {
            return Optional.empty();
        }

        // Assume the first Date object provided gives the desired result
        List<Date> dates = group.getDates();
        if (dates.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(dates.get(0));
    }

    /**
     * Checks if the string contains any common timezone
     */
    private static boolean containsTimeZone(String naturalDateTime) {
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

    private static String appendLocalTimeZone(String dateTime) {
        return dateTime + " " + java.util.TimeZone.getDefault().getID();
    }

    /**
     * Calculates the end time from a start time & duration
     * Start time & end time in Unix time (seconds)
     */
    public static long toEndTime(long startUnixTime, String naturalDuration) throws IllegalDateTimeException {
        long endUnixTime = startUnixTime + naturalDurationToSeconds(naturalDuration);
        if (endUnixTime < startUnixTime) {
            throw new IllegalDateTimeException(MESSAGE_ERROR_END_IS_BEFORE_START );
        } else {
            return endUnixTime;
        }
    }

    public static long naturalDurationToSeconds(String naturalDuration) throws IllegalDateTimeException {
        if (!naturalDuration.matches(REGEX_DURATION_MULTIPLE)) {
            throw new IllegalDateTimeException(MESSAGE_ERROR_NON_CONFORMING_DURATION);
        } else {
            // Natty does not have support for natural durations
            // Parse durations as relative DateTimes into Natty
            // Then subtract from current time to generate duration

            long unixTimeNow = Instant.now().getEpochSecond();
            long actualDurationSeconds = 0;

            Pattern firstDuration = Pattern.compile(REGEX_DURATION_SINGLE);
            Matcher matcher = firstDuration.matcher(naturalDuration);
            while (matcher.find()) {
                actualDurationSeconds += getUnixTime(matcher.group()) - unixTimeNow;
            }

            return actualDurationSeconds;
        }
    }
    //@@author

    public static String epochSecondToDetailedDateTime(long epochSecond) {
        return epochSecondtoDateTime(epochSecond, TimeZoneEnum.ZONED, FORMATTER_DISPLAY);
    }

    public static String epochSecondToShortDateTime(long epochSecond) {
        return epochSecondtoDateTime(epochSecond, TimeZoneEnum.LOCAL, FORMATTER_DISPLAY);
    }

    public static String epochSecondToFormalDateTime(long epochSecond) {
        return epochSecondtoDateTime(epochSecond, TimeZoneEnum.LOCAL, FORMATTER_FORMAL);
    }

    private static String epochSecondtoDateTime(long epochSecond, TimeZoneEnum timeZone, DateTimeFormatter formatter) {
        Instant instant = Instant.ofEpochSecond(epochSecond);
        switch (timeZone) {
            case LOCAL: {
                return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
            }
            case ZONED: {
                return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()).format(formatter);
            }
            default: {
                throw new AssertionError("Unknown TimeZone enum!");
            }
        }
    }

    public static class IllegalDateTimeException extends Exception {
        public IllegalDateTimeException() {
            super();
        }

        public IllegalDateTimeException(String message) {
            super(message);
        }
    }
}
