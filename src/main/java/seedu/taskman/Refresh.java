package seedu.taskman;

import seedu.taskman.commons.core.UnmodifiableObservableList;
import seedu.taskman.model.event.*;

import java.time.Instant;
import java.util.TimerTask;

/**
 * Created by jiayee on 11/1/16.
 */
public class Refresh extends TimerTask {

    MainApp mainApp;

    public Refresh(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @Override
    public void run() {
        UnmodifiableObservableList<Activity> scheduleList = mainApp.model.getSortedScheduleList(); // the data type is unmodifiable though
        int i = 0, size = scheduleList.size();
        while (i < size) {
            Activity activity = scheduleList.get(i);
            Schedule schedule = activity.getSchedule().get();
            if (schedule != null && schedule.endEpochSecond > Instant.now().getEpochSecond()) {
                mainApp.model.getSortedScheduleList().remove(i);
                Activity entryToAdd = null;
                switch(activity.getType()) {
                    case EVENT:
                        Event event = new Event(
                                activity.getTitle(),
                                activity.getTags(),
                                null,
                                activity.getFrequency().orElse(null)
                        );
                        entryToAdd = new Activity(event);
                        break;
                    case TASK:
                        Task task = new Task(
                                activity.getTitle(),
                                activity.getTags(),
                                activity.getDeadline().orElse(null),
                                null,
                                activity.getFrequency().orElse(null));
                        task.setStatus(activity.getStatus().orElse(null));
                        entryToAdd = new Activity(task);
                        break;
                    default:
                        assert false : "Entry is neither an event nor a task.";
                }
                mainApp.model.getSortedScheduleList().add(i, entryToAdd);
                size--;
            } else {
                i++;
            }
        }
        mainApp.ui.refresh();
        System.out.println("Refreshed...");
    }
}
