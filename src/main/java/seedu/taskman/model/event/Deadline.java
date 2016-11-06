package seedu.taskman.model.event;

import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.logic.Formatter;
import seedu.taskman.logic.parser.DateTimeParser;

import java.sql.Date;
import java.time.Instant;
import java.util.Objects;

public class Deadline {

    public final long epochSecond;

    public Deadline(String deadline) throws IllegalValueException {
        deadline = deadline.trim();

        try {
            epochSecond = DateTimeParser.getEpochTime(deadline);
        } catch (DateTimeParser.IllegalDateTimeException e) {
            throw new IllegalValueException(e.getMessage());
        }
    }

    public Deadline(long epochSecond) throws IllegalValueException {
        if (epochSecond < 0) {
            throw new IllegalValueException("Too far in the past.");
        }

        this.epochSecond = epochSecond;
    }

    /**
     * Formats a string for displaying the deadline IN DETAIL to the form of:
     *
     *      DATE TIME (natural language as to how long more before deadline)
     *
     *      Example: Sat, 05 Nov 16, 2:55PM
     *               (Moments from now)
     *
     * @return String containing human-readable information for deadline (deadline, how long more before deadline)
     */
    @Override
    public String toString() {
        return String.format(
                Formatter.FORMAT_TWO_LINES,
                DateTimeParser.epochSecondToShortDateTime(epochSecond),
                String.format(
                        Formatter.FORMAT_WRAP_IN_BRACKET,
                        Formatter.PRETTY_TIME.format(new Date(epochSecond * Formatter.MULTIPLIER_TIME_UNIX_TO_JAVA))
                )
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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
