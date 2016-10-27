package seedu.taskman.model;

import java.util.Set;

import seedu.taskman.commons.core.UnmodifiableObservableList;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.Event;
import seedu.taskman.model.event.UniqueActivityList;

/**
 * The API of the Model component.
 */
public interface Model {

    /** Clears existing backing model and replaces with the provided new data. */
    void resetData(ReadOnlyTaskMan newData);

    ReadOnlyTaskMan getTaskMan();

    void deleteActivity(Activity target) throws UniqueActivityList.ActivityNotFoundException;

    void addActivity(Event task) throws UniqueActivityList.DuplicateActivityException;

    void addActivity(Activity activity) throws UniqueActivityList.DuplicateActivityException;

    UnmodifiableObservableList<Activity> getActivityListForPanelType(Activity.PanelType type);
    
    /**
     * Returns the filtered list of activities with schedules as an {@code UnmodifiableObservableList<Activity>}
     */
    UnmodifiableObservableList<Activity> getSortedScheduleList();
    
    /**
     * Returns the filtered task list of tasks with deadlines as an {@code UnmodifiableObservableList<Activity>}
     */
    UnmodifiableObservableList<Activity> getSortedDeadlineList();
    
    /**
     * Returns the filtered list of tasks without deadlines as an {@code UnmodifiableObservableList<Activity>}
     */
    UnmodifiableObservableList<Activity> getSortedFloatingList();

    /**
     * Updates the filter of the filtered activity panels to show all activities
     */
    void updateFilteredPanelToShowAll(Activity.PanelType panel);
    
    /** Updates the filter of the filtered activity list to filter by the given mode, the given keywords and the given tag names*/
    void updateFilteredPanel(Activity.PanelType panel, Set<String> keywords, Set<String> tagNames);
}
