package seedu.taskman.model;

import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.UniqueActivityList;

import java.util.List;

/**
 * Unmodifiable view of an task man
 */
public interface ReadOnlyTaskMan {

    UniqueActivityList getUniqueActivityList();

    /**
     * Returns an unmodifiable view of tasks list
     */
    List<Activity> getActivityList();

}
