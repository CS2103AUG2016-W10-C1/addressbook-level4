package seedu.taskman.ui.activitycard;

import seedu.taskman.model.event.Activity;
import javafx.fxml.FXML;

/**
 * Created by YiMin on 27/10/2016.
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
