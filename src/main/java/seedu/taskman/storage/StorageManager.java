package seedu.taskman.storage;

import com.google.common.eventbus.Subscribe;
import seedu.taskman.commons.core.ComponentManager;
import seedu.taskman.commons.core.LogsCenter;
import seedu.taskman.commons.events.model.TaskDiaryChangedEvent;
import seedu.taskman.commons.events.storage.DataSavingExceptionEvent;
import seedu.taskman.commons.exceptions.DataConversionException;
import seedu.taskman.model.ReadOnlyTaskDiary;
import seedu.taskman.model.UserPrefs;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Manages storage of TaskDiary data in local storage.
 */
public class StorageManager extends ComponentManager implements Storage {

    private static final Logger logger = LogsCenter.getLogger(StorageManager.class);
    private TaskDiaryStorage taskDiaryStorage;
    private UserPrefsStorage userPrefsStorage;


    public StorageManager(TaskDiaryStorage taskDiaryStorage, UserPrefsStorage userPrefsStorage) {
        super();
        this.taskDiaryStorage = taskDiaryStorage;
        this.userPrefsStorage = userPrefsStorage;
    }

    public StorageManager(String taskDiaryFilePath, String userPrefsFilePath) {
        this(new XmlTaskDiaryStorage(taskDiaryFilePath), new JsonUserPrefsStorage(userPrefsFilePath));
    }

    // ================ UserPrefs methods ==============================

    @Override
    public Optional<UserPrefs> readUserPrefs() throws DataConversionException, IOException {
        return userPrefsStorage.readUserPrefs();
    }

    @Override
    public void saveUserPrefs(UserPrefs userPrefs) throws IOException {
        userPrefsStorage.saveUserPrefs(userPrefs);
    }


    // ================ TaskDiary methods ==============================

    @Override
    public String getTaskDiaryFilePath() {
        return taskDiaryStorage.getTaskDiaryFilePath();
    }

    @Override
    public Optional<ReadOnlyTaskDiary> readTaskDiary() throws DataConversionException, IOException {
        return readTaskDiary(taskDiaryStorage.getTaskDiaryFilePath());
    }

    @Override
    public Optional<ReadOnlyTaskDiary> readTaskDiary(String filePath) throws DataConversionException, IOException {
        logger.fine("Attempting to read data from file: " + filePath);
        return taskDiaryStorage.readTaskDiary(filePath);
    }

    @Override
    public void saveTaskDiary(ReadOnlyTaskDiary taskDiary) throws IOException {
        saveTaskDiary(taskDiary, taskDiaryStorage.getTaskDiaryFilePath());
    }

    @Override
    public void saveTaskDiary(ReadOnlyTaskDiary taskDiary, String filePath) throws IOException {
        logger.fine("Attempting to write to data file: " + filePath);
        taskDiaryStorage.saveTaskDiary(taskDiary, filePath);
    }


    @Override
    @Subscribe
    public void handleTaskDiaryChangedEvent(TaskDiaryChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event, "Local data changed, saving to file"));
        try {
            saveTaskDiary(event.data);
        } catch (IOException e) {
            raise(new DataSavingExceptionEvent(e));
        }
    }

}
