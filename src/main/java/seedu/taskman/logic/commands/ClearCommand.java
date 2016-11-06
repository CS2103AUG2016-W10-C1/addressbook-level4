package seedu.taskman.logic.commands;

import seedu.taskman.model.TaskMan;

/**
 * Clears the task man.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_USAGE = "Clear data in TaskMan.\n"
            + "Example: " + COMMAND_WORD;
    public static final String MESSAGE_SUCCESS = "TaskMan has been cleared!";

    public ClearCommand() {
        super(true);
    }


    @Override
    public CommandResult execute() {
        assert model != null;
        model.resetData(TaskMan.getEmptyTaskMan());
        return new CommandResult(MESSAGE_SUCCESS, true);
    }
}
