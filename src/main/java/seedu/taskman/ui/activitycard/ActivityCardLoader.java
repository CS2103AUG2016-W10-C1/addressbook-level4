package seedu.taskman.ui.activitycard;

import seedu.taskman.model.event.Activity;
import seedu.taskman.ui.UiPartLoader;

/**
 * Loader class to load appropriate Activity Card object
 */
public class ActivityCardLoader {

    /**
     * Constructs the appropriate ActivityCard given the panelType
     *
     * @param activity       to be represented
     * @param panelType      of the Panel
     * @param displayedIndex of the activity in the panel
     * @return the ActivityCard corresponding to the panelType
     */
    public static ActivityCard load(Activity activity, Activity.PanelType panelType, int displayedIndex) {
        ActivityCard card = new ActivityCard();

        switch (panelType) {
            case DEADLINE: {
                card = new DeadlineActivityCard();
                break;
            }
            case SCHEDULE: {
                card = new ScheduleActivityCard();
                break;
            }
            case FLOATING: {
                card = new FloatingActivityCard();
                break;
            }
            default: {
                break;
            }
        }
        card.activity = activity;
        card.displayedIndex = displayedIndex;
        return UiPartLoader.loadUiPart(card);
    }
}