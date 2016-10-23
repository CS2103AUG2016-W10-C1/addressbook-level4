package seedu.taskman.logic.logicmanager;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MiscCommandTests extends LogicManagerTestBase {

    @Test
    public void execute_invalid() throws Exception {
        String invalidCommand = "       ";
        assertCommandNoStateChange(invalidCommand);
    }

    @Test
    public void execute_unknownCommandWord() throws Exception {
        String unknownCommand = "uicfhmowqewca";
        assertCommandNoStateChange(unknownCommand);
    }

    //@Test
    public void execute_help() throws Exception {
        assertCommandNoStateChange("help");
        assertTrue(helpShown);
    }

    //@Test
    public void execute_exit() throws Exception {
        assertCommandNoStateChange("exit");
    }


}
