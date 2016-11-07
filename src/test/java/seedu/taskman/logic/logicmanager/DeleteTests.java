package seedu.taskman.logic.logicmanager;

import org.junit.Test;
import seedu.taskman.commons.core.Messages;
import seedu.taskman.logic.commands.DeleteCommand;
import seedu.taskman.model.TaskMan;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.Task;

import java.util.List;

public class DeleteTests extends LogicManagerTestBase {

    @Test
    public void execute_deleteInvalidArgsFormat_errorMessageShown() throws Exception {
        // assertIncorrectIndexFormatBehaviorForCommand("delete");
        assertCommandBehavior(
                DeleteCommand.COMMAND_WORD,
                DeleteCommand.MESSAGE_DELETE_INVALID_COMMAND_FORMAT,
                new TaskMan(model.getTaskMan())
        );
    }

    @Test
    public void execute_deleteIndexNotFound_errorMessageShown() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        List<Task> threeTasks = helper.generateFullTaskList(3);

        TaskMan expectedTaskMan = helper.generateTaskMan(threeTasks);
        helper.addToModel(model, threeTasks);

        // assertIndexNotFoundBehaviorForCommand("delete");
        assertCommandBehavior(
                DeleteCommand.COMMAND_WORD + " f1000000",
                Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX,
                expectedTaskMan
        );
    }

    @Test
    public void execute_delete_removesCorrectTask() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        List<Task> threeTasks = helper.generateFullTaskList(3);

        int deleteIndex = 1;
        TaskMan expectedTaskMan = helper.generateTaskMan(threeTasks);
        Activity toDelete = new Activity(threeTasks.get(deleteIndex-1));
        expectedTaskMan.removeActivity(toDelete);
        helper.addToModel(model, threeTasks);

        /*
        assertCommandStateChange("delete d2",
                expectedTaskMan
        );
        */

        assertCommandBehavior(
                DeleteCommand.COMMAND_WORD + " " + Activity.PanelType.SCHEDULE + deleteIndex,
                String.format(DeleteCommand.MESSAGE_DELETE_EVENT_SUCCESS, toDelete),
                expectedTaskMan
        );
    }
}
