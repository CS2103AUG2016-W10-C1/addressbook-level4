# A0136070Runused
###### /java/seedu/taskman/logic/logicmanager/EditTests.java
``` java
package seedu.taskman.logic.logicmanager;

import org.junit.Test;
import seedu.taskman.logic.commands.EditCommand;
import seedu.taskman.model.TaskMan;
import seedu.taskman.model.event.Deadline;
import seedu.taskman.model.event.Task;
import seedu.taskman.model.tag.Tag;
import seedu.taskman.model.tag.UniqueTagList;

import java.time.Instant;
import java.util.List;

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

    // @Test
    public void execute_edit_successful() throws Exception {
        // set up expectations
        LogicManagerTestBase.TestDataHelper helper = new LogicManagerTestBase.TestDataHelper();
        int targetIndex = 1, numTasks = 3;
        List<Task> threeTasks = helper.generateTaskList(numTasks);
        TaskMan expectedTaskMan = helper.generateTaskMan(threeTasks);
        Task task = threeTasks.get(targetIndex);

        // edit deadline
        Deadline deadline;
        if (task.getDeadline().isPresent()) {
            deadline = new Deadline(task.getDeadline().get().epochSecond + helper.SECONDS_DAY);
        } else {
            deadline = new Deadline(Instant.now().getEpochSecond() + helper.SECONDS_DAY);
        }
        task.setDeadline(deadline);

        // edit tags
        UniqueTagList tags = task.getTags();
        tags.add(new Tag(helper.STRING_RANDOM));
        task.setTags(tags);

        // expectedTaskMan.removeActivity(threeTasks.get(targetIndex));
        expectedTaskMan.addActivity(task);

        // set up actual before edit
        helper.addToModel(model, threeTasks);

        // java.lang.AssertionError: Activity is neither an event nor a task.
        assertCommandStateChange(helper.generateEditCommand(model, targetIndex, task.getTitle(),
                deadline, task.getSchedule().orElse(null),
                task.getFrequency().orElse(null), tags),
                expectedTaskMan,
                expectedTaskMan.getActivityList());
    }

    // @Test
    public void execute_editToDuplicate_notAllowed() throws Exception {
        // set up expectations
        LogicManagerTestBase.TestDataHelper helper = new LogicManagerTestBase.TestDataHelper();
        int targetIndex = 1, numTasks = 3;
        List<Task> threeTasks = helper.generateTaskList(numTasks);
        TaskMan expectedTaskMan = helper.generateTaskMan(threeTasks);
        Task taskA = threeTasks.get(targetIndex);
        Task taskB = threeTasks.get(targetIndex + 1);

        // set up actual before edit
        helper.addToModel(model, threeTasks);

        // java.lang.AssertionError: Activity is neither an event nor a task.
        assertCommandStateChange(helper.generateEditCommand(model, targetIndex, taskB.getTitle(),
                taskA.getDeadline().orElse(null), taskA.getSchedule().orElse(null),
                taskA.getFrequency().orElse(null), taskA.getTags()),
                expectedTaskMan,
                expectedTaskMan.getActivityList());
    }
}
```
###### /java/seedu/taskman/logic/logicmanager/LogicManagerTestBase.java
``` java
        String generateEditCommand(Model model, int targetIndex, Title title, Deadline deadline, Schedule schedule,
                                   Frequency frequency, UniqueTagList tags) {
            Task task = model.getFilteredActivityList().get(targetIndex);
            StringBuilder command = new StringBuilder();

            command.append(EditCommand.COMMAND_WORD);
            command.append(String.format(" %d ", targetIndex));
            command.append(title.toString());

            Instant instant = Instant.ofEpochSecond(deadline.epochSecond);
            command.append(" d/").
                    append(instant.toString());

            if (task.getFrequency().isPresent()) {
                throw new AssertionError("Frequency is not supported yet");
            }

            if (task.getSchedule().isPresent()) {
                String start = DateTimeParser.epochSecondToShortDateTime(schedule.startEpochSecond);
                String end = DateTimeParser.epochSecondToShortDateTime(schedule.endEpochSecond);
                command.append(" s/").
                        append(start).
                        append(" to ").
                        append(end);
            }

            for (Tag t : tags) {
                command.append(" t/").append(t.tagName);
            }

            return command.toString();
        }
```
