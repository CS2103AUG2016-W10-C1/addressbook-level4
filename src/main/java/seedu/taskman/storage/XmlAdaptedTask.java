package seedu.taskman.storage;

import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.model.event.*;
import seedu.taskman.model.tag.Tag;
import seedu.taskman.model.tag.UniqueTagList;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * JAXB-friendly version of the Task.
 */
public class XmlAdaptedTask {

    @XmlElement(required = true)
    private String title;
    @XmlElement(required = true)
    private String status;

    @XmlElement(required = false)
    private String deadline;
    @XmlElement(required = false)
    private String scheduleStart;
    @XmlElement(required = false)
    private String scheduleEnd;

    @XmlElement
    private List<XmlAdaptedTag> tagged = new ArrayList<>();

    /**
     * No-arg constructor for JAXB use.
     */
    public XmlAdaptedTask() {
    }


    /**
     * Converts a given Task into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created XmlAdaptedTask
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public XmlAdaptedTask(Activity source) {
        title = source.getTitle().title;

        if (source.getStatus().isPresent()) {
            status = source.getStatus().get().completed ?
                    "complete" : "incomplete";
        }

        if (source.getDeadline().isPresent()) {
            deadline = source.getDeadline().get().toFormalString();
        }

        if (source.getSchedule().isPresent()) {
            Schedule schedule = source.getSchedule().get();
            scheduleStart = schedule.getFormalStartString();
            scheduleEnd = schedule.getFormalEndString();
        }

        tagged = new ArrayList<>();
        for (Tag tag : source.getTags()) {
            tagged.add(new XmlAdaptedTag(tag));
        }
    }


    /**
     * Converts this JAXB-friendly adapted task object into the model's Task object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted task
     */
    public Task toModelType() throws IllegalValueException {
        final List<Tag> taskTags = new ArrayList<>();
        for (XmlAdaptedTag tag : tagged) {
            taskTags.add(tag.toModelType());
        }
        final Title title = new Title(this.title);
        final Status status = new Status(this.status);
        final UniqueTagList tags = new UniqueTagList(taskTags);
        final Deadline deadline = this.deadline != null
                ? new Deadline(this.deadline)
                : null;
        final Schedule schedule = this.scheduleStart != null && this.scheduleEnd != null
                ? new Schedule(this.scheduleStart + ", " + this.scheduleEnd)
                : null;

        Task task = new Task(title, tags, deadline, schedule);
        task.setStatus(status);
        return task;
    }

    /**
     *
     * @return String representation for logging
     */
    @Override
    public String toString(){
        final StringBuilder builder = new StringBuilder();
        builder.append("Title: ").append(title).append(", ");
        builder.append("Status: ").append(status).append(", ");
        builder.append("Deadline: ").append(deadline).append(", ");
        builder.append("Schedule: ").append(scheduleStart + " to " +scheduleEnd).append(", ");
        builder.append("Tags: ").append(tagged);
        return builder.toString();
    }
}
