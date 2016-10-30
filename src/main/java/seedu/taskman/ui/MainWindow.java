package seedu.taskman.ui;

import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.taskman.Constants;
import seedu.taskman.commons.core.GuiSettings;
import seedu.taskman.commons.core.config.Config;
import seedu.taskman.commons.events.ui.ExitAppRequestEvent;
import seedu.taskman.logic.Logic;
import seedu.taskman.model.UserPrefs;
import seedu.taskman.model.event.Activity;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart {

    private static final String ICON = Constants.APP_ICON_FILE_PATH;
    private static final String FXML = "MainWindow.fxml";
    public static final int MIN_HEIGHT = 600;
    public static final int MIN_WIDTH = 450;

    private Logic logic;

    // Independent Ui parts residing in this Ui container
    private ActivityPanel schedulePanel;
    private ActivityPanel deadlinePanel;
    private ActivityPanel floatingPanel;
    private ResultDisplay resultDisplay;
    private StatusBarFooter statusBarFooter;
    private CommandBox commandBox;
    private Config config;
    private UserPrefs userPrefs;

    // Handles to elements of this Ui container
    private VBox rootLayout;
    private Scene scene;

    private String taskManName;

    @FXML
    private AnchorPane commandBoxPlaceholder;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private AnchorPane schedulePanelPlaceholder;
    
    @FXML
    private AnchorPane deadlinePanelPlaceholder;
    
    @FXML
    private AnchorPane floatingPanelPlaceholder;

    @FXML
    private AnchorPane resultDisplayPlaceholder;

    @FXML
    private AnchorPane statusbarPlaceholder;


    public MainWindow() {
        super();
    }

    @Override
    public void setNode(Node node) {
        rootLayout = (VBox) node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }

    public static MainWindow load(Stage primaryStage, Config config, UserPrefs prefs, Logic logic) {

        MainWindow mainWindow = UiPartLoader.loadUiPart(primaryStage, new MainWindow());
        mainWindow.configure(config.getAppTitle(), config.getTaskManName(), config, prefs, logic);
        return mainWindow;
    }

    private void configure(String appTitle, String taskManName, Config config, UserPrefs prefs,
                           Logic logic) {

        //Set dependencies
        this.logic = logic;
        this.taskManName = taskManName;
        this.config = config;
        this.userPrefs = prefs;

        //Configure the UI
        setTitle(appTitle);
        setIcon(ICON);
        setWindowMinSize();
        setWindowDefaultSize(prefs);
        scene = new Scene(rootLayout);
        primaryStage.setScene(scene);

        setAccelerators();
    }

    private void setAccelerators() {
        helpMenuItem.setAccelerator(KeyCombination.valueOf("F1"));
    }

    void fillInnerParts() {
        schedulePanel = ActivityPanel.load(primaryStage, getSchedulePanelPlaceholder(), logic.getSortedScheduleList(), Activity.PanelType.SCHEDULE);
        deadlinePanel = ActivityPanel.load(primaryStage, getDeadlinePanelPlaceholder(), logic.getSortedDeadlineList(), Activity.PanelType.DEADLINE);
        floatingPanel = ActivityPanel.load(primaryStage, getFloatingPanelPlaceholder(), logic.getSortedFloatingList(), Activity.PanelType.FLOATING);
        resultDisplay = ResultDisplay.load(primaryStage, getResultDisplayPlaceholder());
        statusBarFooter = StatusBarFooter.load(primaryStage, getStatusbarPlaceholder(), config.getTaskManFilePath());
        commandBox = CommandBox.load(primaryStage, getCommandBoxPlaceholder(), resultDisplay, logic);
        configureFocus();
    }

    private AnchorPane getCommandBoxPlaceholder() {
        return commandBoxPlaceholder;
    }

    private AnchorPane getStatusbarPlaceholder() {
        return statusbarPlaceholder;
    }

    private AnchorPane getResultDisplayPlaceholder() {
        return resultDisplayPlaceholder;
    }

    public AnchorPane getSchedulePanelPlaceholder() {
        return schedulePanelPlaceholder;
    }
    
    public AnchorPane getDeadlinePanelPlaceholder() {
        return deadlinePanelPlaceholder;
    }
    
    public AnchorPane getFloatingPanelPlaceholder() {
        return floatingPanelPlaceholder;
    }

    public void hide() {
        primaryStage.hide();
    }

    private void setTitle(String appTitle) {
        primaryStage.setTitle(appTitle);
    }

    public void clearListPanels(){
        deadlinePanel.clearSelection();
        floatingPanel.clearSelection();
        schedulePanel.clearSelection();
    }

    public void clearListPanelsExclude(Activity.PanelType panelType){
        if (panelType != Activity.PanelType.DEADLINE) {
            deadlinePanel.clearSelection();
        }
        if (panelType != Activity.PanelType.SCHEDULE) {
           schedulePanel.clearSelection();
        }
        if (panelType != Activity.PanelType.FLOATING) {
            floatingPanel.clearSelection();
        }
    }

    public ListPanel getListPanel(Activity.PanelType panelType){
        switch (panelType){
            case DEADLINE: {
                return deadlinePanel;
            }
            case SCHEDULE:{
                return schedulePanel;
            }
            case FLOATING:{
                return floatingPanel;
            }
            default:{
                assert false: "Panel Type not supported.";
                return deadlinePanel;
            }
        }

    }

    /**
     * Sets the default size based on user preferences.
     */
    protected void setWindowDefaultSize(UserPrefs prefs) {
        primaryStage.setHeight(prefs.getGuiSettings().getWindowHeight());
        primaryStage.setWidth(prefs.getGuiSettings().getWindowWidth());
        if (prefs.getGuiSettings().getWindowCoordinates() != null) {
            primaryStage.setX(prefs.getGuiSettings().getWindowCoordinates().getX());
            primaryStage.setY(prefs.getGuiSettings().getWindowCoordinates().getY());
        }
    }

    private void setWindowMinSize() {
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setMinWidth(MIN_WIDTH);
    }

    /**
     * Returns the current size and the position of the main Window.
     */
    public GuiSettings getCurrentGuiSetting() {
        return new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
    }

    @FXML
    public void handleHelp() {
        HelpWindow helpWindow = HelpWindow.load(primaryStage);
        helpWindow.show();
    }

    public void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        raise(new ExitAppRequestEvent());
    }
    
    @FXML
    public void handleEnterPressed(KeyEvent key){
       if (commandBox.getTextField().isFocused()) {
           return;
       } else if (key.getCode().isLetterKey()){
           commandBox.getTextField().requestFocus();
       } else if (key.getCode().isFunctionKey()) {
           resultDisplay.getResultDisplayArea().requestFocus();
       } else {
           return;
       }
    }
    
    private void configureFocus() {
        ArrayList<Node> nodes = getAllNodes();
        for (Node node : nodes) {
            if (node.getClass().equals(ListView.class)) {
                node.setFocusTraversable(true);
            } else {
                node.setFocusTraversable(false);
            }
        }
    }

    private ArrayList<Node> getAllNodes() {
        ArrayList<Node> nodes = new ArrayList<Node>();
        addAllDescendents(rootLayout, nodes);
        return nodes;
    }

    private void addAllDescendents(Parent parent, ArrayList<Node> nodes) {
        for (Node node : parent.getChildrenUnmodifiable()) {
            nodes.add(node);
            if (node instanceof Parent)
                addAllDescendents((Parent)node, nodes);
        }
    }


}
