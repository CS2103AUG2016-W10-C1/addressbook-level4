package seedu.taskman.ui;

import javafx.scene.control.ListCell;
import seedu.taskman.model.event.Activity;
import seedu.taskman.ui.activitycard.ActivityCardLoader;

/**
 * Created by YiMin on 26/10/2016.
 */
public class ActivityListViewCell extends ListCell<Activity> {

    private final Activity.PanelType panelType;

    public ActivityListViewCell(Activity.PanelType panelType){
        this.panelType = panelType;
    }

    @Override
    protected void updateItem(Activity activity, boolean empty) {
        super.updateItem(activity, empty);

        if (empty || activity == null) {
            setGraphic(null);
            setText(null);
        } else {
            setGraphic(ActivityCardLoader.load(activity, panelType, getIndex() + 1).getLayout());
        }
    }

}
