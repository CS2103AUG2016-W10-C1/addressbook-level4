package seedu.taskman.logic.logicmanager;

import org.junit.Test;
import seedu.taskman.commons.core.Messages;
import seedu.taskman.commons.core.config.Config;
import seedu.taskman.commons.core.config.ConfigData;
import seedu.taskman.commons.exceptions.DataConversionException;
import seedu.taskman.commons.util.FileUtil;
import seedu.taskman.logic.commands.CommandResult;
import seedu.taskman.logic.commands.StoragelocCommand;
import seedu.taskman.logic.commands.ViewStoragelocCommand;
import seedu.taskman.model.TaskMan;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class StorageTests extends LogicManagerTestBase {

    //@@author A0121299A
    private void assert_storage_location(String inputCommand, String expectedFeedback,
                                         String expectedPath, boolean success)
            throws IOException, DataConversionException {
        Config.resetInstance();
        CommandResult result = logic.execute(inputCommand);
        assertEquals(result.feedbackToUser, expectedFeedback);
        if (success) {
            TaskMan storageTaskMan = new TaskMan(storage.readTaskMan().get());
            assertEquals(model.getTaskMan(), storageTaskMan);
            assertEquals(storage.getTaskManFilePath(), expectedPath);
        }
        assertEquals(Config.getInstance().getTaskManFilePath(), expectedPath);
    }

    private String getStoragelocFeedback(String path, boolean success) {
        String message = success
                ? StoragelocCommand.MESSAGE_SUCCESS
                : StoragelocCommand.MESSAGE_FAILURE;
        return String.format(message, path);
    }

    private void execute_storageloc_general(int generatedTasks,
                                            String commandArgs,
                                            String expectedPath,
                                            boolean isExpectedSuccess) throws Exception {
        TestDataHelper helper = new TestDataHelper();
        helper.addToModel(model, generatedTasks);
        assert_storage_location(StoragelocCommand.COMMAND_WORD + " " + commandArgs,
                getStoragelocFeedback(expectedPath, isExpectedSuccess), expectedPath, isExpectedSuccess);
    }

    @Test
    public void execute_storageloc_absolutePath() throws Exception {
        String givenPath = FileUtil.getAbsolutePath("./src/test/data/sandbox/LogicManagerTestBase/absolute.xml");
        execute_storageloc_general(4, givenPath, givenPath, true);
    }

    @Test
    public void execute_storageloc_relativePath() throws Exception {
        String givenPath = "./src/test/data/sandbox/LogicManagerTestBase/relative.xml";
        execute_storageloc_general(4, givenPath, FileUtil.getAbsolutePath(givenPath), true);
    }

    @Test
    public void execute_storageloc_default() throws Exception {
        execute_storageloc_general(3, "default",
                FileUtil.getAbsolutePath(ConfigData.DEFAULT_TASK_MAN_FILE_PATH),
                true);
    }

    @Test
    public void execute_storageloc_invalidFileName() throws Exception {
        String invalidFile = "/<3invalidFileName.txt";
        execute_storageloc_general(2, invalidFile, ConfigData.DEFAULT_TASK_MAN_FILE_PATH, false);
    }

    @Test
    public void execute_storageloc_whitespace() throws Exception {
        assert_storage_location(StoragelocCommand.COMMAND_WORD + "     ",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, StoragelocCommand.MESSAGE_USAGE), ConfigData.DEFAULT_TASK_MAN_FILE_PATH, false);
    }

    @Test
    public void execute_storageloc_empty() throws Exception {
        assert_storage_location(StoragelocCommand.COMMAND_WORD + "",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, StoragelocCommand.MESSAGE_USAGE), ConfigData.DEFAULT_TASK_MAN_FILE_PATH, false);
    }

    @Test
    public void execute_storageloc_view() throws Exception {
        assert_storage_location(StoragelocCommand.COMMAND_WORD + " view",
                String.format(ViewStoragelocCommand.MESSAGE,Config.DEFAULT_TASK_MAN_FILE_PATH), Config.DEFAULT_TASK_MAN_FILE_PATH, false);
    }


}
