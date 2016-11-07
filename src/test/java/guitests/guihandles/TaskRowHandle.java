package guitests.guihandles;

import guitests.GuiRobot;
import javafx.stage.Stage;
import seedu.taskman.model.event.*;

import java.util.Optional;

/**
 * Provides a handle to a task row in the task list panel.
 */
public class TaskRowHandle extends GuiHandle {
    private Activity task;

    public TaskRowHandle(GuiRobot guiRobot, Stage primaryStage, Activity task) {
        super(guiRobot, primaryStage, null);
        this.task = task;
    }

    public Title getTitle() {
        return task.getTitle();
    }

    public Optional<Deadline> getDeadline() {
        return task.getDeadline();
    }

    public Optional<Schedule> getSchedule() {
        return task.getSchedule();
    }

    public boolean isSameTask(Activity task) {
        return getTitle().equals(task.getTitle()) && getDeadline().equals(task.getDeadline())
                && getSchedule().equals(task.getSchedule());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TaskRowHandle) {
            TaskRowHandle handle = (TaskRowHandle) obj;
            return handle.task.equals(task);
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return getTitle() + " " + getDeadline() + " " + getSchedule();
    }
}
