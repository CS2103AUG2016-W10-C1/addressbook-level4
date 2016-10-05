package seedu.taskman.testutil;

import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.model.TaskDiary;
import seedu.taskman.model.tag.Tag;
import seedu.taskman.model.task.Task;
import seedu.taskman.model.task.UniqueTaskList;

/**
 * A utility class to help with building TaskDiary objects.
 * Example usage: <br>
 *     {@code TaskDiary ab = new TaskDiaryBuilder().withTask("John", "Doe").withTag("Friend").build();}
 */
public class TaskDiaryBuilder {

    private TaskDiary taskDiary;

    public TaskDiaryBuilder(TaskDiary taskDiary){
        this.taskDiary = taskDiary;
    }

    public TaskDiaryBuilder withTask(Task task) throws UniqueTaskList.DuplicateTaskException {
        taskDiary.addTask(task);
        return this;
    }

    public TaskDiaryBuilder withTag(String tagName) throws IllegalValueException {
        taskDiary.addTag(new Tag(tagName));
        return this;
    }

    public TaskDiary build(){
        return taskDiary;
    }
}
