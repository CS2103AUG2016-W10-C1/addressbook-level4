package seedu.taskman.model;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import seedu.taskman.commons.core.ComponentManager;
import seedu.taskman.commons.core.LogsCenter;
import seedu.taskman.commons.core.UnmodifiableObservableList;
import seedu.taskman.commons.events.model.TaskManChangedEvent;
import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.commons.util.StringUtil;
import seedu.taskman.logic.commands.ListCommand;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.Deadline;
import seedu.taskman.model.event.Event;
import seedu.taskman.model.event.Schedule;
import seedu.taskman.model.event.UniqueActivityList;
import seedu.taskman.model.event.UniqueActivityList.ActivityNotFoundException;
import seedu.taskman.model.tag.Tag;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * Represents the in-memory model of the task man data.
 * All changes to any model should be synchronized.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final TaskMan taskMan;
    private final FilteredList<Activity> filteredActivities;
    private final FilteredList<Activity> filteredSchedules;
    private final FilteredList<Activity> filteredDeadlines;
    private final FilteredList<Activity> filteredFloatings;

    /**
     * Initializes a ModelManager with the given TaskMan
     * TaskMan and its variables should not be null
     */
    public ModelManager(TaskMan src, UserPrefs userPrefs) {
        super();
        assert src != null;
        assert userPrefs != null;

        logger.fine("Initializing with Task Man: " + src + " and user prefs " + userPrefs);

        taskMan = new TaskMan(src);
        ObservableList<Activity> activities = taskMan.getActivities();
        filteredActivities = new FilteredList<>(activities);
        filteredSchedules = activities.filtered(new SchedulePredicate());
        filteredDeadlines = activities.filtered(new DeadlinePredicate());
        filteredFloatings = activities.filtered(new FloatingPredicate());
    }

    public ModelManager() {
        this(new TaskMan(), new UserPrefs());
    }

    public ModelManager(ReadOnlyTaskMan initialData, UserPrefs userPrefs) {
        taskMan = new TaskMan(initialData);
        ObservableList<Activity> activities = taskMan.getActivities();
        filteredActivities = new FilteredList<>(activities);
        filteredSchedules = activities.filtered(new SchedulePredicate());
        filteredDeadlines = activities.filtered(new DeadlinePredicate());
        filteredFloatings = activities.filtered(new FloatingPredicate());
    }

    @Override
    public void resetData(ReadOnlyTaskMan newData) {
        taskMan.resetData(newData);
        indicateTaskManChanged();
    }

    public ReadOnlyTaskMan getTaskMan() {
        return taskMan;
    }

    /**
     * Raises an event to indicate the model has changed
     */
    private void indicateTaskManChanged() {
        raise(new TaskManChangedEvent(taskMan));
    }

    @Override
    public synchronized void deleteActivity(Activity target) throws ActivityNotFoundException {
        taskMan.removeActivity(target);
        indicateTaskManChanged();
    }

    @Override
    public synchronized void addActivity(Event event) throws UniqueActivityList.DuplicateActivityException {
        taskMan.addActivity(event);
        updateFilteredListToShowAll();
        indicateTaskManChanged();
    }

    @Override
    public synchronized void addActivity(Activity activity) throws UniqueActivityList.DuplicateActivityException {
        taskMan.addActivity(activity);
        updateFilteredListToShowAll();
        indicateTaskManChanged();
    }

    //=========== Filtered Task List Accessors ===============================================================

    @Override
    public UnmodifiableObservableList<Activity> getFilteredActivityList() {
        return new UnmodifiableObservableList<>(filteredActivities);
    }
    
    @Override
    public UnmodifiableObservableList<Activity> getFilteredScheduleList() {
        return new UnmodifiableObservableList<>(filteredSchedules);
    }

    @Override
    public UnmodifiableObservableList<Activity> getFilteredDeadlineList() {
        return new UnmodifiableObservableList<>(filteredDeadlines);
    }

    @Override
    public UnmodifiableObservableList<Activity> getFilteredFloatingList() {
        return new UnmodifiableObservableList<>(filteredFloatings);
    }

    @Override
    public void updateFilteredListToShowAll() {
        filteredActivities.setPredicate(null);
    }

    @Override
    public void updateFilteredActivityList(ListCommand.FilterMode filterMode, Set<String> keywords, Set<String> tagNames) {
        updateFilteredActivityList(new PredicateExpression(new ActivityQualifier(filterMode, keywords, tagNames)));
    }

    private void updateFilteredActivityList(Expression expression) {
        filteredActivities.setPredicate(expression::satisfies);
    }

    //========== Inner classes/interfaces used for filtering ==================================================

    interface Expression {
        boolean satisfies(Activity task);

        String toString();
    }

    private class PredicateExpression implements Expression {

        private final Qualifier qualifier;

        PredicateExpression(Qualifier qualifier) {
            this.qualifier = qualifier;
        }

        @Override
        public boolean satisfies(Activity activity) {
            return qualifier.run(activity);
        }

        @Override
        public String toString() {
            return qualifier.toString();
        }
    }

    interface Qualifier {
        boolean run(Activity activity);

        String toString();
    }

    private class ActivityQualifier implements Qualifier {
        private Set<String> titleKeyWords;
        private Set<String> tagNames;
        private ListCommand.FilterMode filterMode = ListCommand.FilterMode.ALL;

        ActivityQualifier(ListCommand.FilterMode filterMode, Set<String> titleKeyWords, Set<String> tagNames) {
            this.filterMode = filterMode;
            this.titleKeyWords = titleKeyWords;
            this.tagNames = tagNames;
        }

        // TODO: refactor, improve readability of this method...
        @Override
        public boolean run(Activity activity) {
            // (fit task/event type && (no keyword || contain a keyword) && (no tag || contain a tag))
            return (filterMode == ListCommand.FilterMode.ALL
                        || (filterMode == ListCommand.FilterMode.SCHEDULE_ONLY && activity.getSchedule().isPresent())
                        || (filterMode == ListCommand.FilterMode.DEADLINE_ONLY && activity.getType() == Activity.ActivityType.TASK
                            && activity.getDeadline().isPresent())
                        || (filterMode == ListCommand.FilterMode.FLOATING_ONLY && activity.getType() == Activity.ActivityType.TASK
                            && !activity.getDeadline().isPresent()))
                    && (titleKeyWords == null || titleKeyWords.isEmpty() || titleKeyWords.stream()
                    .filter(keyword -> StringUtil.containsIgnoreCase(activity.getTitle().title, keyword))
                    .findAny()
                    .isPresent())
                    && (tagNames == null || tagNames.isEmpty() || tagNames.stream()
                    .filter(tagName -> {
                        try {
                            return activity.getTags().contains(new Tag(tagName));
                        } catch (IllegalValueException e) {
                            //ignore incorrect tag name format
                            return false;
                        }
                    })
                    .findAny()
                    .isPresent());
        }

        @Override
        public String toString() {
            return "title=" + String.join(", ", titleKeyWords);
        }
    }
    
    private class SchedulePredicate implements Predicate<Activity> {
        @Override
        public boolean test(Activity t) {
            return t.getSchedule().isPresent();
        }     
    }
    
    private class DeadlinePredicate implements Predicate<Activity> {
        @Override
        public boolean test(Activity t) {
            return t.getType() == Activity.ActivityType.TASK
                   && t.getDeadline().isPresent();
        } 
    }
    
    private class FloatingPredicate implements Predicate<Activity> {
        @Override
        public boolean test(Activity t) {
            return t.getType() == Activity.ActivityType.TASK
                   && !t.getDeadline().isPresent();
        }   
    }

}
