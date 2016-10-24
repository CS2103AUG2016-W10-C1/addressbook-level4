package seedu.taskman.logic.commands;

import seedu.taskman.model.TaskMan;

public class CommandHistory {
    public final String inputCommand;
    public final String resultMessage;
    private final TaskMan oldTaskMan;

    public CommandHistory(String inputCommand, String resultMessage, TaskMan oldTaskMan) {
        this.inputCommand = inputCommand;
        this.resultMessage = resultMessage;
        this.oldTaskMan = oldTaskMan;
    }

    /**
     * Returns TaskMan before the command was executed
     */
    public TaskMan getOldTaskMan() {
        return new TaskMan(oldTaskMan);
    }
}
