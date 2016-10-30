package seedu.taskman.logic.commands;

import javafx.util.Pair;
import seedu.taskman.commons.core.EventsCenter;
import seedu.taskman.commons.core.Messages;
import seedu.taskman.commons.events.ui.JumpToListRequestEvent;
import seedu.taskman.model.event.Activity;
import seedu.taskman.commons.core.UnmodifiableObservableList;

import java.util.Optional;

import static seedu.taskman.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

/**
 * Selects a task identified using it's last displayed index from the task man.
 */
public class SelectCommand extends Command {

    private final int targetIndex;
    private final Activity.PanelType panelType;

    public static final String COMMAND_WORD = "select";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Selects the task identified by the index number used in the latest task listing.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " f2";

    public static final String MESSAGE_SELECT_EVENT_SUCCESS = "Selected Task: %1$s";

    public static Command prepareSelect(String arguments) {
        Optional<Pair<Activity.PanelType, Integer>> panelWithIndex = parsePanelTypeWithIndexOnly(arguments);
        if(!panelWithIndex.isPresent()){
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        }
        Pair<Activity.PanelType, Integer> pair = panelWithIndex.get();
        return new SelectCommand(pair.getKey(), pair.getValue());
    }

    private SelectCommand(Activity.PanelType panelType, int targetIndex) {
        super(false);
        this.targetIndex = targetIndex;
        this.panelType = panelType;
    }

    @Override
    public CommandResult execute() {

        assert model != null;

        UnmodifiableObservableList<Activity> shownList = model.getActivityListForPanelType(panelType);

        if (shownList.size() < targetIndex) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX, false);
        }

        EventsCenter.getInstance().post(new JumpToListRequestEvent(panelType, targetIndex - 1));
        Activity targetActivity =  shownList.get(targetIndex - 1);
        return new CommandResult(String.format(MESSAGE_SELECT_EVENT_SUCCESS, targetActivity), true);

    }

}
