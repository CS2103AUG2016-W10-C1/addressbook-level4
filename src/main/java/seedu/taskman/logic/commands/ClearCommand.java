package seedu.taskman.logic.commands;

import seedu.taskman.model.TaskDiary;

/**
 * Clears the task diary.
 */
public class ClearCommand extends Command {

    public static final String COMMAND_WORD = "clear";
    public static final String MESSAGE_SUCCESS = "Task diary has been cleared!";

    public ClearCommand() {}


    @Override
    public CommandResult execute() {
        assert model != null;
        model.resetData(TaskDiary.getEmptyTaskDiary());
        return new CommandResult(MESSAGE_SUCCESS);
    }
}
