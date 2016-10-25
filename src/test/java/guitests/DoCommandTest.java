package guitests;

import guitests.guihandles.TaskRowHandle;
import org.junit.Test;
import seedu.taskman.commons.core.Messages;
import seedu.taskman.logic.commands.DoCommand;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.Task;
import seedu.taskman.testutil.TestTask;
import seedu.taskman.testutil.TestUtil;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

//TODO Change to DoCommandTest and MarkCommandTest
public class DoCommandTest extends TaskManGuiTest {
    
    @Test
    public void add() {
        //add one task
        List<TestTask> currentList = TestUtil.asList(testTasks.getTypicalTasks());
        TestTask taskToAdd = testTasks.taskCS2102;
        assertAddSuccess(taskToAdd, currentList);
        currentList = TestUtil.addTasksToList(currentList, taskToAdd);

        //add another task
        taskToAdd = testTasks.taskCS2104;
        assertAddSuccess(taskToAdd, currentList);
        currentList = TestUtil.addTasksToList(currentList, taskToAdd);

        //add duplicate task
        commandBox.runCommand(testTasks.taskCS2102.getAddCommand());
        Activity[] expectedList = TestUtil.getActivitiesArray(currentList);
        TestUtil.sortActivitiesByDeadline(expectedList);
        assertResultMessage(DoCommand.MESSAGE_DUPLICATE_EVENT);
        assertTrue(deadlineListPanel.isListMatching(expectedList));

        //add to empty list
        commandBox.runCommand("clear");
        assertAddSuccess(testTasks.taskCS2101, new ArrayList<>());

        //invalid command
        commandBox.runCommand("dos Johnny");
        assertResultMessage(Messages.MESSAGE_UNKNOWN_COMMAND);
    }

    private void assertAddSuccess(TestTask taskToAdd, List<TestTask> currentList) {
        commandBox.runCommand(taskToAdd.getAddCommand());

        //confirm the new row contains the right data
        TaskRowHandle addedRow = deadlineListPanel.navigateToActivity(taskToAdd.getTitle().title);
        assertMatching(new Activity(new Task(taskToAdd)), addedRow);

        //confirm the list now contains all previous tasks plus the new task
        List<TestTask> expectedList = TestUtil.addTasksToList(currentList, taskToAdd);
        Activity[] expectedActivityList = TestUtil.getActivitiesArray(expectedList);
        TestUtil.sortActivitiesByDeadline(expectedActivityList);
        assertTrue(deadlineListPanel.isListMatching(expectedActivityList));
    }

}
