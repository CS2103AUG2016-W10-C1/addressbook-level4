package seedu.taskman.logic.logicmanager;

import org.junit.Test;
import seedu.taskman.logic.commands.AddECommand;
import seedu.taskman.model.TaskMan;
import seedu.taskman.model.event.Event;
import seedu.taskman.model.event.Schedule;
import seedu.taskman.model.event.Title;

/**
 * Created by jiayee on 11/1/16.
 */
public class AddETests extends LogicManagerTestBase {
    @Test
    public void execute_adde_invalidArgsFormat() throws Exception {
        // no args
        // assertCommandNoStateChange("adde");
        assertCommandBehavior(AddECommand.COMMAND_WORD, AddECommand.MESSAGE_ADDE_INVALID_COMMAND_FORMAT);

        // non-existent flag
        // assertCommandNoStateChange("adde x/");
        assertCommandBehavior(AddECommand.COMMAND_WORD + " x/", AddECommand.MESSAGE_ADDE_INVALID_COMMAND_FORMAT);

        // non-existent flag
        // assertCommandNoStateChange("adde d/");
        assertCommandBehavior(AddECommand.COMMAND_WORD + " d/", AddECommand.MESSAGE_ADDE_INVALID_COMMAND_FORMAT);
    }

    @Test
    public void execute_adde_invalidTaskData() throws Exception {
        // bad schedule
        // assertCommandNoStateChange("adde valid title s/invalid schedule");
        assertCommandBehavior(AddECommand.COMMAND_WORD + " valid title s/invalid schedule", Schedule.MESSAGE_SCHEDULE_CONSTRAINTS);

        // bad title
        // assertCommandNoStateChange("adde []\\[;]");
        assertCommandBehavior(AddECommand.COMMAND_WORD + " []\\[;]", Title.MESSAGE_TITLE_CONSTRAINTS);
    }

    @Test
    public void execute_adde_successful() throws Exception {
        // setup expectations
        LogicManagerTestBase.TestDataHelper helper = new LogicManagerTestBase.TestDataHelper();
        Event toBeAdded = helper.generateFullEvent(1);
        TaskMan expectedTaskMan = new TaskMan();
        expectedTaskMan.addActivity(toBeAdded);

        /*
        assertCommandStateChange(helper.generateAddECommand(toBeAdded),
                expectedTaskMan
        );
        */

        assertCommandBehavior(
                helper.generateAddECommand(toBeAdded),
                String.format(AddECommand.MESSAGE_SUCCESS, toBeAdded),
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
        /*
        assertCommandStateChange(
                helper.generateAddECommand(toBeAdded),
                expectedTaskMan
        );
        */

        assertCommandBehavior(
                helper.generateAddECommand(toBeAdded),
                AddECommand.MESSAGE_DUPLICATE_EVENT,
                expectedTaskMan
        );
    }
}
