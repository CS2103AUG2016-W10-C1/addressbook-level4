package guitests;

import org.junit.Test;
import seedu.taskman.commons.core.Messages;
import seedu.taskman.model.event.Activity;

import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test select deadlines()
 */
public class SelectCommandTest extends TaskManGuiTest {

    public static final Pattern SELECT_SUCCESS_MESSAGE_CHECK =
            Pattern.compile("(?:\\S*\\s*)*");


    @Test
    public void selectTask_nonEmptyList() {

        assertSelectionInvalid(10); //invalid index
        assertNoTaskSelected();

        assertSelectionSuccess(1); //first task in the list
        int taskCount = testTasks.getTypicalTasks().length;
        assertSelectionSuccess(taskCount); //last task in the list
        int middleIndex = taskCount / 2;
        assertSelectionSuccess(middleIndex); //a task in the middle of the list

        assertSelectionInvalid(taskCount + 1); //invalid index
        assertTaskSelected(middleIndex); //assert previous selection remains

        /* Testing other invalid indexes such as -1 should be done when testing the SelectCommand */
    }

    @Test
    public void selectTask_emptyList() {
        commandBox.runCommand("clear");
        assertDeadlineListSize(0);
        assertSelectionInvalid(1); //invalid index
    }

    private void assertSelectionInvalid(int index) {
        commandBox.runCommand("select d" + index);
        assertResultMessage(Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
    }

    private void assertSelectionSuccess(int index) {
        commandBox.runCommand("select d" + index);
        System.out.println(resultDisplay.getText());
        assertTrue(SELECT_SUCCESS_MESSAGE_CHECK.matcher(resultDisplay.getText()).matches());
        assertTaskSelected(index);
    }

    private void assertTaskSelected(int index) {
        assertEquals(deadlineListPanel.getSelectedTasks().size(), 1);
        Activity selectedTask = deadlineListPanel.getSelectedTasks().get(0);
        assertEquals(deadlineListPanel.getActivity(index - 1), selectedTask);
    }

    private void assertNoTaskSelected() {
        assertEquals(deadlineListPanel.getSelectedTasks().size(), 0);
    }

}
