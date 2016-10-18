package seedu.taskman.logic.commands;

import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.model.tag.Tag;
import seedu.taskman.model.tag.UniqueTagList;
import seedu.taskman.model.event.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Adds a Task to the task man.
 */
public class MarkCommand extends Command {

    public static final String COMMAND_WORD = "mark";

    // todo, differed: let parameters be objects. we can easily generate the usage in that case
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds an event to TaskMan.\n"
            + "Parameters: TITLE s/SCHEDULE f/FREQUENCY [t/TAG]...\n"
            + "Example: " + COMMAND_WORD
            + " star gazing s/tdy 2300, tdy 2359 f/1 week t/leisure";

    public static final String MESSAGE_SUCCESS = "New event added: %1$s";
    public static final String MESSAGE_DUPLICATE_EVENT = "This event already exists in TaskMan";

    private final Event toAdd;

    /**
     * Convenience constructor using raw values.
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    public MarkCommand(String title, String schedule, String frequency, Set<String> tags)
            throws IllegalValueException {
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(new Tag(tagName));
        }
        this.toAdd = new Event(
                new Title(title),
                new UniqueTagList(tagSet),
                schedule == null
                    ? null
                    : new Schedule(schedule),
                frequency == null
                    ? null
                    : new Frequency(frequency)
        );
    }

    @Override
    public CommandResult execute() {
        assert model != null;
        try {
            model.addEvent(toAdd);
            return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
        } catch (UniqueActivityList.DuplicateActivityException e) {
            return new CommandResult(MESSAGE_DUPLICATE_EVENT);
        }

    }

}