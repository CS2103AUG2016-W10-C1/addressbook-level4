# A0136070Runused
# REJECTED IN CODE REVIEW
###### /java/seedu/taskman/logic/LogicManager.java
``` java
    @Override
    public CommandResult execute(String commandText) {
        logger.info("----------------[USER COMMAND][" + commandText + "]");
        Command command = commandParser.parseCommand(commandText);
        command.setData(model);
        try {
            Command.getTaskManHistory().offerFirst(model.getTaskMan().getClone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return command.execute();
    }
```
###### /java/seedu/taskman/logic/commands/UndoCommand.java
``` java
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
        for (int commandCount = 0; commandCount < numCommands; commandCount++) {
            popHistory();
        }
        getInputHistory().pop();
        model.resetData(getTaskManHistory().pop());
        return new CommandResult(String.format(MESSAGE_SUCCESS, numCommands));
    }

}
```
###### /java/seedu/taskman/logic/commands/HistoryCommand.java
``` java
package seedu.taskman.logic.commands;

public class HistoryCommand extends Command {

	public static final String COMMAND_WORD = "history";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows the "
            + Command.CAPACITY_UPP_BOUND_HISTORY_COMMAND
            + " most recently executed commands.\n"
            + "Example: " + COMMAND_WORD;
    
    public static final int HISTORY_NUMBER_BULLET_POINT = 1;
    public static final String HISTORY_STRING_HEADER = "Command History:\n";
    public static final String HISTORY_STRING_EMPTY_PLACEHOLDER = "\tNIL";
    public static final String HISTORY_STRING_BULLET_POINT = "\t%d. ";
    public static final String HISTORY_STRING_BULLET_POINT_BREAK = "\n";

    public HistoryCommand() {}
	
	@Override
	public CommandResult execute() {
	    popHistory(); // pop history itself
		StringBuilder builder = new StringBuilder(HISTORY_STRING_HEADER);
		int commandCount = HISTORY_NUMBER_BULLET_POINT;
		for (String command : super.getInputHistory()) { // most recently executed command is at the head
		    builder.append(String.format(HISTORY_STRING_BULLET_POINT, commandCount++));
			builder.append(command);
			builder.append(HISTORY_STRING_BULLET_POINT_BREAK);
		}
		if (commandCount == HISTORY_NUMBER_BULLET_POINT) {
		    builder.append(HISTORY_STRING_EMPTY_PLACEHOLDER);
		}
		return new CommandResult(builder.toString().trim());
	}

}
```
###### /java/seedu/taskman/logic/parser/CommandParser.java
``` java
    	if (!Command.getInputHistory().offerFirst(userInput)) {
    		Command.getInputHistory().pollLast();   // poll 10th most recently executed command
    		Command.getTaskManHistory().pollLast(); // do the same for model
    		Command.getInputHistory().offerFirst(userInput); // push model after changes (i.e. at LogicManager)
    	}
```
###### /java/seedu/taskman/logic/parser/CommandParser.java
``` java
    /**
     * Parses arguments in the context of the undo task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareUndo(String args) {

        Optional<Integer> index = parseIndex(args);
        return new UndoCommand(index.get());
    }

}
```
