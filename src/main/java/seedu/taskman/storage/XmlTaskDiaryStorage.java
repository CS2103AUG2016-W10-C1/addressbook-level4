package seedu.taskman.storage;

import seedu.taskman.commons.core.LogsCenter;
import seedu.taskman.commons.exceptions.DataConversionException;
import seedu.taskman.commons.util.FileUtil;
import seedu.taskman.model.ReadOnlyTaskDiary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * A class to access TaskDiary data stored as an xml file on the hard disk.
 */
public class XmlTaskDiaryStorage implements TaskDiaryStorage {

    private static final Logger logger = LogsCenter.getLogger(XmlTaskDiaryStorage.class);

    private String filePath;

    public XmlTaskDiaryStorage(String filePath){
        this.filePath = filePath;
    }

    public String getTaskDiaryFilePath(){
        return filePath;
    }

    /**
     * Similar to {@link #readTaskDiary()}
     * @param filePath location of the data. Cannot be null
     * @throws DataConversionException if the file is not in the correct format.
     */
    public Optional<ReadOnlyTaskDiary> readTaskDiary(String filePath) throws DataConversionException, FileNotFoundException {
        assert filePath != null;

        File taskDiaryFile = new File(filePath);

        if (!taskDiaryFile.exists()) {
            logger.info("TaskDiary file "  + taskDiaryFile + " not found");
            return Optional.empty();
        }

        ReadOnlyTaskDiary taskDiaryOptional = XmlFileStorage.loadDataFromSaveFile(new File(filePath));

        return Optional.of(taskDiaryOptional);
    }

    /**
     * Similar to {@link #saveTaskDiary(ReadOnlyTaskDiary)}
     * @param filePath location of the data. Cannot be null
     */
    public void saveTaskDiary(ReadOnlyTaskDiary taskDiary, String filePath) throws IOException {
        assert taskDiary != null;
        assert filePath != null;

        File file = new File(filePath);
        FileUtil.createIfMissing(file);
        XmlFileStorage.saveDataToFile(file, new XmlSerializableTaskDiary(taskDiary));
    }

    @Override
    public Optional<ReadOnlyTaskDiary> readTaskDiary() throws DataConversionException, IOException {
        return readTaskDiary(filePath);
    }

    @Override
    public void saveTaskDiary(ReadOnlyTaskDiary taskDiary) throws IOException {
        saveTaskDiary(taskDiary, filePath);
    }
}
