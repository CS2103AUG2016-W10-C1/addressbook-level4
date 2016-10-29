package seedu.taskman.ui.activitycard;

import seedu.taskman.model.event.Activity;
import javafx.fxml.FXML;

/**
 * Created by YiMin on 26/10/2016.
 */
public class DeadlineActivityCard extends ActivityCard {

    @Override
    @FXML
    public void initialize() {
        super.initialize();
        id.setText(Activity.PanelType.DEADLINE.getString() + displayedIndex);
        value.setText(activity.getDeadline().get().toString());
        setColour();
    }
}
