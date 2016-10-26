package seedu.taskman.ui.activitycard;

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
        String valueText = activity.getSchedule().isPresent()
                ? activity.getSchedule().get().toString()
                : "";
        value.setText(valueText);
        setColour();
    }

    private void setColour(){
        switch (activity.getType()) {
            case EVENT: {
                cardPane.getStyleClass().add(LIME_BG);
            }
        }
    }
}
