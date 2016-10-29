package seedu.taskman.ui.activitycard;

import seedu.taskman.model.event.Activity;
import javafx.fxml.FXML;

/**
 * Activity Card for Floating Panel, currently has no additional functionality
 */
public class FloatingActivityCard extends ActivityCard {

    @Override
    @FXML
    public void initialize() {
        super.initialize();
        id.setText(Activity.PanelType.FLOATING.getString() + displayedIndex);
    }
}
