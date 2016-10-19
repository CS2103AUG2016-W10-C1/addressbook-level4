package seedu.taskman.storage;

import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.model.tag.Tag;
import seedu.taskman.model.tag.UniqueTagList;
import seedu.taskman.model.event.*;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

/**
 * JAXB-friendly version of the Event.
 */
public class XmlAdaptedEvent {

    @XmlElement(required = true)
    private String title;
    
    @XmlElement(required = false)
    private Long frequency;

    @XmlElement(required = false)
    private Long scheduleStart;
    @XmlElement(required = false)
    private Long scheduleEnd;

    @XmlElement
    private List<XmlAdaptedTag> tagged = new ArrayList<>();

    /**
     * No-arg constructor for JAXB use.
     */
    public XmlAdaptedEvent() {}


    /**
     * Converts a given Event into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created XmlAdaptedEvent
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public XmlAdaptedEvent(Activity source) {
        title = source.getTitle().title;

        if (source.getSchedule().isPresent()) {
            Schedule schedule = source.getSchedule().get();
            scheduleStart = schedule.startEpochSecond;
            scheduleEnd = schedule.endEpochSecond;
        }

        if (source.getFrequency().isPresent()) {
            frequency = source.getFrequency().get().seconds;
        }

        tagged = new ArrayList<>();
        for (Tag tag : source.getTags()) {
            tagged.add(new XmlAdaptedTag(tag));
        }
    }


    /**
     * Converts this JAXB-friendly adapted event object into the model's Event object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted event
     */
    public Event toModelType() throws IllegalValueException {
        final List<Tag> eventTags = new ArrayList<>();
        for (XmlAdaptedTag tag : tagged) {
            eventTags.add(tag.toModelType());
        }
        final Title title = new Title(this.title);
        final UniqueTagList tags = new UniqueTagList(eventTags);
        final Frequency frequency = this.frequency != null
                ? new Frequency(this.frequency)
                : null;
        final Schedule schedule = this.scheduleStart != null && this.scheduleEnd != null
                ? new Schedule(this.scheduleStart, this.scheduleEnd)
                : null;

        Event event = new Event(title, tags, schedule, frequency);
        return event;
    }
}
