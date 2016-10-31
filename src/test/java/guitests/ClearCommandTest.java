package guitests;

import org.junit.Test;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.Task;
import seedu.taskman.testutil.TestTask;
import seedu.taskman.testutil.TestUtil;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class ClearCommandTest extends TaskManGuiTest {

    @Test
    public void clear() {

        //verify a non-empty list can be cleared
        List<TestTask> currentList = TestUtil.asList(testTasks.getTypicalTasks());
        Activity[] expectedList = TestUtil.getActivitiesArray(currentList);
        TestUtil.sortActivitiesByDeadline(expectedList);
        assertTrue(deadlineListPanel.isListMatching(expectedList));
        assertClearCommandSuccess();

        //verify other commands can work after a clear command
        commandBox.runCommand(testTasks.taskCS2102.getAddCommand());
        assertTrue(deadlineListPanel.isListMatching(new Activity(new Task(testTasks.taskCS2102))));
        commandBox.runCommand("delete d1");
        assertDeadlineListSize(0);

        //verify clear command works when the list is empty
        assertClearCommandSuccess();
    }

    private void assertClearCommandSuccess() {
        commandBox.runCommand("clear");
        assertDeadlineListSize(0);
        assertResultMessage("TaskMan has been cleared!");
    }
}
