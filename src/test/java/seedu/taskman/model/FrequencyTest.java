package seedu.taskman.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.model.event.Frequency;
import seedu.taskman.model.event.Schedule;

import static junit.framework.TestCase.assertEquals;

public class FrequencyTest {

    // only tests for successful creation
    @Test
    public void create_singleDuration_success() throws IllegalValueException {
        String duration = "4 years";
        new Frequency(duration);
    }

    // only tests for successful creation
    @Test
    public void create_multipleDuration_success() throws IllegalValueException {
        String duration = "4 years, 3 months";
        new Frequency(duration);
    }

}
