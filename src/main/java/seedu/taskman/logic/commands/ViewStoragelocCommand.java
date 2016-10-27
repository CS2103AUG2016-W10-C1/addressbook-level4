package seedu.taskman.logic.commands;

import seedu.taskman.commons.core.config.Config;

/**
 * To view current storage file location
 */
public class ViewStoragelocCommand extends Command {

    public static final String MESSAGE = "Storage file is currently located at %s";

    public ViewStoragelocCommand() {
       super(false);
    }

    @Override
    public CommandResult execute() {
        return new CommandResult(String.format(MESSAGE, Config.getInstance().getTaskManFilePath()), true);
    }
}
