package seedu.taskman.ui.activitycard;

import seedu.taskman.model.event.Activity;

/**
 * Activity Card for Floating Panel, currently has no additional functionality
 */
public class FloatingActivityCard extends ActivityCard {

    public FloatingActivityCard(){

    }

    @Override
    @javafx.fxml.FXML
    public void initialize() {
        super.initialize();
        id.setText(Activity.PanelType.FLOATING.getString()+displayedIndex);
    }
}
