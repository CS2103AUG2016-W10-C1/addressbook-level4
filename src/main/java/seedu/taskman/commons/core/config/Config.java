package seedu.taskman.commons.core.config;

import seedu.taskman.commons.core.LogsCenter;
import seedu.taskman.commons.exceptions.DataConversionException;
import seedu.taskman.commons.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Config values used by the app
 */
public class Config extends ConfigData {

    public static final String DEFAULT_CONFIG_FILE = "config.json";

    private static Config instance;

    private Config() {
    }

    public static Config getInstance(){
        if (instance == null){
            instance = new Config();
        }
        return instance;
    }

    /**
     * Read and load the config data from the specified file to the config instance
     *
     * @param configFilePath
     * @return true if data is successfully read from file, false otherwise
     * @throws DataConversionException
     */
    public static boolean readConfig(String configFilePath) throws DataConversionException {
        Optional<ConfigData> readData = ConfigUtil.readConfig(configFilePath);
        if (readData.isPresent()){
            update(Config.getInstance(), readData.get());
            return true;
        }
        return false;
    }

    public static void saveConfig(String configFilePath) throws IOException {
        ConfigUtil.saveConfig(Config.getInstance(), configFilePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appTitle, logLevel, userPrefsFilePath, taskManFilePath, taskManName);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("App title : " + appTitle);
        sb.append("\nCurrent log level : " + logLevel);
        sb.append("\nPreference file Location : " + userPrefsFilePath);
        sb.append("\nLocal data file location : " + taskManFilePath);
        sb.append("\nTaskMan name : " + taskManName);
        return sb.toString();
    }

    /**
     * A class for accessing the Config File.
     */
    private static class ConfigUtil {

        private static final Logger logger = LogsCenter.getLogger(ConfigUtil.class);

        /**
         * Returns the Config object from the given file or {@code Optional.empty()} object if the file is not found.
         *   If any values are missing from the file, default values will be used, as long as the file is a valid json file.
         * @param configFilePath cannot be null.
         * @throws DataConversionException if the file format is not as expected.
         */
        public static Optional<ConfigData> readConfig(String configFilePath) throws DataConversionException {

            assert configFilePath != null;

            File configFile = new File(configFilePath);

            if (!configFile.exists()) {
                logger.info("Config file "  + configFile + " not found");
                return Optional.empty();
            }

            ConfigData data;

            try {
                data = FileUtil.deserializeObjectFromJsonFile(configFile, ConfigData.class);
            } catch (IOException e) {
                logger.warning("Error reading from config file " + configFile + ": " + e);
                throw new DataConversionException(e);
            }

            return Optional.of(data);
        }

        /**
         * Saves the Config object to the specified file.
         *   Overwrites existing file if it exists, creates a new file if it doesn't.
         * @param config cannot be null
         * @param configFilePath cannot be null
         * @throws IOException if there was an error during writing to the file
         */
        public static void saveConfig(Config config, String configFilePath) throws IOException {
            assert config != null;
            assert configFilePath != null;

            FileUtil.serializeObjectToJsonFile(new File(configFilePath), config);
        }

    }
}
