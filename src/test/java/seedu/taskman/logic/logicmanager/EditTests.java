package seedu.taskman.logic.logicmanager;

import org.junit.Test;
import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.logic.commands.EditCommand;
import seedu.taskman.model.TaskMan;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.Deadline;
import seedu.taskman.model.event.Task;
import seedu.taskman.model.tag.Tag;
import seedu.taskman.model.tag.UniqueTagList;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class EditTests extends LogicManagerTestBase {

    @Test
    public void execute_edit_invalidArgsFormat() throws Exception {
        // no args
        assertCommandNoStateChange(EditCommand.COMMAND_WORD);

        // non-existent flag
        assertCommandNoStateChange(String.format(EditCommand.COMMAND_WORD, " x/"));
    }

    @Test
    public void execute_edit_invalidTaskData() throws Exception {
        // bad deadline
        assertCommandNoStateChange(String.format(EditCommand.COMMAND_WORD, " Valid Title d/invalid Deadline"));

        // bad schedule
        assertCommandNoStateChange(String.format(EditCommand.COMMAND_WORD, " Valid Title s/invalid Schedule"));

        // bad title
        assertCommandNoStateChange(String.format(EditCommand.COMMAND_WORD, " []\\[;]"));
    }

    @Test
    public void execute_edit_successful() throws Exception {
        LogicManagerTestBase.TestDataHelper helper = new LogicManagerTestBase.TestDataHelper();

        int taskToEdit = 1;
        int numTasks = 3;
        List<Task> originalTasks = helper.generateFullTaskList(numTasks);
        Task originalTask = originalTasks.get(taskToEdit);
        helper.addToModel(model, originalTasks);

        // users start from 1 rather than 0
        int filteredIndex = model.getSortedDeadlineList().indexOf(new Activity(originalTask)) + 1;

        // edit deadline & tags for one task
        Deadline newDeadline = generateDifferentDeadline(new Activity(originalTask));
        Task editedTask = new Task(originalTask);
        editedTask.setDeadline(newDeadline);

        UniqueTagList tags = editedTask.getTags();
        tags.add(new Tag(helper.STRING_RANDOM));
        editedTask.setTags(tags);

        TaskMan expectedTaskMan = helper.generateTaskMan(originalTasks);
        expectedTaskMan.removeActivity(new Activity(originalTasks.get(taskToEdit)));
        expectedTaskMan.addActivity(editedTask);

        // set up actual before edit
        String inputCommand = helper.generateEditCommand(
                model,
                Activity.PanelType.DEADLINE,
                filteredIndex,
                null, // no change in title
                newDeadline,
                null, // no change in edit
                tags
        );

        assertCommandStateChange(inputCommand, expectedTaskMan);
    }

    private Deadline generateDifferentDeadline(Activity activity) throws IllegalValueException {
        int offset = 60 * 60;
        long rawDeadline = Instant.now().getEpochSecond() + offset;
        Optional<Deadline> currentDeadline = activity.getDeadline();
        if (currentDeadline.isPresent() && currentDeadline.get().epochSecond == rawDeadline) {
            rawDeadline += offset;
        }
        return new Deadline(rawDeadline);
    }

    // @Test
    public void execute_editToDuplicate_notAllowed() throws Exception {
        // set up expectations
        LogicManagerTestBase.TestDataHelper helper = new LogicManagerTestBase.TestDataHelper();
        int targetIndex = 1, numTasks = 3;
        List<Task> threeTasks = helper.generateFullTaskList(numTasks);
        TaskMan expectedTaskMan = helper.generateTaskMan(threeTasks);
        Task taskA = threeTasks.get(targetIndex);
        Task taskB = threeTasks.get(targetIndex + 1);

        // set up actual before edit
        helper.addToModel(model, threeTasks);

        /*
        // java.lang.AssertionError: Activity is neither an event nor a task.
        assertCommandStateChange(helper.generateEditCommand(model, targetIndex, taskB.getTitle(),
                taskA.getDeadline().orElse(null), taskA.getSchedule().orElse(null), taskA.getTags()),
                expectedTaskMan,
                expectedTaskMan.getActivityList());
                */
    }
}
