package guitests;

import org.junit.Test;
import seedu.taskman.commons.core.Messages;
import seedu.taskman.model.event.Activity;
import seedu.taskman.testutil.TestTask;
import seedu.taskman.testutil.TestUtil;

import static org.junit.Assert.assertTrue;
import static seedu.taskman.logic.commands.DeleteCommand.MESSAGE_DELETE_EVENT_SUCCESS;


public class DeleteCommandTest extends TaskManGuiTest {

    @Test
    public void delete() {

        //delete the first in the list
        TestTask[] taskList = testTasks.getTypicalTasks();
        Activity[] currentList = TestUtil.getActivitiesArray(TestUtil.asList(taskList));
        TestUtil.sortActivitiesByDeadline(currentList);
        int targetIndex = 1;
        assertDeleteSuccess(targetIndex, currentList);

        //delete the last in the list
        currentList = TestUtil.removeActivityFromList(currentList, targetIndex);
        targetIndex = currentList.length;
        assertDeleteSuccess(targetIndex, currentList);

        //delete from the middle of the list
        currentList = TestUtil.removeActivityFromList(currentList, targetIndex);
        targetIndex = currentList.length / 2;
        assertDeleteSuccess(targetIndex, currentList);

        //invalid index
        commandBox.runCommand("delete d" + currentList.length + 1);
        assertResultMessage(Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);

    }

    /**
     * Runs the delete command to delete the task at specified index and confirms the result is correct.
     *
     * @param targetIndexOneIndexed e.g. to delete the first task in the list, 1 should be given as the target index.
     * @param currentList           A copy of the current list of tasks (before deletion).
     */
    private void assertDeleteSuccess(int targetIndexOneIndexed, final Activity[] currentList) {
        Activity taskToDelete = currentList[targetIndexOneIndexed - 1]; //-1 because array uses zero indexing
        Activity[] expectedRemainderActivities = TestUtil.removeActivityFromList(currentList, targetIndexOneIndexed);
        commandBox.runCommand("delete d" + targetIndexOneIndexed);

        //confirm the list now contains all previous tasks except the deleted task
        assertTrue(deadlineListPanel.isListMatching(expectedRemainderActivities));

        //confirm the result message is correct
        assertResultMessage(String.format(MESSAGE_DELETE_EVENT_SUCCESS, taskToDelete));
    }

}
