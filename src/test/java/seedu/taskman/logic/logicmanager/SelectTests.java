package seedu.taskman.logic.logicmanager;

import org.junit.Test;
import seedu.taskman.model.TaskMan;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.Task;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class SelectTests extends LogicManagerTestBase {

    @Test
    public void execute_selectInvalidArgsFormat_errorMessageShown() throws Exception {
        assertIncorrectIndexFormatBehaviorForCommand("select");
    }

    @Test
    public void execute_selectIndexNotFound_errorMessageShown() throws Exception {
        assertIndexNotFoundBehaviorForCommand("select");
    }

    @Test
    public void execute_select_jumpsToCorrectTask() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        List<Task> threeTasks = helper.generateTaskList(3);

        TaskMan expectedTaskMan = helper.generateTaskMan(threeTasks);
        helper.addToModel(model, threeTasks);

        assertCommandStateChange("select 2",
                expectedTaskMan,
                expectedTaskMan.getActivityList());
        assertEquals(1, targetedJumpIndex);
        assertEquals(model.getFilteredActivityList().get(1), new Activity(threeTasks.get(1)));
    }

}
