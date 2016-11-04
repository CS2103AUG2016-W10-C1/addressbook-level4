package seedu.taskman.model.event;

import com.google.common.base.Objects;
import org.ocpsoft.prettytime.PrettyTime;
import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.logic.Formatter;

import java.sql.Date;

public class Frequency {
    public static final String MESSAGE_FREQUENCY_CONSTRAINTS =
            "Task frequency should only contain frequency and unit of time in the format: ";

    public static final String FREQUENCY_VALIDATION_REGEX = "";//"^" + DateTimeParser.DURATION_MULTIPLE + "$";

    // TODO: decide if it's better to stick to seconds or an end time instead.
    // store duration? store endtime? duration makes more sense for a repeating deadline (what if user removes deadline?)
    public final Long seconds;
    public final PrettyTime prettyTimeFormatter = new PrettyTime();

    private Frequency() {
        throw new AssertionError("Frequency is currently not supported");
    }

    // TODO: FIX OPTIONAL_FREQUENCY!!!!
    public Frequency(String frequency) throws IllegalValueException {
        this();
        assert frequency != null;
        /*
        frequency = frequency.trim();
        if (!isValidFrequency(frequency)) {
            throw new IllegalValueException(MESSAGE_FREQUENCY_CONSTRAINTS);
        }
        this.seconds = DateTimeParser.toEndTime(Instant.now().getEpochSecond(), frequency);
        */
    }

    public Frequency(long seconds) {
        this();
        /*
        assert seconds >= 0;
        this.seconds = seconds;
        */
    }

    public static boolean isValidFrequency(String test) {
        return test.matches(FREQUENCY_VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return prettyTimeFormatter.format(new Date(seconds * Formatter.MULTIPLIER_TIME_UNIX_TO_JAVA));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Frequency frequency = (Frequency) o;
        return Objects.equal(seconds, frequency.seconds);
    }

    @Override
    public int hashCode() {
        return seconds.hashCode();
    }
}
