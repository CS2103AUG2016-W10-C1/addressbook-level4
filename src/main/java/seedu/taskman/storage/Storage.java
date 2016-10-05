package seedu.taskman.storage;

import seedu.taskman.commons.events.model.TaskDiaryChangedEvent;
import seedu.taskman.commons.events.storage.DataSavingExceptionEvent;
import seedu.taskman.commons.exceptions.DataConversionException;
import seedu.taskman.model.ReadOnlyTaskDiary;
import seedu.taskman.model.UserPrefs;

import java.io.IOException;
import java.util.Optional;

/**
 * API of the Storage component
 */
public interface Storage extends TaskDiaryStorage, UserPrefsStorage {

    @Override
    Optional<UserPrefs> readUserPrefs() throws DataConversionException, IOException;

    @Override
    void saveUserPrefs(UserPrefs userPrefs) throws IOException;

    @Override
    String getTaskDiaryFilePath();

    @Override
    Optional<ReadOnlyTaskDiary> readTaskDiary() throws DataConversionException, IOException;

    @Override
    void saveTaskDiary(ReadOnlyTaskDiary taskDiary) throws IOException;

    /**
     * Saves the current version of the Task diary to the hard disk.
     *   Creates the data file if it is missing.
     * Raises {@link DataSavingExceptionEvent} if there was an error during saving.
     */
    void handleTaskDiaryChangedEvent(TaskDiaryChangedEvent abce);
}
