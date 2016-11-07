package seedu.taskman.storage;

import seedu.taskman.commons.core.LogsCenter;
import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.model.ReadOnlyTaskMan;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.UniqueActivityList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * An Immutable TaskMan that is serializable to XML format
 */
@XmlRootElement(name = "taskMan")
public class XmlSerializableTaskMan implements ReadOnlyTaskMan {

    private static final Logger logger = LogsCenter.getLogger(XmlSerializableTaskMan.class);

    @XmlElement
    private List<XmlAdaptedEvent> events;
    @XmlElement
    private List<XmlAdaptedTask> tasks;

    {
        events = new ArrayList<>();
        tasks = new ArrayList<>();
    }

    /**
     * Empty constructor required for marshalling
     */
    public XmlSerializableTaskMan() {
    }

    //@@author A0140136W
    /**
     * Converts data in source file to its respective type of Activity (i.e. Task, Event)
     * for loading in TaskMan.
     */
    public XmlSerializableTaskMan(ReadOnlyTaskMan src) {
        events.addAll(src.getActivityList().stream().filter(activity ->
                                                                activity.getType().equals(Activity.ActivityType.EVENT)
                                                            ).map(XmlAdaptedEvent::new).collect(Collectors.toList()));
        tasks.addAll(src.getActivityList().stream().filter(activity -> 
                                                               activity.getType().equals(Activity.ActivityType.TASK)
                                                           ).map(XmlAdaptedTask::new).collect(Collectors.toList()));
    }
    //@@author

    @Override
    public UniqueActivityList getUniqueActivityList() {
        UniqueActivityList lists = new UniqueActivityList();
        for (XmlAdaptedTask task : tasks) {
            try {
                lists.add(new Activity(task.toModelType()));
            } catch (IllegalValueException e) {
                logger.info(LogsCenter.getConversionFailedMessage(task.toString()));
            }
        }
        for (XmlAdaptedEvent event : events) {
            try {
                lists.add(new Activity(event.toModelType()));
            } catch (IllegalValueException e) {
                logger.info(LogsCenter.getConversionFailedMessage(event.toString()));
            }
        }
        return lists;
    }

    @Override
    public List<Activity> getActivityList() {
        return Collections.unmodifiableList(getUniqueActivityList().getInternalList());
    }

}
