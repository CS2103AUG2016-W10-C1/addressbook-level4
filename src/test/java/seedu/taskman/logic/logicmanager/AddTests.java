package seedu.taskman.logic.logicmanager;

import org.junit.Test;
import seedu.taskman.logic.commands.AddCommand;
import seedu.taskman.model.TaskMan;
import seedu.taskman.model.event.Schedule;
import seedu.taskman.model.event.Task;
import seedu.taskman.model.event.Title;

public class AddTests extends LogicManagerTestBase {
    @Test
    public void execute_add_invalidArgsFormat() throws Exception {
        // no args
        // assertCommandNoStateChange("add");
        assertCommandBehavior(
                AddCommand.COMMAND_WORD,
                AddCommand.MESSAGE_ADD_INVALID_COMMAND_FORMAT,
                new TaskMan(model.getTaskMan())
        );

        // non-existent flag
        // assertCommandNoStateChange("add x/");
        assertCommandBehavior(
                AddCommand.COMMAND_WORD + " x/",
                AddCommand.MESSAGE_ADD_INVALID_COMMAND_FORMAT,
                new TaskMan(model.getTaskMan())
        );
    }

    @Test
    public void execute_add_invalidTaskData() throws Exception {
        // bad deadline
        // assertCommandNoStateChange("add valid title d/invalid deadline");

        // bad schedule
        // assertCommandNoStateChange("add valid title s/invalid schedule");
        assertCommandBehavior(
                AddCommand.COMMAND_WORD + " valid title s/invalid schedule",
                Schedule.MESSAGE_SCHEDULE_CONSTRAINTS,
                new TaskMan(model.getTaskMan())
        );

        // bad title
        // assertCommandNoStateChange("add []\\[;]");
        assertCommandBehavior(
                AddCommand.COMMAND_WORD + " []\\[;]",
                Title.MESSAGE_TITLE_CONSTRAINTS,
                new TaskMan(model.getTaskMan())
        );
    }

    @Test
    public void execute_add_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        Task toBeAdded = helper.generateFullTask(1);
        TaskMan expectedTaskMan = new TaskMan();
        expectedTaskMan.addActivity(toBeAdded);

        /*
        assertCommandStateChange(helper.generateAddCommand(toBeAdded),
                expectedTaskMan
        );
        */

        assertCommandBehavior(
                helper.generateAddCommand(toBeAdded),
                String.format(AddCommand.MESSAGE_SUCCESS, toBeAdded),
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
        /*
        assertCommandStateChange(
                helper.generateAddCommand(toBeAdded),
                expectedTaskMan
        );
        */

        assertCommandBehavior(
                helper.generateAddCommand(toBeAdded),
                AddCommand.MESSAGE_DUPLICATE_EVENT,
                expectedTaskMan
        );
    }
}
