package seedu.taskman.logic.logicmanager;

import seedu.taskman.model.TaskMan;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.Task;

import java.util.List;

public class CompleteTests extends LogicManagerTestBase {

    //@Test
    public void execute_completeInvalidArgsFormat_errorMessageShown() throws Exception {
        assertIncorrectIndexFormatBehaviorForCommand("complete");
    }

    //@Test
    public void execute_completeIndexNotFound_errorMessageShown() throws Exception {
        assertIndexNotFoundBehaviorForCommand("complete");
    }

    //@Test
    public void execute_complete_completesCorrectTask() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        List<Task> threeTasks = helper.generateTaskList(3);

        TaskMan expectedTaskMan = helper.generateTaskMan(threeTasks);
        // Wrap Task in Activity to complete
        expectedTaskMan.completeActivity(new Activity(threeTasks.get(1)));
        helper.addToModel(model, threeTasks);

        // Fails sometimes when generated Activity is not a Task but Event
        assertCommandStateChange("complete 2",
                expectedTaskMan,
                expectedTaskMan.getActivityList());
    }

}
