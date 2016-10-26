package seedu.taskman.ui.activitycard;

/**
 * Created by YiMin on 26/10/2016.
 */
public class DeadlineActivityCard extends ActivityCard {

    public DeadlineActivityCard(){

    }

    @Override
    @javafx.fxml.FXML
    public void initialize() {
        super.initialize();
        value.setText(activity.getDeadline().get().toString());
        setColour();
    }

    private void setColour(){
        //TODO: implement method in activity to check for overdue and do styling here
        if (activity.getStatus().get().completed) {
            title.getStyleClass().add(GREEN_STYLE);
        }
        else {
            title.getStyleClass().add(AMBER_STYLE);
        }
    }
}
