package seedu.taskman.storage;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import seedu.taskman.commons.exceptions.DataConversionException;
import seedu.taskman.commons.util.FileUtil;
import seedu.taskman.model.TaskDiary;
import seedu.taskman.model.ReadOnlyTaskDiary;
import seedu.taskman.model.task.Task;
import seedu.taskman.testutil.TypicalTestTasks;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class XmlTaskDiaryStorageTest {
    private static String TEST_DATA_FOLDER = FileUtil.getPath("./src/test/data/XmlTaskDiaryStorageTest/");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void readTaskDiary_nullFilePath_assertionFailure() throws Exception {
        thrown.expect(AssertionError.class);
        readTaskDiary(null);
    }

    private java.util.Optional<ReadOnlyTaskDiary> readTaskDiary(String filePath) throws Exception {
        return new XmlTaskDiaryStorage(filePath).readTaskDiary(addToTestDataPathIfNotNull(filePath));
    }

    private String addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER + prefsFileInTestDataFolder
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readTaskDiary("NonExistentFile.xml").isPresent());
    }

    @Test
    public void read_notXmlFormat_exceptionThrown() throws Exception {

        thrown.expect(DataConversionException.class);
        readTaskDiary("NotXmlFormatTaskDiary.xml");

        /* IMPORTANT: Any code below an exception-throwing line (like the one above) will be ignored.
         * That means you should not have more than one exception test in one method
         */
    }

    @Test
    public void readAndSaveTaskDiary_allInOrder_success() throws Exception {
        String filePath = testFolder.getRoot().getPath() + "TempTaskDiary.xml";
        TypicalTestTasks td = new TypicalTestTasks();
        TaskDiary original = td.getTypicalTaskDiary();
        XmlTaskDiaryStorage xmlTaskDiaryStorage = new XmlTaskDiaryStorage(filePath);

        //Save in new file and read back
        xmlTaskDiaryStorage.saveTaskDiary(original, filePath);
        ReadOnlyTaskDiary readBack = xmlTaskDiaryStorage.readTaskDiary(filePath).get();
        assertEquals(original, new TaskDiary(readBack));

        //Modify data, overwrite exiting file, and read back
        original.addTask(new Task(TypicalTestTasks.hoon));
        original.removeTask(new Task(TypicalTestTasks.alice));
        xmlTaskDiaryStorage.saveTaskDiary(original, filePath);
        readBack = xmlTaskDiaryStorage.readTaskDiary(filePath).get();
        assertEquals(original, new TaskDiary(readBack));

        //Save and read without specifying file path
        original.addTask(new Task(TypicalTestTasks.ida));
        xmlTaskDiaryStorage.saveTaskDiary(original); //file path not specified
        readBack = xmlTaskDiaryStorage.readTaskDiary().get(); //file path not specified
        assertEquals(original, new TaskDiary(readBack));

    }

    @Test
    public void saveTaskDiary_nullTaskDiary_assertionFailure() throws IOException {
        thrown.expect(AssertionError.class);
        saveTaskDiary(null, "SomeFile.xml");
    }

    private void saveTaskDiary(ReadOnlyTaskDiary taskDiary, String filePath) throws IOException {
        new XmlTaskDiaryStorage(filePath).saveTaskDiary(taskDiary, addToTestDataPathIfNotNull(filePath));
    }

    @Test
    public void saveTaskDiary_nullFilePath_assertionFailure() throws IOException {
        thrown.expect(AssertionError.class);
        saveTaskDiary(new TaskDiary(), null);
    }


}
