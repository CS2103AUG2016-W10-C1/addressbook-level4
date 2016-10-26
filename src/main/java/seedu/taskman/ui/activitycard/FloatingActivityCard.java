package seedu.taskman.ui.activitycard;

/**
 * Created by YiMin on 27/10/2016.
 */
public class FloatingActivityCard extends ActivityCard {

    public FloatingActivityCard(){

    }

    @Override
    @javafx.fxml.FXML
    public void initialize() {
        super.initialize();
        value.setText("");
        setColour();
    }

    private void setColour(){
        if (activity.getStatus().get().completed) {
            title.getStyleClass().add(GREEN_STYLE);
        }
        else {
            title.getStyleClass().add(AMBER_STYLE);
        }
    }
}
