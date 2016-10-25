package seedu.taskman.logic.commands;

import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.logic.parser.CommandParser;
import seedu.taskman.model.tag.Tag;
import seedu.taskman.model.tag.UniqueTagList;
import seedu.taskman.model.event.*;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static seedu.taskman.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

/**
 * Adds a Event to the task man.
 */
public class MarkCommand extends Command {

    public static final String COMMAND_WORD = "mark";

    // todo, differed: let parameters be objects. we can easily generate the usage in that case
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds an event to TaskMan.\n"
            + "Parameters: TITLE s/OPTIONAL_SCHEDULE [t/OPTIONAL_TAGS]...\n"
            + "Example: " + COMMAND_WORD
            + " star gazing s/tdy 2300, tdy 2359 t/leisure";

    public static final String MESSAGE_SUCCESS = "New event added: %1$s";
    public static final String MESSAGE_DUPLICATE_EVENT = "This event already exists in TaskMan";
    private static final Pattern EVENT_MARK_ARGS_FORMAT = // '/' forward slashes are reserved for delimiter prefixes
            Pattern.compile("" + CommandParser.ArgumentPattern.TITLE
                    + CommandParser.ArgumentPattern.OPTIONAL_SCHEDULE
                    + CommandParser.ArgumentPattern.OPTIONAL_FREQUENCY
                    + CommandParser.ArgumentPattern.OPTIONAL_TAGS); // variable number of tags

    private final Event toAdd;

    /**
     * Convenience constructor using raw values.
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    private MarkCommand(String title, String schedule, String frequency, Set<String> tags)
            throws IllegalValueException {
        super(true);
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

    public static Command prepareMark(String args) {
        final Matcher matcher = EVENT_MARK_ARGS_FORMAT.matcher(args.trim());
        // Validate arg string format
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        }
        try {
            return new MarkCommand(
                    matcher.group("title"),
                    matcher.group("schedule"),
                    matcher.group("frequency"),
                    getTagsFromArgs(matcher.group("tagArguments"))
            );
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }

    @Override
    public CommandResult execute() {
        assert model != null;
        try {
            model.addActivity(toAdd);
            return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd), true);
        } catch (UniqueActivityList.DuplicateActivityException e) {
            return new CommandResult(MESSAGE_DUPLICATE_EVENT, false);
        }

    }

}