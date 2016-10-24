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

    /**
     * Returns the filtered list of activities
     */
    ObservableList<Activity> getFilteredActivityList();
    
    /**
     * Returns the filtered list of activities with schedules
     */
    ObservableList<Activity> getFilteredScheduleList();
    
    /**
     * Returns the filtered list of tasks with deadlines
     */
    ObservableList<Activity> getFilteredDeadlineList();
    
    /**
     * Returns the filtered list of tasks without deadlines
     */
    ObservableList<Activity> getFilteredFloatingList();

}
