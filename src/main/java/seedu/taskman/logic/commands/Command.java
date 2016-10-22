package seedu.taskman.logic.commands;

import java.util.concurrent.LinkedBlockingDeque;

import seedu.taskman.commons.core.EventsCenter;
import seedu.taskman.commons.core.Messages;
import seedu.taskman.commons.events.ui.IncorrectCommandAttemptedEvent;
import seedu.taskman.model.Model;
import seedu.taskman.model.ReadOnlyTaskMan;

/**
 * Represents a command with hidden internal logic and the ability to be executed.
 * 
 * Command History:
 * LinkedBlockingDeque
 * Front --	---- ---- ---- ---- ---- ---- ---- ---- Back
 * offerFirst/pollFirst/pop	---	---- ---- ---- ---- offerLast/pollLast
 * Most recently executed command -- ---- ---- ---- 10th most recently executed command
 */
public abstract class Command {
    protected Model model;
    private static LinkedBlockingDeque<String> inputHistory;
    private static LinkedBlockingDeque<ReadOnlyTaskMan> taskManHistory;

    // TODO: May need to move this configuration away? Separation of concerns?
    public static final int CAPACITY_LOW_BOUND_HISTORY_COMMAND = 0; // cannot be negative
    public static final int CAPACITY_UPP_BOUND_HISTORY_COMMAND = 11; // must be 11 because "history" is the 11th command

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
    public void setData(Model model) {
        this.model = model;
    }

    /**
     * Raises an event to indicate an attempt to execute an incorrect command
     */
    protected void indicateAttemptToExecuteIncorrectCommand() {
        // TODO: Need a better way to stalk failed commands
        popHistory();
        EventsCenter.getInstance().post(new IncorrectCommandAttemptedEvent(this));
    }

    public static void initHistory() {
        setInputHistory(new LinkedBlockingDeque<String>(CAPACITY_UPP_BOUND_HISTORY_COMMAND));
        setTaskManHistory(new LinkedBlockingDeque<ReadOnlyTaskMan>(CAPACITY_UPP_BOUND_HISTORY_COMMAND));
    }

    public static void popHistory() {
        getInputHistory().pop();
        getTaskManHistory().pop();
    }

    public static LinkedBlockingDeque<String> getInputHistory() {
        return inputHistory;
    }

    public static void setInputHistory(LinkedBlockingDeque<String> inputHistory) {
        Command.inputHistory = inputHistory;
    }

    public static LinkedBlockingDeque<ReadOnlyTaskMan> getTaskManHistory() {
        return taskManHistory;
    }

    public static void setTaskManHistory(LinkedBlockingDeque<ReadOnlyTaskMan> taskManHistory) {
        Command.taskManHistory = taskManHistory;
    }
}
