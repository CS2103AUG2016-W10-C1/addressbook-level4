package seedu.taskman.logic.logicmanager;

import org.junit.Test;
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
import static seedu.taskman.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

public class StorageTests extends LogicManagerTestBase {

    //@@author A0121299A

    /**
     * Executes the input commands and asserts the feedback, file path of the resulting state
     * @param inputCommand
     * @param expectedFeedback
     * @param expectedPath
     * @param success
     * @throws IOException
     * @throws DataConversionException
     */
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

    /**
     * Returns the expected feedback string given the file path and expected success of the input command
     * @param path
     * @param success
     * @return expected feedback string
     */
    private String getStoragelocFeedback(String path, boolean success) {
        String message = success
                ? StoragelocCommand.MESSAGE_SUCCESS
                : StoragelocCommand.MESSAGE_FAILURE;
        return String.format(message, path);
    }

    /**
     * Populates model with given number of tasks and assert results of the command
     * @param generatedTasks no of tasks to populate model with
     * @param commandArgs to be added behind command
     * @param expectedPath of the storage file
     * @param isExpectedSuccess of the command
     * @throws Exception
     */
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
                MESSAGE_INVALID_COMMAND_FORMAT + "\n" + StoragelocCommand.COMMAND_WORD + ": " + StoragelocCommand.MESSAGE_USAGE,
                ConfigData.DEFAULT_TASK_MAN_FILE_PATH, false);
    }

    @Test
    public void execute_storageloc_empty() throws Exception {
        assert_storage_location(StoragelocCommand.COMMAND_WORD + "",
                MESSAGE_INVALID_COMMAND_FORMAT + "\n" + StoragelocCommand.COMMAND_WORD + ": " + StoragelocCommand.MESSAGE_USAGE,
                ConfigData.DEFAULT_TASK_MAN_FILE_PATH, false);
    }

    /**
     * Tests ViewStoragelocCommand
     * @throws Exception
     */
    @Test
    public void execute_storageloc_view() throws Exception {
        assert_storage_location(StoragelocCommand.COMMAND_WORD + " view",
                String.format(ViewStoragelocCommand.MESSAGE,Config.DEFAULT_TASK_MAN_FILE_PATH), Config.DEFAULT_TASK_MAN_FILE_PATH, false);
    }


}
