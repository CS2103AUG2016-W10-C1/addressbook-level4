package seedu.taskman.logic.logicmanager;

import org.junit.Test;
import seedu.taskman.logic.commands.ExitCommand;

import static org.junit.Assert.assertTrue;

public class MiscCommandTests extends LogicManagerTestBase {

    @Test
    public void execute_invalid() throws Exception {
        String invalidCommand = "       ";
        assertCommandNoStateChange(invalidCommand);
    }

    @Test
    public void execute_unknownCommandWord() throws Exception {
        String unknownCommand = "abcdefghijk";
        assertCommandNoStateChange(unknownCommand);
    }

    @Test
    public void execute_help() throws Exception {
        assertCommandNoStateChange("help");
        assertTrue(helpShown);
    }

    @Test
    public void execute_exit() throws Exception {
        // assertCommandNoStateChange("exit");
        assertCommandBehavior(
                ExitCommand.COMMAND_WORD,
                ExitCommand.MESSAGE_EXIT_ACKNOWLEDGEMENT
        );
    }


}
