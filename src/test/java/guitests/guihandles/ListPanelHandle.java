package guitests.guihandles;


import guitests.GuiRobot;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.control.TableView;
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

    public static final String SCHEDULE_LIST_VIEW_ID = "#scheduleTableView";
    public static final String FLOATING_LIST_VIEW_ID = "#floatingTableView";
    public static final String DEADLINE_LIST_VIEW_ID = "#deadlineTableView";


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
                assert false: "Unsupported Panel Type";
            }
        }
    }

    public List<Activity> getSelectedTasks() {
        TableView<Activity> taskList = getTableView();
        return taskList.getSelectionModel().getSelectedItems();
    }

    // TODO Resolve generic type issue.
    @SuppressWarnings("unchecked")
    public  TableView<Activity> getTableView(){
        return (TableView<Activity>) getNode(listViewId);
    }


    /**
     * Returns true if the list is showing the task details correctly and in correct order.
     *
     * @param tasks A list of task in the correct order.
     */
    public boolean isListMatching(Activity... tasks) {
        return this.isListMatching(0, tasks);
    }

    /**
     * Clicks on the TableView.
     */
    public void clickOnTableView() {
        Point2D point = TestUtil.getScreenMidPoint(getTableView());
        guiRobot.clickOn(point.getX(), point.getY());
    }

    /**
     * Returns true if the {@code tasks} appear as the sub list (in that order) at position {@code startPosition}.
     */
    public boolean containsInOrder(int startPosition, Activity... tasks) {
        List<Activity> tasksInList = getTableView().getItems();

        // Return false if the list in panel is too short to contain the given list
        if (startPosition + tasks.length > tasksInList.size()) {
            return false;
        }

        // Return false if any of the tasks doesn't match
        for (int i = 0; i < tasks.length; i++) {
            if (!tasksInList.get(startPosition + i).getTitle().title.equals(tasks[i].getTitle().title)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns true if the list is showing the task details correctly and in correct order.
     *
     * @param startPosition The starting position of the sub list.
     * @param tasks         A list of task in the correct order.
     */
    public boolean isListMatching(int startPosition, Activity... tasks) throws IllegalArgumentException {
        if (tasks.length + startPosition != getTableView().getItems().size()) {
            throw new IllegalArgumentException("List size mismatched\n" +
                    "Expected " + (getTableView().getItems().size() - 1) + " tasks");
        }
        assertTrue(this.containsInOrder(startPosition, tasks));
        for (int i = 0; i < tasks.length; i++) {
            final int scrollTo = i + startPosition;
            guiRobot.interact(() -> getTableView().scrollTo(scrollTo));
            guiRobot.sleep(200);
            if (!TestUtil.compareRowAndTask(getTaskRowHandle(startPosition + i), tasks[i])) {
                return false;
            }
        }
        return true;
    }


    public TaskRowHandle navigateToActivity(String title) {
        guiRobot.sleep(500); //Allow a bit of time for the list to be updated
        final Optional<Activity> task = getTableView().getItems().stream().filter(p -> p.getTitle().title.equals(title)).findAny();
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
            getTableView().scrollTo(index);
            guiRobot.sleep(150);
            getTableView().getSelectionModel().select(index);
        });
        guiRobot.sleep(100);
        return getTaskRowHandle(task);
    }


    /**
     * Returns the position of the task given, {@code NOT_FOUND} if not found in the list.
     */
    public int getActivityIndex(Activity targetTask) {
        List<Activity> tasksInList = getTableView().getItems();
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
        return getTableView().getItems().get(index);
    }

    public TaskRowHandle getTaskRowHandle(int index) {
        return new TaskRowHandle(guiRobot, primaryStage, getTableView().getItems().get(index));
    }

    public TaskRowHandle getTaskRowHandle(Activity task) {
        ObservableList<Activity> taskList = getTableView().getItems();
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
        return getTableView().getItems().size();
    }
}
