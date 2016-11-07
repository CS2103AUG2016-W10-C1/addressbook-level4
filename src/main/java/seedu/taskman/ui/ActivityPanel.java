package seedu.taskman.ui;

import com.sun.javafx.scene.control.skin.ListViewSkin;
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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
//@@author A0140136W

/**
 * Panel containing the list of tasks.
 */
public class ActivityPanel extends UiPart {
    private final Logger logger = LogsCenter.getLogger(ActivityPanel.class);
    private static final String FXML = "ActivityPanel.fxml";
    private AnchorPane panel;
    private AnchorPane placeHolderPane;
    private Activity.PanelType panelType;
    private ScheduledExecutorService refreshService;

    @FXML
    private ListView<Activity> listView;

    @FXML
    private TitledPane titledPane;

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
        refreshService = createRefreshService();
        this.panelType = panelType;
        setConnections(taskList);
        addToPlaceholder();
        listView.setId(panelType.getName().toLowerCase()+"ListView");
        titledPane.setText(panelType.getName());
    }

    private ScheduledExecutorService createRefreshService() {
        RefreshListViewSkin<Activity> skin = new RefreshListViewSkin<>(listView);
        listView.setSkin(skin);

        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        Runnable invalidateListView = skin::refresh;
        service.scheduleAtFixedRate(invalidateListView, secondsToNextMinute(), 60, TimeUnit.SECONDS);
        return service;
    }

    private long secondsToNextMinute() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime roundCeiling = now.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1);
        return roundCeiling.toEpochSecond(ZoneOffset.UTC) - now.toEpochSecond(ZoneOffset.UTC);
    }

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

    private void setEventHandlerForSelectionChangeEvent() {
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                logger.fine("Selection in task list panel changed to : '" + newValue + "'");
                raise(new TaskPanelSelectionChangedEvent(newValue, panelType));
            }
        });
    }

    /**
     * Scroll the listView to the given index
     * @param index
     */
    public void scrollTo(int index) {
        Platform.runLater(() -> {
            listView.scrollTo(index);
            listView.getSelectionModel().clearAndSelect(index);
        });
    }

    /**
     * Clear the selection of the listView, if any
     */
    public void clearSelection() {
        Platform.runLater(() -> {
            listView.getSelectionModel().clearSelection();
        });
    }


    /**
     * ListViewSkin for refreshing the ListView it is binded to
     */
    private static class RefreshListViewSkin<T> extends ListViewSkin<T> {

        public RefreshListViewSkin(ListView<T> list) {
            super(list);
        }

        /**
         * Refreshes the list view using an undocumented public command
         * Refer to http://stackoverflow.com/a/25962110 for more info
         */
        public void refresh() {
            super.flow.rebuildCells();
        }
    }
}
