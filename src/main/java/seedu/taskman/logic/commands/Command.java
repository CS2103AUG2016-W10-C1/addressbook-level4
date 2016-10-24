package seedu.taskman.logic.commands;

import seedu.taskman.commons.core.EventsCenter;
import seedu.taskman.commons.core.Messages;
import seedu.taskman.commons.events.ui.IncorrectCommandAttemptedEvent;
import seedu.taskman.commons.util.StringUtil;
import seedu.taskman.logic.parser.CommandParser;
import seedu.taskman.model.Model;
import seedu.taskman.storage.Storage;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a command with hidden internal logic and the ability to be executed.
 */
public abstract class Command {
    private static final Pattern TASK_INDEX_ARGS_FORMAT = Pattern.compile(CommandParser.ArgumentPattern.TARGET_INDEX.pattern);
    public final boolean storeHistory;
    protected Model model;
    protected Storage storage;
    protected Stack<CommandHistory> historyStack;

    protected Command(boolean storeHistory) {
        this.storeHistory = storeHistory;
    }

    /**
     * Constructs a feedback message to summarise an operation that displayed a listing of tasks.
     *
     * @param displaySize used to generate summary
     * @return summary message for tasks displayed
     */
    public static String getMessageForTaskListShownSummary(int displaySize) {
        return String.format(Messages.MESSAGE_EVENTS_LISTED_OVERVIEW, displaySize);
    }

    /**
     * Extracts the new task's tags from the add command's tag arguments string.
     * Merges duplicate tag strings.
     */
    public static Set<String> getTagsFromArgs(String tagArguments) {
        // no tags
        if (tagArguments.isEmpty()) {
            return Collections.emptySet();
        }
        // replace first delimiter prefix, then split
        final Collection<String> tagStrings = Arrays.asList(tagArguments.replaceFirst("t/", "").split(" t/"));
        return new HashSet<>(tagStrings);
    }

    /**
     * Returns the specified index in the {@code command} IF a positive unsigned integer is given as the index.
     *   Returns an {@code Optional.empty()} otherwise.
     */
    protected static Optional<Integer> parseIndex(String command) {
        final Matcher matcher = TASK_INDEX_ARGS_FORMAT.matcher(command.trim());
        if (!matcher.matches()) {
            return Optional.empty();
        }

        String index = matcher.group("targetIndex");
        if(!StringUtil.isUnsignedInteger(index)){
            return Optional.empty();
        }
        return Optional.of(Integer.parseInt(index));

    }


    /**
     * Executes the command and returns the result message.
     *
     * @return feedback message of the operation result for display
     */
    public abstract CommandResult execute();

    /**
     * Provides any needed dependencies to the command.
     * Commands making use of any of these should override this method to gain
     * access to the dependencies.
     */
    public void setData(Model model, Storage storage, Stack<CommandHistory> historyStack) {
        this.model = model;
        this.storage = storage;
        this.historyStack = historyStack;
    }

    /**
     * Raises an event to indicate an attempt to execute an incorrect command
     */
    protected void indicateAttemptToExecuteIncorrectCommand() {
        EventsCenter.getInstance().post(new IncorrectCommandAttemptedEvent(this));
    }
}
