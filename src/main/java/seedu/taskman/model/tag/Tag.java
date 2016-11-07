package seedu.taskman.model.tag;


import seedu.taskman.commons.exceptions.IllegalValueException;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Represents a Tag in the task man.
 * Guarantees: immutable; name is valid as declared in {@link #isValidTagName(String)}
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Tag {

    public static final String MESSAGE_TAG_CONSTRAINTS = "Tag name should form a word. " +
            "All punctuation except '/' is accepted";
    public static final String TAG_VALIDATION_REGEX = "[\\w\\p{Punct}&&[^/]]+";

    @XmlElement
    public final String tagName;

    @SuppressWarnings("unused")
    private Tag() {
        tagName = null;
    }

    /**
     * Validates given tag name.
     *
     * @throws IllegalValueException if the given tag name string is invalid.
     */
    public Tag(String name) throws IllegalValueException {
        assert name != null;
        name = name.trim();
        if (!isValidTagName(name)) {
            throw new IllegalValueException(MESSAGE_TAG_CONSTRAINTS);
        }
        this.tagName = name;
    }

    /**
     * Returns true if a given string is a valid tag name.
     */
    public static boolean isValidTagName(String test) {
        return test.matches(TAG_VALIDATION_REGEX);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Tag // instanceof handles nulls
                && this.tagName.equals(((Tag) other).tagName)); // state check
    }

    @Override
    public int hashCode() {
        return tagName.hashCode();
    }

    /**
     * Format state as text for viewing.
     */
    public String toString() {
        return '[' + tagName + ']';
    }

}
