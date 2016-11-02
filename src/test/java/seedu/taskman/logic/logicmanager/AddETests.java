package seedu.taskman.logic.logicmanager;

import org.junit.Test;
import seedu.taskman.model.TaskMan;
import seedu.taskman.model.event.Event;

/**
 * Created by jiayee on 11/1/16.
 */
public class AddETests extends LogicManagerTestBase {
    @Test
    public void execute_adde_invalidArgsFormat() throws Exception {
        // no args
        assertCommandNoStateChange("adde");

        // non-existent flag
        assertCommandNoStateChange("adde x/");

        // non-existent flag
        assertCommandNoStateChange("adde d/");
    }

    @Test
    public void execute_adde_invalidTaskData() throws Exception {

        // bad schedule
        assertCommandNoStateChange("adde Valid Title s/invalid Schedule");

        // bad title
        assertCommandNoStateChange("adde []\\[;]");
    }

    @Test
    public void execute_adde_successful() throws Exception {
        // setup expectations
        LogicManagerTestBase.TestDataHelper helper = new LogicManagerTestBase.TestDataHelper();
        Event toBeAdded = helper.generateFullEvent(1);
        TaskMan expectedTaskMan = new TaskMan();
        expectedTaskMan.addActivity(toBeAdded);

        assertCommandStateChange(helper.generateAddECommand(toBeAdded),
                expectedTaskMan
        );
    }

    @Test
    public void execute_addeDuplicate_notAllowed() throws Exception {
        // setup expected
        LogicManagerTestBase.TestDataHelper helper = new LogicManagerTestBase.TestDataHelper();
        Event toBeAdded = helper.generateFullEvent(1);
        TaskMan expectedTaskMan = new TaskMan();
        expectedTaskMan.addActivity(toBeAdded);

        // setup actual
        model.addActivity(toBeAdded);

        // execute command and verify result
        assertCommandStateChange(
                helper.generateAddECommand(toBeAdded),
                expectedTaskMan
        );

    }
}
