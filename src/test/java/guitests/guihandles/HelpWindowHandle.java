package guitests.guihandles;

import guitests.GuiRobot;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/**
 * Provides a handle to the help window of the app.
 */
public class HelpWindowHandle extends GuiHandle {

    private static final String HELP_WINDOW_TITLE = "Help";
    private static final String HELP_WINDOW_ROOT_FIELD_ID = "#helpWindowRoot";

    public HelpWindowHandle(GuiRobot guiRobot, Stage primaryStage) {
        super(guiRobot, primaryStage, HELP_WINDOW_TITLE);
        guiRobot.sleep(1000);
    }

    public boolean isWindowOpen() {
        return getNode(HELP_WINDOW_ROOT_FIELD_ID) != null;
    }

    public void closeWindow() {
        guiRobot.push(KeyCode.F1);
        guiRobot.sleep(500);
    }

}
