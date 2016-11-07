package seedu.taskman.logic.logicmanager;

import org.junit.Test;
import seedu.taskman.logic.commands.CommandResult;
import seedu.taskman.logic.commands.HelpCommand;
import seedu.taskman.logic.commands.ListCommand;
import seedu.taskman.model.event.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

//@@author A0139019E
public class HistoryTests extends LogicManagerTestBase {

    @Test
    public void execute_historyAfterSingleCommand_success() throws Exception {
        int expectedHistorySize = 0;
        assertEquals(expectedHistorySize, historyDeque.size());

        // do one successful command first
        TestDataHelper helper = new TestDataHelper();
        Task task = helper.generateTaskWithAllFields("task");
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);

        assertCommandStateChange(
                helper.generateAddCommand(task),
                helper.generateTaskMan(tasks)
        );

        expectedHistorySize++;

        assertEquals(expectedHistorySize, historyDeque.size());
    }
    
    @Test
    public void execute_historyUnrecordedCommand_noChangeToHistory() throws Exception {
        // execute unrecorded commands after a recorded one
        // ensure the unrecorded commands does not change our history

        int expectedHistorySize = 1;

        execute_historyAfterSingleCommand_success();
        assertEquals(expectedHistorySize, historyDeque.size());

        CommandResult result = logic.execute(ListCommand.COMMAND_WORD);
        assertTrue(result.succeeded);

        result = logic.execute(HelpCommand.COMMAND_WORD);
        assertTrue(result.succeeded);

        assertEquals(expectedHistorySize, historyDeque.size());
    }

}
