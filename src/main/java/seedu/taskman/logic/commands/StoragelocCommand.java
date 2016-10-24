package seedu.taskman.logic.commands;

import seedu.taskman.commons.core.config.Config;
import seedu.taskman.commons.core.config.ConfigData;
import seedu.taskman.commons.util.FileUtil;
import seedu.taskman.logic.parser.CommandParser;

import java.io.IOException;
import java.util.regex.Pattern;

import static seedu.taskman.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

/**
 * Changes the save location of the data file
 */
public class StoragelocCommand extends Command {

    public static final String COMMAND_WORD = "storageloc";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Saves to the specified file name and location and sets the application to load from the specified location in the future. "
            + "Parameters: LOCATION\n"
            + "Example: " + COMMAND_WORD
            + " C:/Users/Owner/Desktop/new_tasks.xml";

    public static final String MESSAGE_SUCCESS = "Save location successfully updated to %1$s";
    public static final String MESSAGE_FAILURE = "Attempt to Save Location was unsuccessful.\n" +
            "Save location reverted to %1$s";
    private static final String STORAGELOC_DEFAULT_KEYWORD = "default";
    private static final Pattern STORAGELOC_ARGS_FORMAT = Pattern.compile("" + CommandParser.ArgumentPattern.FILE_PATH);

    private final String filePath;

    public static Command prepareStorageloc(String args) {

        String trimmedArgs = args.trim();

        if (!STORAGELOC_ARGS_FORMAT.matcher(trimmedArgs).matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    MESSAGE_USAGE));
        }

        if (trimmedArgs.equals(STORAGELOC_DEFAULT_KEYWORD)) {
            trimmedArgs = ConfigData.DEFAULT_TASK_MAN_FILE_PATH;
        }

        return new StoragelocCommand(trimmedArgs);

    }

    private StoragelocCommand(String filePath) {
        super(false);
        this.filePath = FileUtil.getAbsolutePath(filePath);
    }


    @Override
    public CommandResult execute() {
        assert model != null;
        assert storage != null;

        boolean saveChanged = false;
        ConfigData initialConfigData = Config.getInstance().getDataClone();

        try {
            storage.saveTaskMan(model.getTaskMan(), filePath);
            Config.getInstance().setTaskManFilePath(filePath);
            Config.save();
            storage.setTaskManFilePath(filePath);
            saveChanged = true;
        } catch (IOException e) {
            Config.getInstance().setTaskManFilePath(initialConfigData.getTaskManFilePath());
        }

        String message = saveChanged
                ? MESSAGE_SUCCESS
                : MESSAGE_FAILURE;

        return new CommandResult(String.format(message, Config.getInstance().getTaskManFilePath()), saveChanged);
    }
}
