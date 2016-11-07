package seedu.taskman.ui.activitycard;

import seedu.taskman.model.event.Activity;
import javafx.fxml.FXML;

//@@author A0121299A
/**
 * Activity Card for Floating Panel
 */
public class FloatingActivityCard extends ActivityCard {

    @Override
    @FXML
    public void initialize() {
        super.initialize();
        id.setText(Activity.PanelType.FLOATING.getString() + displayedIndex);
    }
}
