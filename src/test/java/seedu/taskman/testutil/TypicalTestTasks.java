package seedu.taskman.testutil;

import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.model.TaskMan;
import seedu.taskman.model.event.*;

/**
 *
 */
public class TypicalTestTasks {

    public TestTask taskCS2101, taskCS2103T, taskCS2309, taskCS3244, taskCS2105, taskCS2106, taskCS2107, taskCS2102, taskCS2104;

    public TypicalTestTasks() {
        try {
            taskCS2101 =  new TaskBuilder("CS2101").withDeadline("mon 1200").withSchedule("fri 0000 to fri 0300").build();
            taskCS2103T = new TaskBuilder("CS2103T Project").withDeadline("wed 1000").withSchedule("fri 0000 to fri 0300").withTags("SuperShag", "V02").build();
            taskCS2309 = new TaskBuilder("CS2309").withDeadline("fri 0900").withSchedule("fri 0000 to fri 0300").build();
            taskCS3244 = new TaskBuilder("CS3244 Project").withDeadline("wed 1400").withSchedule("thu 1400 to thu 1500").build();
            taskCS2105 = new TaskBuilder("CS2105").withDeadline("mon 1600").withSchedule("mon 1400, mon 1600").build();
            taskCS2106 = new TaskBuilder("CS2106").withDeadline("fri 1200").withSchedule("fri 1200, fri 1400").build();
            taskCS2107 = new TaskBuilder("CS2107").withDeadline("fri 1800").withSchedule("fri 1600, next sat 1600").build();
            taskCS2102 = new TaskBuilder("CS2102").withDeadline("tue 1200").withSchedule("tue 1000, tue 1200").build();
            taskCS2104 = new TaskBuilder("CS2104").withDeadline("mon 1000").withSchedule("sun 2300 to next mon 0100").build();
        } catch (IllegalValueException e) {
            e.printStackTrace();
            assert false : "error building typical tasks";
        }
    }

    public void loadTaskManWithSampleData(TaskMan ab) {

        try {
            ab.addActivity(new Task(taskCS2101));
            ab.addActivity(new Task(taskCS2103T));
            ab.addActivity(new Task(taskCS2309));
            ab.addActivity(new Task(taskCS3244));
            ab.addActivity(new Task(taskCS2105));
            ab.addActivity(new Task(taskCS2106));
            ab.addActivity(new Task(taskCS2107));
        } catch (UniqueActivityList.DuplicateActivityException e) {
            assert false : "not possible";
        }
    }

    public TestTask[] getTypicalTasks() {
        return new TestTask[]{taskCS2101, taskCS2103T, taskCS2309, taskCS3244, taskCS2105, taskCS2106, taskCS2107};
    }

    public TaskMan getTypicalTaskMan() {
        TaskMan ab = new TaskMan();
        loadTaskManWithSampleData(ab);
        return ab;
    }
}
