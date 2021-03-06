package guitests.guihandles;

import guitests.GuiRobot;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import seedu.taskman.TestApp;

import java.util.Arrays;

/**
 * Provides a handle to the main menu of the app.
 */
public class MainMenuHandle extends GuiHandle {
    public MainMenuHandle(GuiRobot guiRobot, Stage primaryStage) {
        super(guiRobot, primaryStage, TestApp.APP_TITLE);
    }

    public HelpWindowHandle openHelpWindowUsingShortcut() {
        useF1Shortcut();
        return new HelpWindowHandle(guiRobot, primaryStage);
    }

    private void useF1Shortcut() {
        guiRobot.push(KeyCode.F1);
        guiRobot.sleep(500);
    }
}
