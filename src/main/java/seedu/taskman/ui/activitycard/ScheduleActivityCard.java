package seedu.taskman.ui.activitycard;

import seedu.taskman.model.event.Activity;

/**
 * Created by YiMin on 27/10/2016.
 */
public class ScheduleActivityCard extends ActivityCard {

    public ScheduleActivityCard(){

    }

    @Override
    @javafx.fxml.FXML
    public void initialize() {
        super.initialize();
        id.setText(Activity.PanelType.SCHEDULE.getString()+displayedIndex);
        String valueText = activity.getSchedule().isPresent()
                ? activity.getSchedule().get().toString()
                : "";
        value.setText(valueText);
    }
}
