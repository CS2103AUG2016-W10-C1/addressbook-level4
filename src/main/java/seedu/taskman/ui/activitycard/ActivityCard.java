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
    protected static final String BLUE_STYLE = "label-blue";

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

    public ActivityCard() {

    }

    @Override
    public void setNode(Node node) {
        cardPane = (GridPane) node;
    }

    @FXML
    public void initialize() {
        title.setText(activity.getTitle().toString());
        id.setText(displayedIndex + "");
        for (Tag tag : activity.getTags()) {
            tagsFlowPane.getChildren().add(new Label(tag.toString()));
        }
        value.setText("");
        setColour();
    }

    /**
     * Setting colours to title labels
     * Event -> blue
     * Task ->
     *  Overdue -> red
     *  Not overdue ->
     *      Complete -> green
     *      Incomplete -> amber
     */
    protected void setColour() {
        switch (activity.getType()) {
            case EVENT: {
                title.getStyleClass().add(BLUE_STYLE);
                break;
            }
            case TASK: {
                if (activity.getDeadline().isPresent()
                        && activity.getDeadline().get().hasPast()
                        && !activity.getStatus().get().completed) {
                    title.getStyleClass().add(RED_STYLE);
                    break;
                }

                if (activity.getStatus().get().completed) {
                    title.getStyleClass().add(GREEN_STYLE);
                } else {
                    title.getStyleClass().add(AMBER_STYLE);
                }
            }
        }
    }

    public GridPane getLayout() {
        return cardPane;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }
}
