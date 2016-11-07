package seedu.taskman.ui.activitycard;

import seedu.taskman.model.event.Activity;
import javafx.fxml.FXML;


//@@author A0121299A
/**
 * Activity Card for Deadline Panel with value field displaying deadline
 */
public class DeadlineActivityCard extends ActivityCard {

    @Override
    @FXML
    public void initialize() {
        super.initialize();
        id.setText(Activity.PanelType.DEADLINE.getString() + displayedIndex);
        value.setText(activity.getDeadline().get().toString());
        setColours();
    }
}
