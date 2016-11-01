package seedu.taskman.logic.commands;

import java.util.Optional;

import static seedu.taskman.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

//@@author A0139019E
public class UndoCommand extends Command {

    public static final String COMMAND_WORD = "undo";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Undo the most recently executed command which changed the data in TaskMan. If a number between 1 to 10 inclusive is specified, that number of commands will be undone. "
            + "Parameters: NUMBER (1 to 10 inclusive)\n"
            + "Example: " + COMMAND_WORD
            + " 2";

    public static final String MESSAGE_SUCCESS = "Your %d command(s) are undone.";
    public static final String MESSAGE_NUMBER_OUT_OF_RANGE = "The number provided is out of range (only 1 to 10 inclusive).";

    private int commandsToUndo;

    public static Command prepareUndo(String arguments) {
        if (arguments.trim().isEmpty()) {
            return new UndoCommand(1);
        } else {
            Optional<Integer> index = parseIndex(arguments);
            if(!index.isPresent()){
                return new IncorrectCommand(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
            }
            return new UndoCommand(index.get());
        }
    }

    private UndoCommand(int number) {
        super(false);
        commandsToUndo = number;
    }

    @Override
    public CommandResult execute() {
        assert model != null;
        if (commandsToUndo < HistoryCommand.CAPACITY_LOW_BOUND_HISTORY_COMMAND ||
                commandsToUndo > historyDeque.size() ||
                commandsToUndo > HistoryCommand.CAPACITY_UPP_BOUND_HISTORY_COMMAND) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(MESSAGE_NUMBER_OUT_OF_RANGE, false);
        }

        for (int i = 0; i < commandsToUndo - 1; i++) {
            historyDeque.pop();
        }

        CommandHistory history = historyDeque.pop();
        model.resetData(history.getOldTaskMan());
        return new CommandResult(String.format(MESSAGE_SUCCESS, commandsToUndo), true);
    }

}
