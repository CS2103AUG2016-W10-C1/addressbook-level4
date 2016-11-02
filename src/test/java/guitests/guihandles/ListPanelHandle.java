package guitests.guihandles;


import guitests.GuiRobot;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import seedu.taskman.TestApp;
import seedu.taskman.model.event.Activity;
import seedu.taskman.testutil.TestUtil;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

/**
 * Provides a handle for the panel containing the task list.
 */
public class ListPanelHandle extends GuiHandle {

    public static final int NOT_FOUND = -1;

    private final String listViewId;

    public static final String SCHEDULE_LIST_VIEW_ID = "#scheduleListView";
    public static final String FLOATING_LIST_VIEW_ID = "#floatingListView";
    public static final String DEADLINE_LIST_VIEW_ID = "#deadlineListView";


    public ListPanelHandle(GuiRobot guiRobot, Stage primaryStage, Activity.PanelType panelType) {
        super(guiRobot, primaryStage, TestApp.APP_TITLE);
        switch(panelType){
            case SCHEDULE:{
                listViewId = SCHEDULE_LIST_VIEW_ID;
                break;
            }
            case FLOATING:{
                listViewId = FLOATING_LIST_VIEW_ID;
                break;
            }
            case DEADLINE:{
                listViewId = DEADLINE_LIST_VIEW_ID;
                break;
            }
            default:{
                listViewId = "unsupported";
                assert false : "Unsupported Panel Type";
            }
        }
    }

    public List<Activity> getSelectedTasks() {
        ListView<Activity> taskList = getListView();
        return taskList.getSelectionModel().getSelectedItems();
    }

    // TODO Resolve generic type issue.
    @SuppressWarnings("unchecked")
    public ListView<Activity> getListView(){
        return (ListView<Activity>) getNode(listViewId);
    }


    /**
     * Returns true if the list is showing the task details correctly and in correct order.
     *
     * @param activities A list of task in the correct order.
     */
    public boolean isListMatching(Activity... activities) {
        if (activities.length != getListView().getItems().size()) {
            throw new IllegalArgumentException("List size mismatched\n" +
                    "Expected " + activities.length + " tasks");
        }
        assertTrue(this.containsInOrder(0, activities));
        for (int i = 0; i < activities.length; i++) {
            final int scrollTo = i;
            guiRobot.interact(() -> getListView().scrollTo(scrollTo));
            guiRobot.sleep(200);
            if (!TestUtil.compareRowAndTask(getTaskRowHandle(i), activities[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Clicks on the TableView.
     */
    public void clickOnListView() {
        Point2D point = TestUtil.getScreenMidPoint(getListView());
        guiRobot.clickOn(point.getX(), point.getY());
    }

    /**
     * Returns true if the {@code tasks} appear as the sub list (in that order) at position {@code startPosition}.
     */
    public boolean containsInOrder(int startPosition, Activity... tasks) {
        List<Activity> tasksInList = getListView().getItems();

        // Return false if the list in panel is too short to contain the given list
        if (startPosition + tasks.length > tasksInList.size()) {
            return false;
        }

        // Return false if any of the tasks doesn't match
        for (int i = 0; i < tasks.length; i++) {
            if (!tasksInList.get(startPosition + i).getTitle().toString().equals(tasks[i].getTitle().toString())) {
                return false;
            }
        }

        return true;
    }

    public TaskRowHandle navigateToActivity(String title) {
        guiRobot.sleep(500); //Allow a bit of time for the list to be updated
        final Optional<Activity> task = getListView().getItems().stream().filter(p -> p.getTitle().toString().equals(title)).findAny();
        if (!task.isPresent()) {
            throw new IllegalStateException("Title not found: " + title);
        }

        return navigateToActivity(task.get());
    }

    /**
     * Navigates the TableView to display and select the task.
     */
    public TaskRowHandle navigateToActivity(Activity task) {
        int index = getActivityIndex(task);

        guiRobot.interact(() -> {
            getListView().scrollTo(index);
            guiRobot.sleep(150);
            getListView().getSelectionModel().select(index);
        });
        guiRobot.sleep(100);
        return getTaskRowHandle(task);
    }


    /**
     * Returns the position of the task given, {@code NOT_FOUND} if not found in the list.
     */
    public int getActivityIndex(Activity targetTask) {
        List<Activity> tasksInList = getListView().getItems();
        for (int i = 0; i < tasksInList.size(); i++) {
            if (tasksInList.get(i).getTitle().equals(targetTask.getTitle())) {
                return i;
            }
        }
        return NOT_FOUND;
    }

    /**
     * Gets a task from the list by index
     */
    public Activity getActivity(int index) {
        return getListView().getItems().get(index);
    }

    public TaskRowHandle getTaskRowHandle(int index) {
        return new TaskRowHandle(guiRobot, primaryStage, getListView().getItems().get(index));
    }

    public TaskRowHandle getTaskRowHandle(Activity task) {
        ObservableList<Activity> taskList = getListView().getItems();
        Optional<Activity> hit = taskList.stream()
                .filter(n -> new TaskRowHandle(guiRobot, primaryStage, n).isSameTask(task))
                .findFirst();
        if (hit.isPresent()) {
            return new TaskRowHandle(guiRobot, primaryStage, hit.get());
        } else {
            return null;
        }
    }

    public int getNumberOfPeople() {
        return getListView().getItems().size();
    }
}
