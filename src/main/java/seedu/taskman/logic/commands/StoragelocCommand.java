package seedu.taskman.logic.commands;

import seedu.taskman.commons.core.config.Config;
import seedu.taskman.commons.core.config.ConfigData;
import seedu.taskman.commons.util.FileUtil;

import java.io.IOException;

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

    private final String filePath;

    public StoragelocCommand(String filePath){
        this.filePath = FileUtil.getAbsolutePath(filePath);
    }

    @Override
    public CommandResult execute() {
        assert model != null;
        assert storage != null;

        boolean saveChanged = false;
        ConfigData initialConfigData = Config.getInstance().getDataClone();

        try {
            storage.saveTaskMan(model.getTaskMan(),filePath);
            Config.getInstance().setTaskManFilePath(filePath);
            System.out.println(Config.getInstance().getTaskManFilePath());
            Config.save();
            storage.setTaskManFilePath(filePath);
            saveChanged = true;
        } catch (IOException e) {
            Config.getInstance().setTaskManFilePath(initialConfigData.getTaskManFilePath());
        }

        String message = saveChanged
                ? MESSAGE_SUCCESS
                : MESSAGE_FAILURE;

        return new CommandResult(String.format(message, Config.getInstance().getTaskManFilePath()));
    }
}
