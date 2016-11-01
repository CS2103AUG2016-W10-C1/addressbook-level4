package seedu.taskman.ui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import seedu.taskman.commons.util.FxViewUtil;
import seedu.taskman.commons.core.LogsCenter;
import seedu.taskman.commons.events.ui.ExitAppRequestEvent;

import java.util.logging.Logger;

/**
 * Controller for a help page
 */
public class HelpWindow extends UiPart {

    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String FXML = "HelpWindow.fxml";

    private VBox mainPane;
    private Scene previousScene;

    @FXML
    private MenuItem backMenuItem;
    
    @FXML
    private TableView cheatSheet;

    public static HelpWindow load(Stage primaryStage) {
        logger.fine("Showing help page about the application.");
        HelpWindow helpWindow = UiPartLoader.loadUiPart(primaryStage, new HelpWindow());
        helpWindow.configure();
        return helpWindow;
    }

    @Override
    public void setNode(Node node) {
        mainPane = (VBox) node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }

    private void configure() {
        previousScene = primaryStage.getScene();
        Scene scene = new Scene(mainPane);
        primaryStage.setScene(scene);
        setAccelerators();
    }
    
    private void setAccelerators() {
        backMenuItem.setAccelerator(KeyCombination.valueOf("F1"));
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
    public void handleBack() {
        primaryStage.setScene(previousScene);
    }
}