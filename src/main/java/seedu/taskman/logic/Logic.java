package seedu.taskman.logic;

import javafx.collections.ObservableList;
import seedu.taskman.logic.commands.CommandResult;
import seedu.taskman.model.event.Activity;

/**
 * API of the Logic component
 */
public interface Logic {
    /**
     * Executes the command and returns the result.
     *
     * @param commandText The command as entered by the user.
     * @return the result of the command execution.
     */
    CommandResult execute(String commandText);

    //TODO Remove
    /**
     * Returns the filtered list of activities
     */
    //ObservableList<Activity> getFilteredActivityList();
    
    /**
     * Returns the filtered list of activities with schedules
     */
    ObservableList<Activity> getSortedScheduleList();
    
    /**
     * Returns the filtered list of tasks with deadlines
     */
    ObservableList<Activity> getSortedDeadlineList();
    
    /**
     * Returns the filtered list of tasks without deadlines
     */
    ObservableList<Activity> getSortedFloatingList();

}
