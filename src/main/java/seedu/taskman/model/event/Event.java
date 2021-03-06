package seedu.taskman.model.event;

import seedu.taskman.commons.util.CollectionUtil;
import seedu.taskman.model.tag.UniqueTagList;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a Task in the task man.
 * Guarantees: Title and UniqueTagList are present and not null, field values are validated.
 */
public class Event implements ReadOnlyEvent, MutableTagsEvent {
    private static final long DAY_IN_SECONDS = 60 * 60 * 24;
    protected static final long EXPIRY_THRESHOLD = DAY_IN_SECONDS * 7;
    private Title title;
    private Schedule schedule;

    private UniqueTagList tags;

    public Event(@Nonnull Title title, @Nonnull UniqueTagList tags,
                 @Nonnull Schedule schedule) {
        assert !CollectionUtil.isAnyNull(title, tags);
        this.title = title;
        this.schedule = schedule;
        this.tags = new UniqueTagList(tags); // protect internal tags from changes in the arg list
    }

    /**
     * Copy constructor.
     */
    public Event(ReadOnlyEvent source) {
        this(source.getTitle(), source.getTags(),
                source.getSchedule().orElse(null)
        );
    }

    @Override
    public Title getTitle() {
        return title;
    }

    @Override
    public Optional<Schedule> getSchedule() {
        return Optional.ofNullable(schedule);
    }

    @Override
    public UniqueTagList getTags() {
        return new UniqueTagList(tags);
    }

    @Override
    public boolean isExpired() {
        if (schedule == null) {
            return false;
        }

        long secondsElapsedSinceEnd = Instant.now().getEpochSecond() - schedule.endEpochSecond;
        return secondsElapsedSinceEnd > EXPIRY_THRESHOLD;
    }

    /**
     * Replaces this task's tags with the tags in the argument tag list.
     */
    public void setTags(UniqueTagList replacement) {
        tags.setTags(replacement);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Event // instanceof handles nulls
                && this.isSameStateAs((Event) other));
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(title, schedule, tags);
    }

    @Override
    public String toString() {
        return getAsText();
    }

}
