package seedu.taskman.storage;

import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.model.event.TimeSlot;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;

public class XmlAdaptedTimeSlot {

    @XmlElement
    public Long startEpochSecond;

    @XmlElement
    public Long endEpochSecond;

    // JAXB constructor
    public XmlAdaptedTimeSlot() {}

    public XmlAdaptedTimeSlot(TimeSlot source) {
        startEpochSecond = source.startEpochSecond;
        endEpochSecond = source.endEpochSecond;
    }

    public TimeSlot toModelType() throws IllegalValueException {
        return new TimeSlot(startEpochSecond, endEpochSecond);
    }

}
