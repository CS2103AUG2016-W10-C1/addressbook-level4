package seedu.taskman.commons.core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import seedu.taskman.commons.core.config.Config;
import seedu.taskman.commons.core.config.ConfigData;
import seedu.taskman.commons.exceptions.DataConversionException;
import seedu.taskman.commons.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import static org.junit.Assert.*;

public class ConfigTest {

    private static String TEST_DATA_FOLDER = FileUtil.getPath("./src/test/data/ConfigTest/");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void resetInstance() {
        Config.resetInstance();
        assertTrue(Config.getInstance().equals(new ConfigData()));
    }

    @Test
    public void toString_defaultObject_stringReturned() {
        String defaultConfigAsString = "App title : TaskMan\n" +
                "Current log level : INFO\n" +
                "Preference file Location : preferences.json\n" +
                "Local data file location : data/taskMan.xml\n" +
                "TaskMan name : MyTaskMan";
        Config.resetInstance();
        assertEquals(defaultConfigAsString, Config.getInstance().toString());
    }

    @Test
    public void equalsMethod() {
        Config defaultConfig = Config.getInstance();
        assertFalse(defaultConfig.equals(null));
        assertTrue(defaultConfig.equals(defaultConfig));
    }

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
        ConfigData expectedData = new ConfigData();
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
        save(null);
    }

    @Test
    public void saveConfig_allInOrder_success() throws DataConversionException, IOException {
        Config config = getTypicalConfig();

        String configFilePath = testFolder.getRoot() + File.separator + "TempConfig.json";

        //Try writing when the file doesn't exist
        ConfigData originalData = config.getDataClone();
        Config.setConfigFile(configFilePath);
        Config.save();
        config.readConfig(configFilePath);
        assertEquals(config.getDataClone(), originalData);

        //Try saving when the file exists
        config.setAppTitle("Updated Title");
        config.setLogLevel(Level.FINE);
        originalData = config.getDataClone();
        Config.setConfigFile(configFilePath);
        Config.save();
        config.readConfig(configFilePath);
        assertEquals(config, originalData);
    }

    @Test
    public void getInstance() {
        assertNotNull(Config.getInstance());
        assertTrue(Config.getInstance() == Config.getInstance());
    }

    @Test
    public void getInstance_afterReset() {
        assertNotNull(Config.getInstance());
        Config.resetInstance();
        assertTrue(Config.getInstance() == Config.getInstance());
    }

    @Test
    public void getInstance_afterRead() throws DataConversionException {
        assertNotNull(Config.getInstance());
        read("TypicalConfig.json");
        assertTrue(Config.getInstance() == Config.getInstance());
    }

    @Test
    public void clone_getInstance() {
        assertFalse(Config.getInstance().getDataClone() == Config.getInstance());
        assertEquals(Config.getInstance().getDataClone(), Config.getInstance());
    }

    private void save(String configFileInTestDataFolder) throws IOException {
        String configFilePath = addToTestDataPathIfNotNull(configFileInTestDataFolder);
        Config.setConfigFile(configFilePath);
        Config.save();
    }

    private String addToTestDataPathIfNotNull(String configFileInTestDataFolder) {
        return configFileInTestDataFolder != null
                ? TEST_DATA_FOLDER + configFileInTestDataFolder
                : null;
    }


}
