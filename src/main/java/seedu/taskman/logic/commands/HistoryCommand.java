package seedu.taskman.logic.commands;

public class HistoryCommand extends Command {

	public static final String COMMAND_WORD = "history";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows command history (only commands which mutate data).\n"
            + "Example: " + COMMAND_WORD;

    public HistoryCommand() {}
	
	@Override
	public CommandResult execute() {
		super.getInputHistory().pop(); // pop history itself
		StringBuilder commandHistory = new StringBuilder();
		for (String command : super.getInputHistory()) {
			commandHistory.append("\n");
			commandHistory.append(new StringBuffer(command).reverse());
		}
		return new CommandResult(commandHistory.reverse().toString());
	}

}
