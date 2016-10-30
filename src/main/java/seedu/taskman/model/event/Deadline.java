package seedu.taskman.model.event;

import org.ocpsoft.prettytime.PrettyTime;
import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.logic.parser.DateTimeParser;

import java.sql.Date;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Deadline {

    public static final String MESSAGE_DEADLINE_CONSTRAINTS =
            "Deadline should only contain dates and times in the format: " +
                    DateTimeParser.DESCRIPTION_DATE_TIME_FULL;

    public final long epochSecond;

    public static final int MULTIPLIER_TIME_UNIX_TO_JAVA = 1000;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE");
    private static final PrettyTime prettyTimeFormatter = new PrettyTime();

    public Deadline(String deadline) throws IllegalValueException {
        deadline = deadline.trim();
        epochSecond = DateTimeParser.getUnixTime(deadline);
    }

    public Deadline(long epochSecond) throws IllegalValueException {
        if (epochSecond < 0) {
            throw new IllegalValueException("Too far in the past.");
        }

        this.epochSecond = epochSecond;
    }

    @Override
    public String toString() {
        return toStringDetailed();
    }

    /**
     * Formats a string for displaying the deadline IN DETAIL to the form of:
     *
     *      DATE TIME (natural language as to how long before deadline)
     *
     *      Example: 25-10-2016 23:15 (Moments from now)
     *
     * @return String containing human-readable information for schedule (start, how long before start)
     */
    public String toStringDetailed() {
        return String.format(
                "%s\n(%s)",
                DateTimeParser.epochSecondToShortDateTime(epochSecond),
                prettyTimeFormatter.format(new Date(epochSecond * MULTIPLIER_TIME_UNIX_TO_JAVA))
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deadline deadline = (Deadline) o;
        return epochSecond == deadline.epochSecond;
    }

    @Override
    public int hashCode() {
        return Objects.hash(epochSecond);
    }

    public boolean hasPast(){
        return epochSecond <= Instant.now().getEpochSecond();
    }

    public String toFormalString(){
        return DateTimeParser.epochSecondToFormalDateTime(epochSecond);
    }
}
