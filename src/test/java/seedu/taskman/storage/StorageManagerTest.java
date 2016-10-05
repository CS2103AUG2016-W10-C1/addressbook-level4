package seedu.taskman.storage;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import seedu.taskman.commons.events.model.TaskDiaryChangedEvent;
import seedu.taskman.commons.events.storage.DataSavingExceptionEvent;
import seedu.taskman.model.TaskDiary;
import seedu.taskman.model.ReadOnlyTaskDiary;
import seedu.taskman.model.UserPrefs;
import seedu.taskman.testutil.TypicalTestTasks;
import seedu.taskman.testutil.EventsCollector;

import java.io.IOException;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StorageManagerTest {

    private StorageManager storageManager;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();


    @Before
    public void setup() {
        storageManager = new StorageManager(getTempFilePath("ab"), getTempFilePath("prefs"));
    }


    private String getTempFilePath(String fileName) {
        return testFolder.getRoot().getPath() + fileName;
    }


    /*
     * Note: This is an integration test that verifies the StorageManager is properly wired to the
     * {@link JsonUserPrefsStorage} class.
     * More extensive testing of UserPref saving/reading is done in {@link JsonUserPrefsStorageTest} class.
     */

    @Test
    public void prefsReadSave() throws Exception {
        UserPrefs original = new UserPrefs();
        original.setGuiSettings(300, 600, 4, 6);
        storageManager.saveUserPrefs(original);
        UserPrefs retrieved = storageManager.readUserPrefs().get();
        assertEquals(original, retrieved);
    }

    @Test
    public void taskDiaryReadSave() throws Exception {
        TaskDiary original = new TypicalTestTasks().getTypicalTaskDiary();
        storageManager.saveTaskDiary(original);
        ReadOnlyTaskDiary retrieved = storageManager.readTaskDiary().get();
        assertEquals(original, new TaskDiary(retrieved));
        //More extensive testing of TaskDiary saving/reading is done in XmlTaskDiaryStorageTest
    }

    @Test
    public void getTaskDiaryFilePath(){
        assertNotNull(storageManager.getTaskDiaryFilePath());
    }

    @Test
    public void handleTaskDiaryChangedEvent_exceptionThrown_eventRaised() throws IOException {
        //Create a StorageManager while injecting a stub that throws an exception when the save method is called
        Storage storage = new StorageManager(new XmlTaskDiaryStorageExceptionThrowingStub("dummy"), new JsonUserPrefsStorage("dummy"));
        EventsCollector eventCollector = new EventsCollector();
        storage.handleTaskDiaryChangedEvent(new TaskDiaryChangedEvent(new TaskDiary()));
        assertTrue(eventCollector.get(0) instanceof DataSavingExceptionEvent);
    }


    /**
     * A Stub class to throw an exception when the save method is called
     */
    class XmlTaskDiaryStorageExceptionThrowingStub extends XmlTaskDiaryStorage {

        public XmlTaskDiaryStorageExceptionThrowingStub(String filePath) {
            super(filePath);
        }

        @Override
        public void saveTaskDiary(ReadOnlyTaskDiary taskDiary, String filePath) throws IOException {
            throw new IOException("dummy exception");
        }
    }


}
