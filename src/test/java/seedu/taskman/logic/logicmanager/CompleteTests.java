package seedu.taskman.logic.logicmanager;

import org.junit.Test;
import seedu.taskman.commons.core.Messages;
import seedu.taskman.logic.commands.CompleteCommand;
import seedu.taskman.model.TaskMan;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.Status;
import seedu.taskman.model.event.Task;

import java.util.ArrayList;
import java.util.List;

public class CompleteTests extends LogicManagerTestBase {

    @Test
    public void execute_completeInvalidArgsFormat_errorMessageShown() throws Exception {
        // assertIncorrectIndexFormatBehaviorForCommand("complete");
        assertCommandBehavior(
                CompleteCommand.COMMAND_WORD,
                CompleteCommand.MESSAGE_COMPLETE_INVALID_COMMAND_FORMAT,
                new TaskMan(model.getTaskMan())
        );
    }

    @Test
    public void execute_completeIndexNotFound_errorMessageShown() throws Exception {
        // assertIndexNotFoundBehaviorForCommand("complete");
        assertCommandBehavior(
                CompleteCommand.COMMAND_WORD + " s1000000",
                Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX,
                new TaskMan(model.getTaskMan())
        );
    }

    @Test
    public void execute_complete_completesCorrectTask() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        List<Task> threeTasks = helper.generateFullTaskList(3);

        helper.addToModel(model, threeTasks);

        // need to reposition task due to how "complete" works. (remove & add new task)
        int completeIndex = 2;
        List<Task> expectedList = new ArrayList<>(threeTasks);
        Task toComplete = expectedList.remove(completeIndex - 1);
        toComplete.setStatus(new Status(Status.COMPLETE));
        expectedList.add(toComplete);
        TaskMan expectedTaskMan = helper.generateTaskMan(expectedList);

        /*
        assertCommandStateChange(CompleteCommand.COMMAND_WORD + " " + Activity.PanelType.SCHEDULE + completeIndex,
                expectedTaskMan);
        */

        assertCommandBehavior(
                CompleteCommand.COMMAND_WORD + " " + Activity.PanelType.SCHEDULE + completeIndex,
                String.format(CompleteCommand.MESSAGE_SUCCESS, toComplete.getTitle().title),
                expectedTaskMan
        );

        assertCommandBehavior(
                CompleteCommand.COMMAND_WORD + " " + Activity.PanelType.SCHEDULE + completeIndex,
                String.format(CompleteCommand.MESSAGE_SUCCESS, toComplete.getTitle().title),
                expectedTaskMan
        );
    }

}
