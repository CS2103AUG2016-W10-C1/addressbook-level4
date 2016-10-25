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
    private final SortedList<Activity> sortedActivities;
    private final SortedList<Activity> sortedSchedules;
    private final SortedList<Activity> sortedDeadlines;
    private final SortedList<Activity> sortedFloatings;

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
        sortedActivities = new SortedList<>(activities);
        sortedSchedules = activities.filtered(new SchedulePredicate()).sorted(new ScheduleComparator());
        sortedDeadlines = activities.filtered(new DeadlinePredicate()).sorted(new DeadlineComparator());
        sortedFloatings = activities.filtered(new FloatingPredicate()).sorted();
    }

    public ModelManager() {
        this(new TaskMan(), new UserPrefs());
    }

    public ModelManager(ReadOnlyTaskMan initialData, UserPrefs userPrefs) {
        taskMan = new TaskMan(initialData);
        ObservableList<Activity> activities = taskMan.getActivities();
        sortedActivities = new SortedList<>(activities);
        sortedSchedules = activities.filtered(new SchedulePredicate()).sorted(new ScheduleComparator());
        sortedDeadlines = activities.filtered(new DeadlinePredicate()).sorted(new DeadlineComparator());
        sortedFloatings = activities.filtered(new FloatingPredicate()).sorted();
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
        //updateFilteredListToShowAll();
        indicateTaskManChanged();
    }

    @Override
    public synchronized void addActivity(Activity activity) throws UniqueActivityList.DuplicateActivityException {
        taskMan.addActivity(activity);
        //updateFilteredListToShowAll();
        indicateTaskManChanged();
    }

    //=========== Sorted Task List Accessors ===============================================================


    @Override
    public UnmodifiableObservableList<Activity> getActivityListForPanelType(Activity.PanelType type) {
        switch (type) {
            case DEADLINE: {
                return getSortedDeadlineList();
            }
            case SCHEDULE: {
                return getSortedScheduleList();
            }
            case FLOATING: {
                return getSortedFloatingList();
            }
            default:
                throw new AssertionError("Unspecified panel type");
        }
    }

    //TODO Remove
    /*
    @Override
    public UnmodifiableObservableList<Activity> getFilteredActivityList() {
        return new UnmodifiableObservableList<>(sortedActivities);
    }
    */
    
    @Override
    public UnmodifiableObservableList<Activity> getSortedScheduleList() {
        return new UnmodifiableObservableList<>(sortedSchedules);
    }

    @Override
    public UnmodifiableObservableList<Activity> getSortedDeadlineList() {
        return new UnmodifiableObservableList<>(sortedDeadlines);
    }

    @Override
    public UnmodifiableObservableList<Activity> getSortedFloatingList() {
        return new UnmodifiableObservableList<>(sortedFloatings);
    }

    //TODO Remove
    /*
    @Override
    public void updateFilteredListToShowAll() {
        sortedActivities.setPredicate(null);
    }

    @Override
    public void updateFilteredActivityList(ListCommand.FilterMode filterMode, Set<String> keywords, Set<String> tagNames) {
        updateFilteredActivityList(new PredicateExpression(new ActivityQualifier(filterMode, keywords, tagNames)));
    }

    private void updateFilteredActivityList(Expression expression) {
        sortedActivities.setPredicate(expression::satisfies);
    }
    */

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
    
    private static class SchedulePredicate implements Predicate<Activity> {
        @Override
        public boolean test(Activity t) {
            return t.getSchedule().isPresent();
        }     
    }
    
    private static class DeadlinePredicate implements Predicate<Activity> {
        @Override
        public boolean test(Activity t) {
            return t.getType() == Activity.ActivityType.TASK
                   && t.getDeadline().isPresent();
        } 
    }
    
    private static class FloatingPredicate implements Predicate<Activity> {
        @Override
        public boolean test(Activity t) {
            return t.getType() == Activity.ActivityType.TASK
                   && !t.getDeadline().isPresent();
        }   
    }
    
    private static class ScheduleComparator implements Comparator<Activity> {
        @Override
        public int compare(Activity activity1, Activity activity2) {
            Optional<Schedule> schedule1 = activity1.getSchedule();
            Optional<Schedule> schedule2 = activity2.getSchedule();
            if (!schedule1.isPresent() || !schedule2.isPresent()) {
                throw new AssertionError("There are acitivities in the schedules table that have no schedules!", null);
            }
            Long start1 = schedule1.get().startEpochSecond;
            Long start2 = schedule2.get().startEpochSecond;
            return start1.compareTo(start2);
        } 
    }
    
    private static class DeadlineComparator implements Comparator<Activity> {
        @Override
        public int compare(Activity activity1, Activity activity2) {
            Optional<Deadline> deadline1 = activity1.getDeadline();
            Optional<Deadline> deadline2 = activity2.getDeadline();
            if (!deadline1.isPresent() || !deadline2.isPresent()) {
                throw new AssertionError("There are acitivities in the deadlines table that have no deadlines!", null);
            }
            Long due1 = deadline1.get().epochSecond;
            Long due2 = deadline2.get().epochSecond;
            return due1.compareTo(due2);
        } 
    }

}
