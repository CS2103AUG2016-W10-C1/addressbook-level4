package seedu.taskman.model.event;

import seedu.taskman.model.tag.UniqueTagList;

/**
 * An interface for classes to allow setting tags
 */
public interface MutableTagsEvent extends ReadOnlyEvent{

    // TODO: might need to add setSchedule
    void setTags(UniqueTagList replacement);
}
