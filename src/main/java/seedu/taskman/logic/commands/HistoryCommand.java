package seedu.taskman.logic.commands;

import java.util.Iterator;

public class HistoryCommand extends Command {

	public static final String COMMAND_WORD = "history";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows command history (only commands which mutate data).\n"
            + "Example: " + COMMAND_WORD;

    public HistoryCommand() {}
	
	@Override
	public CommandResult execute() {
		super.getInputHistory().pop(); // pop history itself
		StringBuilder builder = new StringBuilder();
		Iterator<String> iterator = super.getInputHistory().iterator(); // most recently executed command is at the head
		while (iterator.hasNext()) {
			builder.append(iterator.next());
			builder.append("\n");
		}
		return new CommandResult(builder.toString().trim());
	}

}
