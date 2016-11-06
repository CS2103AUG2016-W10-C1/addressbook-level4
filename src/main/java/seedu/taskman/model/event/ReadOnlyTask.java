package seedu.taskman.model.event;

import seedu.taskman.logic.Formatter;

import java.util.Optional;

public interface ReadOnlyTask extends ReadOnlyEvent {
    Status getStatus();

    Optional<Deadline> getDeadline();

    /**
     * Returns true if both have the same state. (interfaces cannot override .equals)
     */
    default boolean isSameStateAs(ReadOnlyTask other) {
        return other == this // short circuit if same object
                || (other != null // this is first to avoid NPE below
                && other.getTitle().equals(this.getTitle()) // state checks here onwards
                && other.getSchedule().equals(this.getSchedule())
                && other.getDeadline().equals(this.getDeadline())
                && other.getStatus().equals(this.getStatus())
        );
    }

    /**
     * Formats the event as text, showing all contact details.
     */
    default String getAsText() {
        final StringBuilder builder = new StringBuilder();
        builder.append(
                String.format(
                        Formatter.FORMAT_TWO_TERMS_SPACED_WITHIN_AFTER,
                        getTitle(),
                        String.format(
                                Formatter.FORMAT_WRAP_IN_BRACKET,
                                getStatus()
                        )
                ).trim()
        );
        if (!getTags().getInternalList().isEmpty()){
            builder.append("\n");
            getTags().forEach(builder::append);
        }
        if (getDeadline().isPresent()) {
            builder.append("\nDeadline:\n\t")
                    .append(getDeadline().get().toString().replaceAll("\n", " "));
        }
        if (getSchedule().isPresent()) {
            builder.append("\nSchedule:\n\t")
                    .append(getSchedule().get().toStringSelected());
        }
        return builder.toString();
    }
}
