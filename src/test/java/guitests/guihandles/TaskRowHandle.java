package guitests.guihandles;

import java.util.Optional;

import guitests.GuiRobot;
import javafx.stage.Stage;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.Deadline;
import seedu.taskman.model.event.Frequency;
import seedu.taskman.model.event.Schedule;

/**
 * Provides a handle to a task row in the task list panel.
 */
public class TaskRowHandle extends GuiHandle {
    private Activity task;

    public TaskRowHandle(GuiRobot guiRobot, Stage primaryStage, Activity task) {
        super(guiRobot, primaryStage, null);
        this.task = task;
    }

    public String getTitle() {
        return task.getTitle().title;
    }

    public Optional<Deadline> getDeadline() {
        return task.getDeadline();
    }

    public Optional<Schedule> getSchedule() {
        return task.getSchedule();
    }

    public Optional<Frequency> getFrequency() {
        return task.getFrequency();
    }

    public boolean isSameTask(Activity task) {
        return getTitle().equals(task.getTitle().title) && getDeadline().equals(task.getDeadline())
                && getSchedule().equals(task.getSchedule()) && getFrequency().equals(task.getFrequency());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TaskRowHandle) {
            TaskRowHandle handle = (TaskRowHandle) obj;
            return getTitle().equals(handle.getTitle())
                    && getDeadline().equals(handle.getDeadline())
                    && getSchedule().equals(handle.getSchedule())
                    && getFrequency().equals(handle.getFrequency()); //TODO: compare the rest
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return getTitle() + " " + getDeadline() + " " + getSchedule() + " " + getFrequency();
    }
}
