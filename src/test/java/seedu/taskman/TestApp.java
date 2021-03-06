package seedu.taskman;

import javafx.stage.Screen;
import javafx.stage.Stage;
import seedu.taskman.commons.core.config.Config;
import seedu.taskman.commons.core.GuiSettings;
import seedu.taskman.model.ReadOnlyTaskMan;
import seedu.taskman.model.UserPrefs;
import seedu.taskman.storage.XmlSerializableTaskMan;
import seedu.taskman.testutil.TestUtil;

import java.util.function.Supplier;

/**
 * This class is meant to override some properties of MainApp so that it will be suited for
 * testing
 */
public class TestApp extends MainApp {

    public static final String SAVE_LOCATION_FOR_TESTING = TestUtil.getFilePathInSandboxFolder("sampleData.xml");
    protected static final String DEFAULT_PREF_FILE_LOCATION_FOR_TESTING = TestUtil.getFilePathInSandboxFolder("pref_testing.json");
    public static final String APP_TITLE = "Test App";
    protected static final String TASK_MAN_NAME = "Test";
    protected Supplier<ReadOnlyTaskMan> initialDataSupplier = () -> null;
    protected String saveFileLocation = SAVE_LOCATION_FOR_TESTING;

    public TestApp() {
    }

    public TestApp(Supplier<ReadOnlyTaskMan> initialDataSupplier, String saveFileLocation) {
        super();
        this.initialDataSupplier = initialDataSupplier;
        this.saveFileLocation = saveFileLocation;

        // If some initial LOCAL data has been provided, write those to the file
        if (initialDataSupplier.get() != null) {
            TestUtil.createDataFileWithData(
                    new XmlSerializableTaskMan(this.initialDataSupplier.get()),
                    this.saveFileLocation);
        }
    }

    @Override
    protected void initConfig(String configFilePath) {
        super.initConfig(configFilePath);
        config.setAppTitle(APP_TITLE);
        config.setTaskManFilePath(saveFileLocation);
        config.setUserPrefsFilePath(DEFAULT_PREF_FILE_LOCATION_FOR_TESTING);
        config.setTaskManName(TASK_MAN_NAME);
    }

    @Override
    protected UserPrefs initPrefs(Config config) {
        UserPrefs userPrefs = super.initPrefs(config);
        double x = Screen.getPrimary().getVisualBounds().getMinX();
        double y = Screen.getPrimary().getVisualBounds().getMinY();
        userPrefs.updateLastUsedGuiSetting(new GuiSettings(600.0, 600.0, (int) x, (int) y));
        return userPrefs;
    }


    @Override
    public void start(Stage primaryStage) {
        ui.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
