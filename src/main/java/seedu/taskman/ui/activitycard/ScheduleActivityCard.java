package seedu.taskman.ui.activitycard;

import seedu.taskman.model.event.Activity;
import javafx.fxml.FXML;


//@@author A0121299A
/**
 * Activity Card for Schedule Panel with value field displaying schedule
 */
public class ScheduleActivityCard extends ActivityCard {

    @Override
    @FXML
    public void initialize() {
        super.initialize();
        id.setText(Activity.PanelType.SCHEDULE.getString() + displayedIndex);
        String valueText = activity.getSchedule().isPresent()
                ? activity.getSchedule().get().toString()
                : "";
        value.setText(valueText);
    }
}
