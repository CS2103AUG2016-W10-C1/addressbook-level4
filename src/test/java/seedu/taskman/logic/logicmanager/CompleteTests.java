package seedu.taskman.logic.logicmanager;

import org.junit.Test;
import seedu.taskman.model.TaskMan;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.Status;
import seedu.taskman.model.event.Task;

import java.util.ArrayList;
import java.util.List;

public class CompleteTests extends LogicManagerTestBase {

    @Test
    public void execute_completeInvalidArgsFormat_errorMessageShown() throws Exception {
        assertIncorrectIndexFormatBehaviorForCommand("complete");
    }

    @Test
    public void execute_completeIndexNotFound_errorMessageShown() throws Exception {
        assertIndexNotFoundBehaviorForCommand("complete");
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

        assertCommandStateChange("complete " + Activity.PanelType.SCHEDULE + completeIndex,
                expectedTaskMan, expectedTaskMan.getActivityList());
    }

}
