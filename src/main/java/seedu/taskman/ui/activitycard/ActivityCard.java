package seedu.taskman.ui.activitycard;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import seedu.taskman.Constants;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.tag.Tag;
import seedu.taskman.ui.UiPart;

//@@author A0121299A
/**
 * Small card panel which displays a single Activity object.
 */
public class ActivityCard extends UiPart {

    protected static final String FXML = "ActivityCard.fxml";

    @FXML
    protected GridPane cardPane;
    @FXML
    protected Label title;
    @FXML
    protected Label id;
    @FXML
    protected Label value;
    @FXML
    protected ImageView icon;
    @FXML
    protected FlowPane tagsFlowPane;

    protected Activity activity;
    protected int displayedIndex;

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
        setColours();
    }

    /**
     * Setting colours and styling to the card
     */
    protected void setColours() {
        switch (activity.getType()) {
            case EVENT: {
                break;
            }
            case TASK: {

                if (activity.getDeadline().isPresent()
                        && activity.getDeadline().get().hasPast()
                        && !activity.getStatus().get().completed) {
                    //if overdue
                    title.getStyleClass().add(Constants.TextStyle.RED.getStyleClass());
                    icon.setImage(new Image(Constants.Icon.OVERDUE.getPath()));
                    break;
                }

                if (activity.getStatus().get().completed) {
                    //if completed
                    title.getStyleClass().add(Constants.TextStyle.GREEN.getStyleClass());
                    icon.setImage(new Image(Constants.Icon.COMPLETE.getPath()));
                } else {
                    //if not completed
                    title.getStyleClass().add(Constants.TextStyle.AMBER.getStyleClass());
                    icon.setImage(new Image(Constants.Icon.INCOMPLETE.getPath()));
                }
            }
            default: {
                break;
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
