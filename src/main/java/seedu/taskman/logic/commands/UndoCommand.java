package seedu.taskman.logic.commands;

import javax.annotation.Nullable;

public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Undo the most recently executed command. If a number between 1 to 10 inclusive is specified, that number of commands will be undone. "
            + "Parameters: NUMBER (1 to 10 inclusive)\n"
            + "Example: " + COMMAND_WORD
            + " 2";

    public static final String MESSAGE_SUCCESS = "Your %d command(s) are undone.";
    public static final String MESSAGE_NUMBER_OUT_OF_RANGE = "The number provided is out of range (1 to 10 inclusive).";
    
    private int numCommands;
    
    public UndoCommand(@Nullable int number) {
        numCommands = (number == 0) ? 1 : number;
    }
    
    @Override
    public CommandResult execute() {
        assert model != null;
        if (numCommands < Command.CAPACITY_LOW_BOUND_HISTORY_COMMAND ||
                numCommands > getTaskManHistory().size() ||
                numCommands >= Command.CAPACITY_UPP_BOUND_HISTORY_COMMAND) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(String.format(MESSAGE_NUMBER_OUT_OF_RANGE, MESSAGE_USAGE));
        }
        for (int commandCount = 0; commandCount < numCommands; ++commandCount) {
            popHistory();
        }
        getInputHistory().pop();
        model.resetData(getTaskManHistory().pop());
        return new CommandResult(String.format(MESSAGE_SUCCESS, numCommands));
    }

}
