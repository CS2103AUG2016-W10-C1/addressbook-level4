package seedu.taskman.logic.commands;

public class HistoryCommand extends Command {

	public static final String COMMAND_WORD = "history";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows the "
    		+ Command.CAPACITY_HISTORY_COMMAND
    		+ " most recently executed commands.\n"
            + "Example: " + COMMAND_WORD;

    public HistoryCommand() {}
	
	@Override
	public CommandResult execute() {
		super.getInputHistory().pop(); // pop history itself
		StringBuilder builder = new StringBuilder();
		for (String command : super.getInputHistory()) { // most recently executed command is at the head
			builder.append(command);
			builder.append("\n");
		}
		return new CommandResult(builder.toString().trim());
	}

}
