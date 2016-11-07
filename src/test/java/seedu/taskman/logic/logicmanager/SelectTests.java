package seedu.taskman.logic.logicmanager;

import org.junit.Test;
import seedu.taskman.commons.core.Messages;
import seedu.taskman.logic.commands.SelectCommand;
import seedu.taskman.model.TaskMan;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.Task;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class SelectTests extends LogicManagerTestBase {

    @Test
    public void execute_selectInvalidArgsFormat_errorMessageShown() throws Exception {
        // assertIncorrectIndexFormatBehaviorForCommand("select");
        assertCommandBehavior(
                SelectCommand.COMMAND_WORD,
                SelectCommand.MESSAGE_SELECT_INVALID_COMMAND_FORMAT,
                new TaskMan(model.getTaskMan())
        );
    }

    @Test
    public void execute_selectIndexNotFound_errorMessageShown() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        List<Task> threeTasks = helper.generateFullTaskList(3);

        TaskMan expectedTaskMan = helper.generateTaskMan(threeTasks);
        helper.addToModel(model, threeTasks);

        // assertIndexNotFoundBehaviorForCommand("select");
        assertCommandBehavior(
                SelectCommand.COMMAND_WORD + " s1000000",
                Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX,
                expectedTaskMan
        );
    }

    @Test
    public void execute_select_jumpsToCorrectDeadline() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        List<Task> threeTasks = helper.generateFullTaskList(3);

        TaskMan expectedTaskMan = helper.generateTaskMan(threeTasks);
        helper.addToModel(model, threeTasks);

        assertCommandStateChange(SelectCommand.COMMAND_WORD + " d2",
                expectedTaskMan
        );
        assertEquals(1, targetedJumpIndex);
        assertEquals(model.getActivityListForPanelType(targetedPanelType).get(1), new Activity(threeTasks.get(1)));
    }

}
