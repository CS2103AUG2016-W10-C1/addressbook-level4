package seedu.taskman.storage;

import seedu.taskman.commons.util.XmlUtil;
import seedu.taskman.commons.exceptions.DataConversionException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Stores taskDiary data in an XML file
 */
public class XmlFileStorage {
    /**
     * Saves the given taskDiary data to the specified file.
     */
    public static void saveDataToFile(File file, XmlSerializableTaskDiary taskDiary)
            throws FileNotFoundException {
        try {
            XmlUtil.saveDataToFile(file, taskDiary);
        } catch (JAXBException e) {
            assert false : "Unexpected exception " + e.getMessage();
        }
    }

    /**
     * Returns task diary in the file or an empty task diary
     */
    public static XmlSerializableTaskDiary loadDataFromSaveFile(File file) throws DataConversionException,
                                                                            FileNotFoundException {
        try {
            return XmlUtil.getDataFromFile(file, XmlSerializableTaskDiary.class);
        } catch (JAXBException e) {
            throw new DataConversionException(e);
        }
    }

}
