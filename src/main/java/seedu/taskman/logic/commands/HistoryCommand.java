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
		super.getInputHistory().pop(); // pop history itself
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
