package seedu.taskman.logic.commands;

public class HistoryCommand extends Command {

	public static final String COMMAND_WORD = "history";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows the "
    		+ Command.CAPACITY_HISTORY_COMMAND
    		+ " most recently executed commands.\n"
            + "Example: " + COMMAND_WORD;
    
    private static final int HISTORY_BULLET_FIRST = 1;
    private static final String HISTORY_EMPTY_PLACEHOLDER = "\tNIL";

    public HistoryCommand() {}
	
	@Override
	public CommandResult execute() {
		super.getInputHistory().pop(); // pop history itself
		StringBuilder builder = new StringBuilder("Command History:\n");
		int commandCount = HISTORY_BULLET_FIRST;
		for (String command : super.getInputHistory()) { // most recently executed command is at the head
		    builder.append(String.format("\t%d. ", commandCount++));
			builder.append(command);
			builder.append("\n");
		}
		if (commandCount == HISTORY_BULLET_FIRST) {
		    builder.append(HISTORY_EMPTY_PLACEHOLDER);
		} 
		return new CommandResult(builder.toString().trim());
	}

}
