package seedu.taskman.commons.events.ui;

import seedu.taskman.commons.events.BaseEvent;
import seedu.taskman.model.event.Activity;

/**
 * Represents a selection change in the Task List Panel
 */
public class TaskPanelSelectionChangedEvent extends BaseEvent {


    private final Activity newSelection;
    private final Activity.PanelType panelType;

    public TaskPanelSelectionChangedEvent(Activity newSelection, Activity.PanelType panelType) {
        this.newSelection = newSelection;
        this.panelType = panelType;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public Activity getNewSelection() {
        return newSelection;
    }

    public Activity.PanelType getPanelType(){
        return panelType;
    }
}
