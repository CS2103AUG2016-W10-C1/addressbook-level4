package seedu.taskman.commons.events.ui;

import seedu.taskman.commons.events.BaseEvent;
import seedu.taskman.model.event.Activity;

/**
 * Indicates a request to jump to the list of tasks
 */
public class JumpToListRequestEvent extends BaseEvent {

    public final int targetIndex;
    public final Activity.PanelType panelType;

    public JumpToListRequestEvent(Activity.PanelType panelType, int targetIndex) {
        this.panelType = panelType;
        this.targetIndex = targetIndex;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
