package seedu.taskman.storage;

import seedu.taskman.commons.exceptions.DataConversionException;
import seedu.taskman.model.ReadOnlyTaskDiary;
import seedu.taskman.model.TaskDiary;

import java.io.IOException;
import java.util.Optional;

/**
 * Represents a storage for {@link TaskDiary}.
 */
public interface TaskDiaryStorage {

    /**
     * Returns the file path of the data file.
     */
    String getTaskDiaryFilePath();

    /**
     * Returns TaskDiary data as a {@link ReadOnlyTaskDiary}.
     *   Returns {@code Optional.empty()} if storage file is not found.
     * @throws DataConversionException if the data in storage is not in the expected format.
     * @throws IOException if there was any problem when reading from the storage.
     */
    Optional<ReadOnlyTaskDiary> readTaskDiary() throws DataConversionException, IOException;

    /**
     * @see #getTaskDiaryFilePath()
     */
    Optional<ReadOnlyTaskDiary> readTaskDiary(String filePath) throws DataConversionException, IOException;

    /**
     * Saves the given {@link ReadOnlyTaskDiary} to the storage.
     * @param taskDiary cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    void saveTaskDiary(ReadOnlyTaskDiary taskDiary) throws IOException;

    /**
     * @see #saveTaskDiary(ReadOnlyTaskDiary)
     */
    void saveTaskDiary(ReadOnlyTaskDiary taskDiary, String filePath) throws IOException;

}
