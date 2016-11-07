package seedu.taskman.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.taskman.commons.core.LogsCenter;
import seedu.taskman.logic.commands.AddCommand;
import seedu.taskman.logic.commands.AddECommand;
import seedu.taskman.logic.commands.ClearCommand;
import seedu.taskman.logic.commands.CompleteCommand;
import seedu.taskman.logic.commands.DeleteCommand;
import seedu.taskman.logic.commands.EditCommand;
import seedu.taskman.logic.commands.ExitCommand;
import seedu.taskman.logic.commands.HelpCommand;
import seedu.taskman.logic.commands.HistoryCommand;
import seedu.taskman.logic.commands.ListCommand;
import seedu.taskman.logic.commands.SelectCommand;
import seedu.taskman.logic.commands.StoragelocCommand;
import seedu.taskman.logic.commands.TagsCommand;
import seedu.taskman.logic.commands.UndoCommand;

import java.util.logging.Logger;

/**
 * Controller for a help page
 */
public class HelpWindow extends UiPart {

    private static final Logger logger = LogsCenter.getLogger(HelpWindow.class);
    private static final String FXML = "HelpWindow.fxml";
    final ObservableList<HelpTableViewRow> data = FXCollections.observableArrayList(
                                                  new HelpTableViewRow(AddCommand.COMMAND_WORD, AddCommand.MESSAGE_USAGE),
                                                  new HelpTableViewRow(AddECommand.COMMAND_WORD, AddECommand.MESSAGE_USAGE),
                                                  new HelpTableViewRow(ClearCommand.COMMAND_WORD, ClearCommand.MESSAGE_USAGE),
                                                  new HelpTableViewRow(CompleteCommand.COMMAND_WORD, CompleteCommand.MESSAGE_USAGE),
                                                  new HelpTableViewRow(DeleteCommand.COMMAND_WORD, DeleteCommand.MESSAGE_USAGE),
                                                  new HelpTableViewRow(EditCommand.COMMAND_WORD, EditCommand.MESSAGE_USAGE),
                                                  new HelpTableViewRow(ExitCommand.COMMAND_WORD, ExitCommand.MESSAGE_USAGE),
                                                  new HelpTableViewRow(HelpCommand.COMMAND_WORD, HelpCommand.MESSAGE_USAGE),
                                                  new HelpTableViewRow(HistoryCommand.COMMAND_WORD, HistoryCommand.MESSAGE_USAGE),
                                                  new HelpTableViewRow(ListCommand.COMMAND_WORD, ListCommand.MESSAGE_USAGE),
                                                  new HelpTableViewRow(SelectCommand.COMMAND_WORD, SelectCommand.MESSAGE_USAGE),
                                                  new HelpTableViewRow(StoragelocCommand.COMMAND_WORD, StoragelocCommand.MESSAGE_USAGE),
                                                  new HelpTableViewRow(TagsCommand.COMMAND_WORD, TagsCommand.MESSAGE_USAGE),
                                                  new HelpTableViewRow(UndoCommand.COMMAND_WORD, UndoCommand.MESSAGE_USAGE));


    private VBox mainPane;
    private Scene previousScene;
    private Scene helpScene;
    
    @FXML
    private TableView<HelpTableViewRow> helpTableView;
    
    @FXML
    private TableColumn<HelpTableViewRow, String> commandColumn;
    
    @FXML
    private TableColumn<HelpTableViewRow, String> formatColumn;

    public static HelpWindow load(Stage primaryStage, Scene mainScene) {
        logger.fine("Showing help page about the application.");
        HelpWindow helpWindow = UiPartLoader.loadUiPart(primaryStage, new HelpWindow());
        helpWindow.configure(mainScene);
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

    private void configure(Scene mainScene) {
        previousScene = mainScene;
        helpScene = new Scene(mainPane);
        primaryStage.setScene(helpScene);
        addKeyPressedFilters();
        initTable();
    }
    
    private void addKeyPressedFilters() {
        helpScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode code = event.getCode();
            if (code.equals(KeyCode.ESCAPE)) {
                handleBack();
            }
        });
    }
    
    private void initTable() {
        commandColumn.setCellValueFactory(
                      new PropertyValueFactory<HelpTableViewRow,String>("command"));
        formatColumn.setCellValueFactory(
                     new PropertyValueFactory<HelpTableViewRow,String>("format"));
        helpTableView.setItems(data);
    }

    public Scene getScene() {
        return helpScene;
    }
    
    public void handleBack() {
        primaryStage.setScene(previousScene);
    }
}