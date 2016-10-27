package seedu.taskman.ui;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import seedu.taskman.commons.core.LogsCenter;
import seedu.taskman.commons.events.ui.TaskPanelSelectionChangedEvent;
import seedu.taskman.commons.util.FxViewUtil;
import seedu.taskman.model.event.Activity;

import java.util.logging.Logger;
//@@author A0140136W

/**
 * Panel containing the list of tasks.
 */
public class ActivityPanel extends UiPart implements ListPanel {
    private final Logger logger = LogsCenter.getLogger(ActivityPanel.class);
    private static final String FXML = "ActivityPanel.fxml";
    private AnchorPane panel;
    private AnchorPane placeHolderPane;
    private Activity.PanelType panelType;



    @FXML
    private ListView<Activity> listView;

    @FXML
    private TitledPane titledPane;

    public ActivityPanel() {
        super();
    }

    @Override
    public void setNode(Node node) {
        panel = (AnchorPane) node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }

    @Override
    public void setPlaceholder(AnchorPane pane) {
        this.placeHolderPane = pane;
    }

    public static ActivityPanel load(Stage primaryStage, AnchorPane taskListPlaceholder,
                                     ObservableList<Activity> taskList, Activity.PanelType panelType) {
        ActivityPanel activityPanel =
                UiPartLoader.loadUiPart(primaryStage, taskListPlaceholder, new ActivityPanel());
        activityPanel.configure(taskList, panelType);
        return activityPanel;
    }

    private void configure(ObservableList<Activity> taskList, Activity.PanelType panelType) {
        this.panelType = panelType;
        setConnections(taskList);
        addToPlaceholder();
        titledPane.setText(panelType.getName());
    }

    // TODO Resolve generic type issue.
    private void setConnections(ObservableList<Activity> taskList) {      
        listView.setItems(taskList);
        listView.setCellFactory(listView -> new ActivityListViewCell(panelType));
        setEventHandlerForSelectionChangeEvent();
    }

    private void addToPlaceholder() {
        placeHolderPane.getChildren().add(panel);
        FxViewUtil.applyAnchorBoundaryParameters(panel, 0.0, 0.0, 0.0, 0.0);
        FxViewUtil.applyAnchorBoundaryParameters(titledPane, 0.0, 0.0, 0.0, 0.0);
    }

    // TODO Edit
    private void setEventHandlerForSelectionChangeEvent() {
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                logger.fine("Selection in task list panel changed to : '" + newValue + "'");
                raise(new TaskPanelSelectionChangedEvent(newValue, panelType));
            }
        });
    }

    // TODO Edit
    @Override
    public void scrollTo(int index) {
        Platform.runLater(() -> {
            listView.scrollTo(index);
            listView.getSelectionModel().clearAndSelect(index);
        });
    }

    @Override
    public void clearSelection() {
        Platform.runLater(() -> {
            listView.getSelectionModel().clearSelection();
        });
    }


}
