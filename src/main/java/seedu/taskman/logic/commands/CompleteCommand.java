package seedu.taskman.logic.commands;

import java.util.Set;

import javax.annotation.Nullable;

public class CompleteCommand extends EditCommand {
	
	public static final String COMMAND_WORD = "complete";
	private static final String STATUS_COMPLETE = "complete";
	
	public static final String MESSAGE_USAGE = COMMAND_WORD + ": Marks an existing task as complete. "
            + "Parameters: INDEX\n"
            + "Example: " + COMMAND_WORD
            + " 1";

    public static final String MESSAGE_SUCCESS = "Task completed: %1$s";

    public CompleteCommand(int targetIndex,
            @Nullable String title, @Nullable String deadline, @Nullable String status,
            @Nullable String schedule, @Nullable String frequency, @Nullable Set<String> tags) {
		super(targetIndex, title, deadline, STATUS_COMPLETE, schedule, frequency, tags);
	}

}
