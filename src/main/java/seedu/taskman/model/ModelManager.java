package seedu.taskman.model;

import javafx.collections.transformation.FilteredList;
import seedu.taskman.commons.core.LogsCenter;
import seedu.taskman.commons.core.UnmodifiableObservableList;
import seedu.taskman.commons.util.StringUtil;
import seedu.taskman.model.task.ReadOnlyTask;
import seedu.taskman.model.task.Task;
import seedu.taskman.model.task.UniqueTaskList;
import seedu.taskman.model.task.UniqueTaskList.TaskNotFoundException;
import seedu.taskman.commons.events.model.TaskDiaryChangedEvent;
import seedu.taskman.commons.core.ComponentManager;

import java.util.Set;
import java.util.logging.Logger;

/**
 * Represents the in-memory model of the task diary data.
 * All changes to any model should be synchronized.
 */
public class ModelManager extends ComponentManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final TaskDiary taskDiary;
    private final FilteredList<Task> filteredTasks;

    /**
     * Initializes a ModelManager with the given TaskDiary
     * TaskDiary and its variables should not be null
     */
    public ModelManager(TaskDiary src, UserPrefs userPrefs) {
        super();
        assert src != null;
        assert userPrefs != null;

        logger.fine("Initializing with Task Diary: " + src + " and user prefs " + userPrefs);

        taskDiary = new TaskDiary(src);
        filteredTasks = new FilteredList<>(taskDiary.getTasks());
    }

    public ModelManager() {
        this(new TaskDiary(), new UserPrefs());
    }

    public ModelManager(ReadOnlyTaskDiary initialData, UserPrefs userPrefs) {
        taskDiary = new TaskDiary(initialData);
        filteredTasks = new FilteredList<>(taskDiary.getTasks());
    }

    @Override
    public void resetData(ReadOnlyTaskDiary newData) {
        taskDiary.resetData(newData);
        indicateTaskDiaryChanged();
    }

    public ReadOnlyTaskDiary getTaskDiary() {
        return taskDiary;
    }

    /** Raises an event to indicate the model has changed */
    private void indicateTaskDiaryChanged() {
        raise(new TaskDiaryChangedEvent(taskDiary));
    }

    @Override
    public synchronized void deleteTask(ReadOnlyTask target) throws TaskNotFoundException {
        taskDiary.removeTask(target);
        indicateTaskDiaryChanged();
    }

    @Override
    public synchronized void addTask(Task task) throws UniqueTaskList.DuplicateTaskException {
        taskDiary.addTask(task);
        updateFilteredListToShowAll();
        indicateTaskDiaryChanged();
    }

    //=========== Filtered Task List Accessors ===============================================================

    @Override
    public UnmodifiableObservableList<ReadOnlyTask> getFilteredTaskList() {
        return new UnmodifiableObservableList<>(filteredTasks);
    }

    @Override
    public void updateFilteredListToShowAll() {
        filteredTasks.setPredicate(null);
    }

    @Override
    public void updateFilteredTaskList(Set<String> keywords){
        updateFilteredTaskList(new PredicateExpression(new NameQualifier(keywords)));
    }

    private void updateFilteredTaskList(Expression expression) {
        filteredTasks.setPredicate(expression::satisfies);
    }

    //========== Inner classes/interfaces used for filtering ==================================================

    interface Expression {
        boolean satisfies(ReadOnlyTask task);
        String toString();
    }

    private class PredicateExpression implements Expression {

        private final Qualifier qualifier;

        PredicateExpression(Qualifier qualifier) {
            this.qualifier = qualifier;
        }

        @Override
        public boolean satisfies(ReadOnlyTask task) {
            return qualifier.run(task);
        }

        @Override
        public String toString() {
            return qualifier.toString();
        }
    }

    interface Qualifier {
        boolean run(ReadOnlyTask task);
        String toString();
    }

    private class NameQualifier implements Qualifier {
        private Set<String> nameKeyWords;

        NameQualifier(Set<String> nameKeyWords) {
            this.nameKeyWords = nameKeyWords;
        }

        @Override
        public boolean run(ReadOnlyTask task) {
            return nameKeyWords.stream()
                    .filter(keyword -> StringUtil.containsIgnoreCase(task.getTitle().title, keyword))
                    .findAny()
                    .isPresent();
        }

        @Override
        public String toString() {
            return "name=" + String.join(", ", nameKeyWords);
        }
    }

}
