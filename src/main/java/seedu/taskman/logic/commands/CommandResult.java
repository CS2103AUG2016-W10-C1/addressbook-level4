package seedu.taskman.logic.commands;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    public final String feedbackToUser;
    public final boolean succeeded;

    public CommandResult(String feedbackToUser, boolean succeeded) {
        assert feedbackToUser != null;
        this.feedbackToUser = feedbackToUser;
        this.succeeded = succeeded;
    }

}
