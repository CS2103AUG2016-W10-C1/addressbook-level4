package seedu.taskman.commons.util;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import seedu.taskman.commons.core.config.Config;
import seedu.taskman.commons.core.config.ConfigData;
import seedu.taskman.commons.exceptions.DataConversionException;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ConfigUtilTest {

    private static String TEST_DATA_FOLDER = FileUtil.getPath("./src/test/data/ConfigUtilTest/");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void read_null_assertionFailure() throws DataConversionException {
        thrown.expect(AssertionError.class);
        read(null);
    }

    @Test
    public void read_missingFile_emptyResult() throws DataConversionException {
        assertFalse(read("NonExistentFile.json"));
    }

    @Test
    public void read_notJasonFormat_exceptionThrown() throws DataConversionException {

        thrown.expect(DataConversionException.class);
        read("NotJasonFormatConfig.json");

        /* IMPORTANT: Any code below an exception-throwing line (like the one above) will be ignored.
         * That means you should not have more than one exception test in one method
         */
    }

    @Test
    public void read_fileInOrder_successfullyRead() throws DataConversionException {

        ConfigData expectedData = getTypicalConfig().getDataClone();

        read("TypicalConfig.json");
        assertEquals(expectedData, Config.getInstance().getDataClone());
    }

    @Test
    public void read_valuesMissingFromFile_defaultValuesUsed() throws DataConversionException {
        ConfigData expectedData = Config.getInstance().getDataClone();
        read("EmptyConfig.json");
        assertEquals(expectedData, Config.getInstance().getDataClone());
    }

    @Test
    public void read_extraValuesInFile_extraValuesIgnored() throws DataConversionException {
        ConfigData expectedData = getTypicalConfig().getDataClone();
        read("ExtraValuesConfig.json");

        assertEquals(expectedData, Config.getInstance().getDataClone());
    }

    private Config getTypicalConfig() {
        Config config = Config.getInstance();
        config.setAppTitle("Typical App Title");
        config.setLogLevel(Level.INFO);
        config.setUserPrefsFilePath("C:\\preferences.json");
        config.setTaskManFilePath("taskMan.xml");
        config.setTaskManName("TypicalTaskManName");
        System.out.println(config.getAppTitle());
        System.out.println(config.getLogLevel());
        System.out.println(config.getTaskManFilePath());
        System.out.println(config.getTaskManName());
        System.out.println(config.getUserPrefsFilePath());
        return config;
    }

    private boolean read(String configFileInTestDataFolder) throws DataConversionException {
        String configFilePath = addToTestDataPathIfNotNull(configFileInTestDataFolder);
        return Config.readConfig(configFilePath);
    }

    @Test
    public void save_nullFile_assertionFailure() throws IOException {
        thrown.expect(AssertionError.class);
        save( null);
    }

    @Test
    public void saveConfig_allInOrder_success() throws DataConversionException, IOException {
        Config config = getTypicalConfig();

        String configFilePath = testFolder.getRoot() + File.separator + "TempConfig.json";

        //Try writing when the file doesn't exist
        ConfigData originalData = config.getDataClone();
        config.saveConfig(configFilePath);
        config.readConfig(configFilePath);
        assertEquals(config.getDataClone(), originalData);

        //Try saving when the file exists
        config.setAppTitle("Updated Title");
        config.setLogLevel(Level.FINE);
        originalData = config.getDataClone();
        config.saveConfig(configFilePath);
        config.readConfig(configFilePath);
        assertEquals(config, originalData);
    }

    private void save(String configFileInTestDataFolder) throws IOException {
        String configFilePath = addToTestDataPathIfNotNull(configFileInTestDataFolder);
        Config.saveConfig(configFilePath);
    }

    private String addToTestDataPathIfNotNull(String configFileInTestDataFolder) {
        return configFileInTestDataFolder != null
                                  ? TEST_DATA_FOLDER + configFileInTestDataFolder
                                  : null;
    }


}
