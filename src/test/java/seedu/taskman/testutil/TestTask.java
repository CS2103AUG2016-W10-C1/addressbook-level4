package seedu.taskman.testutil;

import seedu.taskman.logic.commands.AddCommand;
import seedu.taskman.model.tag.UniqueTagList;
import seedu.taskman.model.event.*;

import java.util.Optional;

/**
 * A mutable task object. For testing only.
 */
public class TestTask implements ReadOnlyTask {

    private Title title;
    private Deadline deadline;
    private Status status;
    private Schedule schedule;
    private UniqueTagList tags;

    public TestTask() {
        tags = new UniqueTagList();
        status = new Status();
    }

    @Override
    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    @Override
    public UniqueTagList getTags() {
        return tags;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean isExpired() {
        // currently unused for testing
        return false;
    }

    @Override
    public Optional<Deadline> getDeadline() {
        return Optional.ofNullable(deadline);
    }

    public void setDeadline(Deadline deadline) {
        this.deadline = deadline;
    }

    @Override
    public Optional<Schedule> getSchedule() {
        return Optional.ofNullable(schedule);
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    @Override
    public String toString() {
        return getAsText();
    }

    public String getAddCommand() {
        StringBuilder sb = new StringBuilder();
        sb.append(AddCommand.COMMAND_WORD + " " + this.getTitle().title + " ");
        if (this.getDeadline().isPresent()) {
            sb.append("d/" + this.getDeadline().get().toFormalString() + " ");
        }
        if (this.getSchedule().isPresent()) {
            sb.append("s/" + this.getSchedule().get().toFormalString() + " ");
        }
        this.getTags().getInternalList().stream().forEach(s -> sb.append("t/" + s.tagName + " "));
        return sb.toString().replace('\n',' ');
    }

}
