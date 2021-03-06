package guitests;

import guitests.guihandles.HelpWindowHandle;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class HelpWindowTest extends TaskManGuiTest {

    @Test
    public void openHelpWindow() {

        deadlineListPanel.clickOnListView();

        assertHelpWindowOpen(mainMenu.openHelpWindowUsingShortcut());

        assertHelpWindowOpen(commandBox.runHelpCommand());

    }

    private void assertHelpWindowOpen(HelpWindowHandle helpWindowHandle) {
        assertTrue(helpWindowHandle.isWindowOpen());
        helpWindowHandle.closeWindow();
    }
}
