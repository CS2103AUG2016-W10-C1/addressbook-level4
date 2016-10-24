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
 * Adds a Task to the task man.
 */
public class DoCommand extends Command {

    public static final String COMMAND_WORD = "do";

    // todo, differed: let parameters be objects. we can easily generate the usage in that case
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a task to TaskMan.\n"
            + "Parameters: TITLE d/DEADLINE s/SCHEDULE [t/TAG]...\n"
            + "Example: " + COMMAND_WORD
            + " pay utility bills d/next fri 1800 s/tdy 1800, tdy 1830 t/bills";

    public static final String MESSAGE_SUCCESS = "New task added: %1$s";
    public static final String MESSAGE_DUPLICATE_EVENT = "This task already exists in TaskMan";
    private static final Pattern TASK_DO_ARGS_FORMAT = // '/' forward slashes are reserved for delimiter prefixes
            Pattern.compile("" + CommandParser.ArgumentPattern.TITLE
                    + CommandParser.ArgumentPattern.DEADLINE
                    + CommandParser.ArgumentPattern.SCHEDULE
                    + CommandParser.ArgumentPattern.FREQUENCY
                    + CommandParser.ArgumentPattern.TAG); // variable number of tags

    private final Task toAdd;

    /**
     * Convenience constructor using raw values.
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    private DoCommand(String title, String deadline, String schedule, String frequency, Set<String> tags)
            throws IllegalValueException {
        super(true);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(new Tag(tagName));
        }
        this.toAdd = new Task(
                new Title(title),
                new UniqueTagList(tagSet),
                deadline == null
                        ? null
                        : new Deadline(deadline),
                schedule == null
                        ? null
                        : new Schedule(schedule),
                frequency == null
                        ? null
                        : new Frequency(frequency)
        );
    }

    public static Command prepareDo(String args) {
        final Matcher matcher = TASK_DO_ARGS_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        }
        try {
            return new DoCommand(
                    matcher.group("title"),
                    matcher.group("deadline"),
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
