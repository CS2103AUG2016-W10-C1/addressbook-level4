package guitests.guihandles;

import guitests.GuiRobot;
import javafx.stage.Stage;
import seedu.taskman.TestApp;
import seedu.taskman.model.event.Activity;

/**
 * Provides a handle for the main GUI.
 */
public class MainGuiHandle extends GuiHandle {

    public MainGuiHandle(GuiRobot guiRobot, Stage primaryStage) {
        super(guiRobot, primaryStage, TestApp.APP_TITLE);
    }

    public ListPanelHandle getDeadlineListPanel() {
        return new ListPanelHandle(guiRobot, primaryStage, Activity.PanelType.DEADLINE);
    }

    public ListPanelHandle getFloatingListPanel() {
        return new ListPanelHandle(guiRobot, primaryStage, Activity.PanelType.FLOATING);
    }

    public ListPanelHandle getScheduleListPanel() {
        return new ListPanelHandle(guiRobot, primaryStage, Activity.PanelType.SCHEDULE);
    }

    public ResultDisplayHandle getResultDisplay() {
        return new ResultDisplayHandle(guiRobot, primaryStage);
    }

    public CommandBoxHandle getCommandBox() {
        return new CommandBoxHandle(guiRobot, primaryStage, TestApp.APP_TITLE);
    }

    public MainMenuHandle getMainMenu() {
        return new MainMenuHandle(guiRobot, primaryStage);
    }

}
