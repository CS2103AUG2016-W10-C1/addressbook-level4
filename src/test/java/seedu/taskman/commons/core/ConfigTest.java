package seedu.taskman.commons.core;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import seedu.taskman.commons.core.config.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ConfigTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void toString_defaultObject_stringReturned() {
        String defaultConfigAsString = "App title : TaskMan\n" +
                "Current log level : INFO\n" +
                "Preference file Location : preferences.json\n" +
                "Local data file location : data/taskMan.xml\n" +
                "TaskMan name : MyTaskMan";

        assertEquals(defaultConfigAsString, Config.getInstance().toString());
    }

    @Test
    public void equalsMethod(){
        Config defaultConfig = Config.getInstance();
        assertFalse(defaultConfig.equals(null));
        assertTrue(defaultConfig.equals(defaultConfig));
    }


}
