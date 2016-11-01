package seedu.taskman.logic.logicmanager;

import org.junit.Test;
import seedu.taskman.model.TaskMan;
import seedu.taskman.model.event.Task;

public class AddTests extends LogicManagerTestBase {
    @Test
    public void execute_add_invalidArgsFormat() throws Exception {
        // no args
        assertCommandNoStateChange("add");

        // non-existent flag
        assertCommandNoStateChange("add x/");
    }

    @Test
    public void execute_add_invalidTaskData() throws Exception {
        // bad deadline
        assertCommandNoStateChange("add Valid Title d/invalid Deadline");

        // bad schedule
        assertCommandNoStateChange("add Valid Title s/invalid Schedule");

        // bad title
        assertCommandNoStateChange("add []\\[;]");
    }

    @Test
    public void execute_add_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        Task toBeAdded = helper.generateFullTask(1);
        TaskMan expectedTaskMan = new TaskMan();
        expectedTaskMan.addActivity(toBeAdded);

        assertCommandStateChange(helper.generateAddCommand(toBeAdded),
                expectedTaskMan
        );
    }

    @Test
    public void execute_addDuplicate_notAllowed() throws Exception {
        // setup expected
        TestDataHelper helper = new TestDataHelper();
        Task toBeAdded = helper.generateFullTask(1);
        TaskMan expectedTaskMan = new TaskMan();
        expectedTaskMan.addActivity(toBeAdded);

        // setup actual
        model.addActivity(toBeAdded);

        // execute command and verify result
        assertCommandStateChange(
                helper.generateAddCommand(toBeAdded),
                expectedTaskMan
        );

    }
}
