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
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.Deadline;
import seedu.taskman.model.event.Event;
import seedu.taskman.model.event.Schedule;
import seedu.taskman.model.event.UniqueActivityList;
import seedu.taskman.model.event.UniqueActivityList.ActivityNotFoundException;
import seedu.taskman.model.tag.Tag;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * Represents the in-memory model of the TaskMan data.
 * All changes to any model should be synchronized.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final TaskMan taskMan;
    
    private final FilteredList<Activity> filteredSchedules;
    private final FilteredList<Activity> filteredDeadlines;
    private final FilteredList<Activity> filteredFloatings;
    
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
        filteredSchedules = activities.filtered(new SchedulePredicate());
        filteredDeadlines = activities.filtered(new DeadlinePredicate());
        filteredFloatings = activities.filtered(new FloatingPredicate());
        sortedSchedules = filteredSchedules.sorted(new ScheduleComparator());
        sortedDeadlines = filteredDeadlines.sorted(new DeadlineComparator());
        sortedFloatings = filteredFloatings.sorted();
    }

    public ModelManager(ReadOnlyTaskMan initialData, UserPrefs userPrefs) {
        taskMan = new TaskMan(initialData);
        ObservableList<Activity> activities = taskMan.getActivities();
        filteredSchedules = activities.filtered(new SchedulePredicate());
        filteredDeadlines = activities.filtered(new DeadlinePredicate());
        filteredFloatings = activities.filtered(new FloatingPredicate());
        sortedSchedules = filteredSchedules.sorted(new ScheduleComparator());
        sortedDeadlines = filteredDeadlines.sorted(new DeadlineComparator());
        sortedFloatings = filteredFloatings.sorted();
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
        indicateTaskManChanged();
    }

    @Override
    public synchronized void addActivity(Activity activity) throws UniqueActivityList.DuplicateActivityException {
        taskMan.addActivity(activity);
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

    //@@author A0140136W
    @Override
    public void updateAllPanelsToShowAll() {
        updateFilteredPanel(Activity.PanelType.ALL, Collections.EMPTY_SET, Collections.EMPTY_SET);
    }
    
    public void updateFilteredPanel(Activity.PanelType panel, Set<String> keywords, Set<String> tagNames) {
        Expression schedule = new PredicateExpression(
                new ActivityQualifier(Activity.PanelType.SCHEDULE, keywords, tagNames));
        Expression deadline = new PredicateExpression(
                new ActivityQualifier(Activity.PanelType.DEADLINE, keywords, tagNames));
        Expression floating = new PredicateExpression(
                new ActivityQualifier(Activity.PanelType.FLOATING, keywords, tagNames));

        switch(panel) {
            case ALL: {
                filteredSchedules.setPredicate(schedule::satisfies);
                filteredDeadlines.setPredicate(deadline::satisfies);
                filteredFloatings.setPredicate(floating::satisfies);
                return;
            }
            case SCHEDULE: {
                filteredSchedules.setPredicate(schedule::satisfies);
                return;
            }
            case DEADLINE: {
                filteredDeadlines.setPredicate(deadline::satisfies);
                return;
            }
            case FLOATING: {
                filteredFloatings.setPredicate(floating::satisfies);
                return;
            }
            default: {
                assert false : "No such panel.";
            }
        }
    }
    //@@author

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

    //========== Inner classes/interfaces used for filtering ==================================================

    interface Expression {
        boolean satisfies(Activity task);

        String toString();
    }

    private static class PredicateExpression implements Expression {

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

    private static class ActivityQualifier implements Qualifier {        
        private Set<String> titleKeyWords;
        private Set<String> tagNames;
        private Activity.PanelType panelType;

        ActivityQualifier(Activity.PanelType panel, Set<String> titleKeyWords, Set<String> tagNames) {
            this.panelType = panel;
            this.titleKeyWords = titleKeyWords;
            this.tagNames = tagNames;
        }

        @Override
        public boolean run(@Nonnull Activity activity) {
            boolean noTitleKeyWords = titleKeyWords == null
                                      || titleKeyWords.isEmpty()
                                      || (titleKeyWords.size() == 1 && titleKeyWords.contains(""));
            boolean noTags = tagNames == null
                             || tagNames.isEmpty();

            // (no keyword || contain a keyword) && (no tag || contain a tag))
            return isCorrectActivityType(activity)
                   &&(noTitleKeyWords || containKeyWordsInTitle(titleKeyWords, activity))
                   && (noTags || containsTags(tagNames, activity));
        }
        
        private boolean isCorrectActivityType(Activity activity) {
            if (panelType == null) {
                throw new AssertionError("No such panel.", null);
            } else {
                switch(panelType) {
                    case SCHEDULE: {
                        return activity.getSchedule().isPresent();
                    }
                    case DEADLINE: {
                        return activity.getType() == Activity.ActivityType.TASK
                               && activity.getDeadline().isPresent();
                    }
                    case FLOATING: {
                        return activity.getType() == Activity.ActivityType.TASK
                               && !activity.getDeadline().isPresent();
                    }
                    default: {
                        throw new AssertionError("No such panel.", null);
                    }
                }
            }
        }

        private boolean containKeyWordsInTitle(@Nonnull Set<String> titleKeyWords, Activity activity) {
            return titleKeyWords.stream()
                    .filter(keyword -> StringUtil.containsIgnoreCase(activity.getTitle().title, keyword))
                    .findAny()
                    .isPresent();
        }

        private boolean containsTags(@Nonnull Set<String> tagNames, Activity activity) {
            return tagNames.stream()
                    .filter(tagName -> {
                        try {
                            return activity.getTags().contains(new Tag(tagName));
                        } catch (IllegalValueException e) {
                            //ignore incorrect tag name format
                            return false;
                        }})
                    .findAny()
                    .isPresent();
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
                throw new AssertionError("There are activities in the schedules table that have no schedules!", null);
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
                throw new AssertionError("There are activities in the deadlines table that have no deadlines!", null);
            }
            Long due1 = deadline1.get().epochSecond;
            Long due2 = deadline2.get().epochSecond;
            return due1.compareTo(due2);
        } 
    }

}
