package seedu.taskman.logic.commands;
import seedu.taskman.commons.core.UnmodifiableObservableList;
import seedu.taskman.model.tag.Tag;

//@@author A0140136W
public class TagsCommand extends Command {

    public static final String COMMAND_WORD = "tags";

    public static final String MESSAGE_USAGE = "Show all existing tags.\n"
                                               + "Example: " + COMMAND_WORD;

    public static final String TAG_STRING_HEADER = "Existing tags:\n";
    public static final String TAG_STRING_EMPTY_PLACEHOLDER = "NIL";

    public TagsCommand() {
        // Command is not stored in history.
        super(false);
    }

    @Override
    public CommandResult execute() {
        // Gets list of tags.
        UnmodifiableObservableList<Tag> tags = model.getTagList();
            
        // Builds a string for command result.
        StringBuilder builder = new StringBuilder(TAG_STRING_HEADER);
        
        // If there are no tags, append NIL to string builder.
        // Else, append each tag name to the builder.
        if(tags.isEmpty()) {
            builder.append(TAG_STRING_EMPTY_PLACEHOLDER);
        } else {
            for (Tag tag : tags) {
                builder.append(tag.toString());
                builder.append(" ");
            }
        }
        return new CommandResult(builder.toString().trim(), true);
    }

}
