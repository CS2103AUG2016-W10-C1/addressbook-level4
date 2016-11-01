package seedu.taskman.logic.commands;

import seedu.taskman.commons.core.Messages;
import seedu.taskman.commons.core.UnmodifiableObservableList;
import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.logic.parser.CommandParser;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.Deadline;
import seedu.taskman.model.event.Event;
import seedu.taskman.model.event.Frequency;
import seedu.taskman.model.event.Schedule;
import seedu.taskman.model.event.Status;
import seedu.taskman.model.event.Task;
import seedu.taskman.model.event.Title;
import seedu.taskman.model.event.UniqueActivityList;
import seedu.taskman.model.tag.Tag;
import seedu.taskman.model.tag.UniqueTagList;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static seedu.taskman.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

/**
 * Edits an existing activity
 */
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits an existing entry.\n"
            + "Parameters: INDEX [TITLE] [d/DEADLINE] [s/SCHEDULE] [t/TAGS]...\n"
            + "Example: " + COMMAND_WORD
            + " s2 s/mon 10am, tue 2pm";

    public static final String MESSAGE_EDIT_EVENT_PARAMETERS_DISALLOWED = "Deadline and status";
    public static final String MESSAGE_EDIT_EVENT_SUCCESS = "Event updated: %1$s";
    public static final String MESSAGE_EDIT_TASK_SUCCESS = "Task updated: %1$s";
    public static final String MESSAGE_DUPLICATE_ACTIVITY = "An event or a task with the same name already exists";

    private static final Pattern TASK_EDIT_ARGS_FORMAT = // '/' forward slashes are reserved for delimiter prefixes
            Pattern.compile("" + CommandParser.ArgumentPattern.PANEL
                    + CommandParser.ArgumentPattern.TARGET_INDEX
                    + CommandParser.ArgumentPattern.TITLE + "?"
                    + CommandParser.ArgumentPattern.OPTIONAL_DEADLINE
                    + CommandParser.ArgumentPattern.OPTIONAL_STATUS
                    + CommandParser.ArgumentPattern.OPTIONAL_SCHEDULE
                    + CommandParser.ArgumentPattern.OPTIONAL_FREQUENCY
                    + CommandParser.ArgumentPattern.OPTIONAL_TAGS); // variable number of tags

    private final ArgumentContainer argsContainer;
    private Activity beforeEdit;
    private Activity afterEdit;
    private Activity.ActivityType activityType;

    /**
     * Convenience constructor using raw values.
     * Fields which are null are assumed not to be replaced
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    private EditCommand(Activity.PanelType panelType, int targetIndex,
                        @Nullable String title, @Nullable String deadline, @Nullable String status,
                        @Nullable String schedule, @Nullable String frequency, @Nullable Set<String> tags) {
        super(true);
        argsContainer = new ArgumentContainer(panelType, targetIndex, title, deadline, status, schedule, frequency, tags);
    }

    public static Command prepareEdit(String args) {
        final Matcher matcher = TASK_EDIT_ARGS_FORMAT.matcher(args.trim());

        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, MESSAGE_USAGE));
        }

        String panelTypeRaw = matcher.group(CommandParser.Group.targetIndex.name).trim();
        Activity.PanelType panelType = Activity.PanelType.fromString(panelTypeRaw);

        String indexString = matcher.group(CommandParser.Group.targetIndex.name).trim();
        int index = Integer.parseInt(indexString);

        String title = matcher.group(CommandParser.Group.title.name);
        if (title != null && title.trim().isEmpty()) {
            title = null;
        }

        String tags = matcher.group(CommandParser.Group.tagArguments.name);
        return new EditCommand(
                panelType,
                index,
                title,
                matcher.group(CommandParser.Group.deadline.name),
                matcher.group(CommandParser.Group.status.name),
                matcher.group(CommandParser.Group.schedule.name),
                matcher.group(CommandParser.Group.frequency.name),
                tags.isEmpty() ? null : getTagsFromArgs(tags));
    }

    @Override
    public CommandResult execute() {
        assert model != null;

        try {
            initMembers(argsContainer);
        } catch (IllegalValueException e) {
            return new CommandResult(e.getMessage(), false);
        }

        try {
            model.deleteActivity(beforeEdit);
            model.addActivity(afterEdit);
            switch(activityType) {
                case EVENT: {
                    return new CommandResult(String.format(MESSAGE_EDIT_EVENT_SUCCESS, afterEdit), true);
                }
                case TASK: {
                    return new CommandResult(String.format(MESSAGE_EDIT_TASK_SUCCESS, afterEdit), true);
                }
                default: {
                    throw new AssertionError("Entry is neither an event nor a task.", null);
                }
            }
        } catch (UniqueActivityList.ActivityNotFoundException pnfe) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX, false);
        } catch (UniqueActivityList.DuplicateActivityException e) {
            try {
                model.addActivity(beforeEdit);
            } catch (UniqueActivityList.DuplicateActivityException e1) {
                assert false : "Deleted entry should be able to be added back.";
            }
            return new CommandResult(MESSAGE_DUPLICATE_ACTIVITY, false);
        }

    }

    private void initMembers(ArgumentContainer argsContainer) throws IllegalValueException {
        UnmodifiableObservableList<Activity> lastShownList = model.getActivityListForPanelType(argsContainer.panelType);

        if (lastShownList.size() < argsContainer.targetIndex) {
            indicateAttemptToExecuteIncorrectCommand();
            throw new IllegalValueException(Messages.MESSAGE_INVALID_EVENT_DISPLAYED_INDEX);
        }

        beforeEdit = lastShownList.get(argsContainer.targetIndex - 1);
        activityType = beforeEdit.getType();
        if (activityType.equals(Activity.ActivityType.EVENT) &&
                (argsContainer.deadline != null || argsContainer.status != null)) {
            throw new IllegalValueException(String.format(
                    Messages.MESSAGE_INVALID_PARAMETERS,
                    MESSAGE_EDIT_EVENT_PARAMETERS_DISALLOWED,
                    Activity.ActivityType.EVENT)
            );
        }

        Set<Tag> tagSet = new HashSet<>();
        if (argsContainer.tags != null) {
            for (String tagName : argsContainer.tags) {
                tagSet.add(new Tag(tagName));
            }
        }

        switch (activityType){
            case EVENT: {
                Event event = new Event(
                        argsContainer.title == null
                        ? beforeEdit.getTitle()
                        : new Title(argsContainer.title),
                argsContainer.tags == null
                        ? beforeEdit.getTags()
                        : new UniqueTagList(tagSet),
                argsContainer.schedule == null
                        ? beforeEdit.getSchedule().orElse(null)
                        : new Schedule (argsContainer.schedule),
                argsContainer.frequency == null
                        ? beforeEdit.getFrequency().orElse(null)
                        : new Frequency(argsContainer.frequency)
                );
                afterEdit = new Activity(event);
                break;
            }
            case TASK: {
                Task task = new Task(
                        argsContainer.title == null
                        ? beforeEdit.getTitle()
                        : new Title(argsContainer.title),
                argsContainer.tags == null
                        ? beforeEdit.getTags()
                        : new UniqueTagList(tagSet),
                argsContainer.deadline == null
                        ? beforeEdit.getDeadline().orElse(null)
                        : new Deadline(argsContainer.deadline),
                argsContainer.schedule == null
                        ? beforeEdit.getSchedule().orElse(null)
                        : new Schedule (argsContainer.schedule),
                argsContainer.frequency == null
                        ? beforeEdit.getFrequency().orElse(null)
                        : new Frequency(argsContainer.frequency)
                );
                
                task.setStatus(argsContainer.status == null
                               ? beforeEdit.getStatus().orElse(null)
                               : new Status(argsContainer.status));
                        
                afterEdit = new Activity(task);
                break;
            }
            default: {
                assert false : "Entry is neither an event nor a task.";
            }
        }
    }

    private static class ArgumentContainer {
        public final Activity.PanelType panelType;
        public final int targetIndex;
        public final String title;
        public final String deadline;
        public final String status;
        public final String schedule;
        public final String frequency;
        public final Set<String> tags;

        public ArgumentContainer(Activity.PanelType panelType,
                                 int targetIndex,
                                 String title,
                                 String deadline,
                                 String status,
                                 String schedule,
                                 String frequency,
                                 Set<String> tags) {
            this.panelType = panelType;
            this.targetIndex = targetIndex;
            this.title = title;
            this.deadline = deadline;
            this.status = status;
            this.schedule = schedule;
            this.frequency = frequency;
            this.tags = tags;
        }
    }
}