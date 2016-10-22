package seedu.taskman.logic.commands;

import seedu.taskman.commons.core.Messages;
import seedu.taskman.commons.core.UnmodifiableObservableList;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.UniqueActivityList.ActivityNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * Deletes a task identified using it's last displayed index from the task man.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the task or event identified by the index number used in the last activity listing.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_EVENT_SUCCESS = "Deleted Task: %1$s";
    public static final String MESSAGE_DELETE_ALL_EVENT_SUCCESS = "Deleted Task(s):";
    public static final String MESSAGE_DELETE_ALL_EVENT_SUCCESS_SEPARATOR = "\n\t";

    public final int targetIndex;

    public DeleteCommand(int targetIndex) {
        this.targetIndex = targetIndex;
    }


    @Override
    public CommandResult execute() {

        UnmodifiableObservableList<Activity> lastShownList = model.getFilteredActivityList();

        if (targetIndex == 0) {
            StringBuilder builder = new StringBuilder(MESSAGE_DELETE_ALL_EVENT_SUCCESS);
            try {
                List<Activity> activityList = new ArrayList<>();
                for (int i = 0; i < lastShownList.size(); i++) {
                    Activity activityToDelete = lastShownList.get(i);
                    activityList.add(activityToDelete);
                    builder.append(MESSAGE_DELETE_ALL_EVENT_SUCCESS_SEPARATOR);
                    builder.append(activityToDelete.getTitle().title);
                }
                model.deleteActivities(activityList);
            } catch (ActivityNotFoundException pnfe) {
                assert false: "The target task cannot be missing";
            }

            return new CommandResult(builder.toString());
        }

        if (lastShownList.size() < targetIndex) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
        }

        Activity activityToDelete = lastShownList.get(targetIndex - 1);

        try {
            model.deleteActivity(activityToDelete);
        } catch (ActivityNotFoundException pnfe) {
            assert false: "The target task cannot be missing";
        }

        return new CommandResult(String.format(MESSAGE_DELETE_EVENT_SUCCESS, activityToDelete));
    }

}
