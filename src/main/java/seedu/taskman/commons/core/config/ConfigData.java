package seedu.taskman.commons.core.config;

import java.util.Objects;
import java.util.logging.Level;

public class ConfigData {

    public static final String DEFAULT_APP_TITLE = "TaskMan";
    public static final Level DEFAULT_LOG_LEVEL = Level.INFO;
    public static final String DEFAULT_USER_PREFS_FILE_PATH = "preferences.json";
    public static final String DEFAULT_TASK_MAN_FILE_PATH = "./data/taskMan.xml";
    public static final String DEFAULT_TASK_MAN_NAME = "MyTaskMan";

    // Config values customizable through config file
    protected String appTitle = DEFAULT_APP_TITLE;
    protected Level logLevel = DEFAULT_LOG_LEVEL;
    protected String userPrefsFilePath = DEFAULT_USER_PREFS_FILE_PATH;
    protected String taskManFilePath = DEFAULT_TASK_MAN_FILE_PATH;
    protected String taskManName = DEFAULT_TASK_MAN_NAME;

    public String getAppTitle() {
        return appTitle;
    }

    public void setAppTitle(String appTitle) {
        this.appTitle = appTitle;
    }

    public Level getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(Level logLevel) {
        this.logLevel = logLevel;
    }

    public String getUserPrefsFilePath() {
        return userPrefsFilePath;
    }

    public void setUserPrefsFilePath(String userPrefsFilePath) {
        this.userPrefsFilePath = userPrefsFilePath;
    }

    public String getTaskManFilePath() {
        return taskManFilePath;
    }

    public void setTaskManFilePath(String taskManFilePath) {
        this.taskManFilePath = taskManFilePath;
    }

    public String getTaskManName() {
        return taskManName;
    }

    public void setTaskManName(String taskManName) {
        this.taskManName = taskManName;
    }

    //@@author A0121299A
    protected static void update(ConfigData toUpdate, ConfigData source) {
        toUpdate.appTitle = source.appTitle;
        toUpdate.logLevel = source.logLevel;
        toUpdate.userPrefsFilePath = source.userPrefsFilePath;
        toUpdate.taskManFilePath = source.taskManFilePath;
        toUpdate.taskManName = source.taskManName;
    }

    public ConfigData getDataClone() {
        ConfigData clone = new ConfigData();
        update(clone, this);
        return clone;
    }

    //@@author
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ConfigData)) { //this handles null as well.
            return false;
        }

        ConfigData o = (ConfigData) other;

        return Objects.equals(appTitle, o.appTitle)
                && Objects.equals(logLevel, o.logLevel)
                && Objects.equals(userPrefsFilePath, o.userPrefsFilePath)
                && Objects.equals(taskManFilePath, o.taskManFilePath)
                && Objects.equals(taskManName, o.taskManName);
    }

}
