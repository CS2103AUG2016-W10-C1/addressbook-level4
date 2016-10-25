package seedu.taskman.logic.logicmanager;

import org.junit.Ignore;
import org.junit.Test;
import seedu.taskman.model.TaskMan;
import seedu.taskman.model.event.Task;

public class DoTests extends LogicManagerTestBase {
    @Test
    public void execute_do_invalidArgsFormat() throws Exception {
        // no args
        assertCommandNoStateChange("do");

        // non-existent flag
        assertCommandNoStateChange("do x/");
    }

    @Test
    public void execute_do_invalidTaskData() throws Exception {
        // bad deadline
        assertCommandNoStateChange("do Valid Title d/invalid Deadline");

        // bad schedule
        assertCommandNoStateChange("do Valid Title s/invalid Schedule");

        // bad title
        assertCommandNoStateChange("do []\\[;]");
    }

    // todo: should fix
    @Ignore
    @Test
    public void execute_do_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        Task toBeAdded = helper.generateFullTask(1);
        TaskMan expectedTaskMan = new TaskMan();
        expectedTaskMan.addActivity(toBeAdded);

        assertCommandStateChange(helper.generateDoCommand(toBeAdded),
                expectedTaskMan,
                expectedTaskMan.getActivityList());
    }

    @Test
    public void execute_doDuplicate_notAllowed() throws Exception {
        // setup expected
        TestDataHelper helper = new TestDataHelper();
        Task toBeAdded = helper.generateFullTask(1);
        TaskMan expectedTaskMan = new TaskMan();
        expectedTaskMan.addActivity(toBeAdded);

        // setup actual
        model.addActivity(toBeAdded);

        // execute command and verify result
        assertCommandStateChange(
                helper.generateDoCommand(toBeAdded),
                expectedTaskMan,
                expectedTaskMan.getActivityList());

    }
}
