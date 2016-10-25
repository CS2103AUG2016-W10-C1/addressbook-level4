package guitests;

import guitests.guihandles.HelpWindowHandle;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

// todo: should fix
@Ignore
public class HelpWindowTest extends TaskManGuiTest {

    @Test
    public void openHelpWindow() {

        deadlineListPanel.clickOnTableView();

        assertHelpWindowOpen(mainMenu.openHelpWindowUsingAccelerator());

        assertHelpWindowOpen(mainMenu.openHelpWindowUsingMenu());

        assertHelpWindowOpen(commandBox.runHelpCommand());

    }

    private void assertHelpWindowOpen(HelpWindowHandle helpWindowHandle) {
        assertTrue(helpWindowHandle.isWindowOpen());
        helpWindowHandle.closeWindow();
    }
}
