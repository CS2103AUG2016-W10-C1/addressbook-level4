package seedu.taskman.commons.events.model;

import seedu.taskman.commons.events.BaseEvent;
import seedu.taskman.model.ReadOnlyTaskDiary;

/** Indicates the TaskDiary in the model has changed*/
public class TaskDiaryChangedEvent extends BaseEvent {

    public final ReadOnlyTaskDiary data;

    public TaskDiaryChangedEvent(ReadOnlyTaskDiary data){
        this.data = data;
    }

    @Override
    public String toString() {
        return "number of tasks " + data.getTaskList().size() + ", number of tags " + data.getTagList().size();
    }
}
