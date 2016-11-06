package seedu.taskman.model.event;

import seedu.taskman.model.tag.UniqueTagList;

import java.util.Optional;

/**
 * A read-only immutable interface for an Event in the TaskMan.
 * Implementations should guarantee: details are present and not null, field values are validated.
 */
public interface ReadOnlyEvent {

    Title getTitle();

    Optional<Schedule> getSchedule();

    /**
     * Expiry indicates if the event is no longer useful in business logic.
     * A positive result gives the green light for removal from storage.
     */
    boolean isExpired();

    /**
     * The returned TagList is a deep copy of the internal TagList,
     * changes on the returned list will not affect the event's internal tags.
     */
    UniqueTagList getTags();

    /**
     * Returns true if both have the same state. (interfaces cannot override .equals)
     */
    default boolean isSameStateAs(ReadOnlyEvent other) {
        return other == this // short circuit if same object
                || (other != null // this is first to avoid NPE below
                && other.getTitle().equals(this.getTitle()) // state checks here onwards
                && other.getSchedule().equals(this.getSchedule())
        );
    }

    /**
     * Formats the event as text, showing all details.
     */
    default String getAsText() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getTitle());
        if (!getTags().getInternalList().isEmpty()){
            builder.append("\n");
            getTags().forEach(builder::append);
        }
        if (getSchedule().isPresent()) {
            builder.append("\nSchedule:\n\t")
                    .append(getSchedule().get().toStringSelected());
        }
        return builder.toString();
    }

    /**
     * Returns a string representation of this event's tags
     */
    default String tagsString() {
        final StringBuffer buffer = new StringBuffer();
        final String separator = ", ";
        getTags().forEach(tag -> buffer.append(tag).append(separator));
        if (buffer.length() == 0) {
            return "";
        } else {
            return buffer.substring(0, buffer.length() - separator.length());
        }
    }

}
