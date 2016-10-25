package seedu.taskman.logic.logicmanager;

import org.junit.Test;
import seedu.taskman.logic.commands.CommandResult;
import seedu.taskman.model.event.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HistoryTests extends LogicManagerTestBase {

    @Test
    public void execute_historyAfterSingleCommand_success() throws Exception {
        assertEquals(0, historyDeque.size());

        // do one successful command first
        TestDataHelper helper = new TestDataHelper();
        Task task = helper.generateTaskWithAllFields("task");
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);

        assertCommandStateChange(helper.generateDoCommand(task),
                helper.generateTaskMan(tasks)
        );

        assertCommandNoStateChange("history");
        assertEquals(1, historyDeque.size());
    }

    @Test
    public void execute_historyUnrecordedCommand_noChangeToHistory() throws Exception {
        // execute an unrecorded command after a recorded one
        // ensure the former does not change our history

        execute_historyAfterSingleCommand_success();
        assertEquals(1, historyDeque.size());

        CommandResult result = logic.execute("select 1");
        assertTrue(result.succeeded);

        assertCommandNoStateChange("history");
        assertEquals(1, historyDeque.size());
    }

}
