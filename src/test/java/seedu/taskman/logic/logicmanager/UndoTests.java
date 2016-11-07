package seedu.taskman.logic.logicmanager;

import org.junit.Before;
import org.junit.Test;
import seedu.taskman.logic.commands.CommandResult;
import seedu.taskman.logic.commands.UndoCommand;
import seedu.taskman.model.TaskMan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

//@@author A0139019E
public class UndoTests extends LogicManagerTestBase {
    public TestDataHelper helper;

    @Before
    public void initHelper() {
        helper = new TestDataHelper();
    }

    @Test
    public void execute_undoNothingToUndo_failure() {
        assertTrue(!logic.execute(UndoCommand.COMMAND_WORD).succeeded);
    }

    @Test
    public void execute_undoWithoutIndexAfterSingleCommand_success() throws Exception {
        TaskMan before = new TaskMan(model.getTaskMan());
        assertExecuteCommandWithStateChange(helper.generateAddCommandWithOnlyTaskTitle("something"));
        assertExecuteCommandWithStateChange(UndoCommand.COMMAND_WORD);
        assertEquals("failed to undo", before, model.getTaskMan());
    }

    @Test
    public void execute_undoWithIndexAfterSingleCommand_success() throws Exception {
        TaskMan before = new TaskMan(model.getTaskMan());
        assertExecuteCommandWithStateChange(helper.generateAddCommandWithOnlyTaskTitle("something"));
        assertExecuteCommandWithStateChange(UndoCommand.COMMAND_WORD + " 1");
        assertEquals("failed to undo", before, model.getTaskMan());
    }

    @Test
    public void execute_undoThreeCommands_success() throws Exception {
        assertExecuteCommandWithStateChange(helper.generateAddCommandWithOnlyTaskTitle("something"));

        TaskMan before = new TaskMan(model.getTaskMan());
        assertExecuteCommandWithStateChange(helper.generateAddCommandWithOnlyTaskTitle("something else"));
        assertExecuteCommandWithStateChange(helper.generateAddCommandWithOnlyTaskTitle("something else 1"));
        assertExecuteCommandWithStateChange(helper.generateAddCommandWithOnlyTaskTitle("something else 2"));
        assertExecuteCommandWithStateChange(UndoCommand.COMMAND_WORD + " 3");
        assertEquals("failed to undo", before, model.getTaskMan());
    }

    private void assertExecuteCommandWithStateChange(String command) {
        TaskMan before = new TaskMan(model.getTaskMan());
        CommandResult result = logic.execute(command);
        assertTrue("Invalid command entered!", result.succeeded);
        assertNotEquals("Command does not change state", before, model.getTaskMan());
    }
}
