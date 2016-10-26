package seedu.taskman.ui.activitycard;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.tag.Tag;
import seedu.taskman.ui.*;

/**
 * Created by YiMin on 26/10/2016.
 */
public class ActivityCard extends UiPart {

    private static final String FXML = "ActivityCard.fxml";

    //todo: shift to more appropriate place
    protected static final String AMBER_STYLE = "label-amber";
    protected static final String GREEN_STYLE = "label-green";
    protected static final String RED_STYLE = "label-red";
    protected static final String LIME_BG = "bg-lime";

    @javafx.fxml.FXML
    protected GridPane cardPane;
    @FXML
    protected Label title;
    @FXML
    protected Label id;
    @FXML
    protected Label value;
    @FXML
    protected FlowPane tagsFlowPane;

    protected Activity activity;
    protected int displayedIndex;

    public ActivityCard(){

    }

    /**
     * Constructs the appropriate ActivityCard given the panelType
     * @param activity to be represented
     * @param panelType of the Panel
     * @param displayedIndex of the activity in the panel
     * @return the ActivityCard corresponding to the panelType
     */
    public static ActivityCard load(Activity activity, Activity.PanelType panelType, int displayedIndex){
        ActivityCard card = new ActivityCard();

        switch (panelType) {
            case DEADLINE: {
                card = new DeadlineActivityCard();
                break;
            }
            case SCHEDULE: {
                card = new ScheduleActivityCard();
                break;
            }
            case FLOATING: {
                card = new FloatingActivityCard();
                break;
            }
        }
        card.activity = activity;
        card.displayedIndex = displayedIndex;
        return UiPartLoader.loadUiPart(card);
    }

    @Override
    public void setNode(Node node) {
        cardPane = (GridPane) node;
    }

    @FXML
    public void initialize() {
        title.setText(activity.getTitle().toString());
        id.setText(displayedIndex + "");
        for (Tag tag: activity.getTags()) {
            tagsFlowPane.getChildren().add(new Label(tag.toString()));
        }
    }

    public GridPane getLayout(){
        return cardPane;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }
}
