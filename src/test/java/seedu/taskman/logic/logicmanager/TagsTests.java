package seedu.taskman.logic.logicmanager;

import org.junit.Test;
import seedu.taskman.logic.commands.CommandResult;
import seedu.taskman.logic.commands.TagsCommand;
import seedu.taskman.model.TaskMan;
import seedu.taskman.model.event.Event;
import seedu.taskman.model.event.Task;
import seedu.taskman.model.tag.Tag;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by jiayee on 11/1/16.
 */
public class TagsTests extends LogicManagerTestBase {
    @Test
    public void execute_tags_emptyArgsFormat() throws Exception {
        // no args
        assertCommandNoStateChange("tags");

        logic.execute("clear");
        CommandResult result = logic.execute("tags");
        assertTrue(result.feedbackToUser.equals(TagsCommand.TAG_STRING_HEADER
                + TagsCommand.TAG_STRING_EMPTY_PLACEHOLDER));
        // this is similar to assertCommandBehaviour, will be bringing it back

        // TODO: Tidy up

        LogicManagerTestBase.TestDataHelper helper = new LogicManagerTestBase.TestDataHelper();
        Task toBeAdded = helper.generateFullTask(1);

        TaskMan expectedTaskMan = new TaskMan();
        expectedTaskMan.addActivity(toBeAdded); // expected

        List<Task> tasks = new ArrayList<>();
        tasks.add(toBeAdded);
        helper.addToModel(model, tasks); // actual

        result = logic.execute("tags");
        StringBuilder tags = new StringBuilder();
        for (Tag tag : toBeAdded.getTags()) {
            tags.append(tag);
            tags.append(" ");
        }
        assertTrue(result.feedbackToUser.equals(TagsCommand.TAG_STRING_HEADER
                + tags.toString().trim()));
    }
}
