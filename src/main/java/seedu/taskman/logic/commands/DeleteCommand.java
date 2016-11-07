package seedu.taskman.logic.commands;

import javafx.util.Pair;
import seedu.taskman.commons.core.Messages;
import seedu.taskman.commons.core.UnmodifiableObservableList;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.UniqueActivityList.ActivityNotFoundException;

import java.util.Optional;

import static seedu.taskman.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

/**
 * Deletes a task identified using it's last displayed index from the task man.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = "Delete an activity.\n"
            + "Parameters: INDEX\n"
            + "Example: " + COMMAND_WORD + " f1";

    public static final String MESSAGE_DELETE_EVENT_SUCCESS = "Deleted task: %1$s";
    public static final String MESSAGE_DELETE_INVALID_COMMAND_FORMAT = MESSAGE_INVALID_COMMAND_FORMAT
                    + "\n" + COMMAND_WORD + ": " + MESSAGE_USAGE;

    private final int targetIndex;
    private final Activity.PanelType panelType;

    private DeleteCommand(Activity.PanelType panelType, int targetIndex) {
        super(true);
        this.targetIndex = targetIndex;
        this.panelType = panelType;
    }

    public static Command prepareDelete(String arguments) {
        Optional<Pair<Activity.PanelType, Integer>> panelWithIndex = parsePanelTypeWithIndexOnly(arguments);
        if(!panelWithIndex.isPresent()){
            return new IncorrectCommand(MESSAGE_DELETE_INVALID_COMMAND_FORMAT);
        }
        Pair<Activity.PanelType, Integer> pair = panelWithIndex.get();
        return new DeleteCommand(pair.getKey(), pair.getValue());
    }

    @Override
    public CommandResult execute() {

        UnmodifiableObservableList<Activity> lastShownList = model.getActivityListForPanelType(panelType);

        if (lastShownList.size() < targetIndex) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX, false);
        }

        Activity activityToDelete = lastShownList.get(targetIndex - 1);

        try {
            model.deleteActivity(activityToDelete);
        } catch (ActivityNotFoundException pnfe) {
            assert false : "The target task cannot be missing.";
        }

        return new CommandResult(String.format(MESSAGE_DELETE_EVENT_SUCCESS, activityToDelete), true);
    }

}
