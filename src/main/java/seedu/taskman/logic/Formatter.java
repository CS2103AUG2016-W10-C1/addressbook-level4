package seedu.taskman.logic;

import org.ocpsoft.prettytime.PrettyTime;

import java.time.format.DateTimeFormatter;

/**
 * Created by jiayee on 11/5/16.
 */
public class Formatter {

    public enum Duration {
        MINUTE  (1, "mins"),
        HOUR    (60, "hrs"),
        DAY     (24 * HOUR.count, "days"),
        WEEK    (7 * DAY.count, "wks"),
        MONTH   (30 * DAY.count, "mths"),
        YEAR    (365 * DAY.count, "yrs");

        public int count;
        public String string;

        Duration(int count, String string) {
            this.count = count;
            this.string = string;
        }
    }

    // Deadline, Schedule
    public static final int MULTIPLIER_TIME_UNIX_TO_JAVA = 1000;

    // Schedule.toStringSelected, ReadOnlyTask.getAsText
    public static final String FORMAT_TWO_TERMS_SPACED_WITHIN_AFTER = "%1$s %2$s ";

    // Schedule.toFormalString
    public static final String FORMAT_THREE_TERMS_SPACED_WITHIN = "%1$s %2$s %3$s";

    // Deadline.toString, Schedule, Schedule.toString
    public static final String FORMAT_TWO_LINES = "%1$s\n%2$s";

    // Deadline.toString, ReadOnlyTask.getAsText
    public static final String FORMAT_WRAP_IN_BRACKET = "(%1$s)";

    // DateTimeParser.epochSecondTo...
    public static final DateTimeFormatter DATETIME_DISPLAY = DateTimeFormatter.ofPattern("EEE, dd MMM YY, h.mma");
    public static final DateTimeFormatter DATETIME_FORMAL = DateTimeFormatter.ofPattern("dd MMM YYYY HHmm");

    // Deadline, Schedule
    public static final PrettyTime PRETTY_TIME = new PrettyTime();

    public static String appendWithNewlines(String string, String... strings) {
        StringBuilder builder = new StringBuilder(string);
        for(String str : strings) {
            builder.append("\n")
                    .append(str);
        }

        return builder.toString();
    }
}
