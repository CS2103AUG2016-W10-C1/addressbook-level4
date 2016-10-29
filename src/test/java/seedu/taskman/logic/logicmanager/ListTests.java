package seedu.taskman.logic.logicmanager;

import org.junit.Test;
import seedu.taskman.model.TaskMan;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
//@@author A0140136W
public class ListTests extends LogicManagerTestBase {

    @Test
    public void execute_list_emptyArgsFormat() throws Exception {
        assertCommandNoStateChange("list ");
    }

    @Test
    public void execute_listFilteredDeadline_correctTasks() throws Exception {
        // setup expected
        LogicManagerTestBase.TestDataHelper helper = new TestDataHelper();
        ArrayList<Task> targetList = helper.generateTaskList(
                helper.generateTaskWithOnlyDeadline("show1"),
                helper.generateTaskWithOnlyDeadline("show2"),
                helper.generateTaskWithAllFields("show3")
        );
        ArrayList<Task> fullList = helper.generateTaskList(
                helper.generateTaskWithOnlySchedule("other1"),
                helper.generateTaskWithOnlySchedule("other2")
        );
        fullList.addAll(targetList);

        List<Activity> expectedList = helper.tasksToActivity(targetList);

        helper.addToModel(model, fullList);
        logic.execute("list d");
        assertTrue(model.getSortedDeadlineList().containsAll(expectedList));
        assertTrue(model.getSortedDeadlineList().size() == expectedList.size());
    }

    @Test
    public void execute_listSchedule_showsScheduledTasks() throws Exception {
        // setup expected
        LogicManagerTestBase.TestDataHelper helper = new TestDataHelper();
        ArrayList<Task> targetList = helper.generateTaskList(
                helper.generateTaskWithOnlySchedule("show1"),
                helper.generateTaskWithOnlySchedule("show2"),
                helper.generateTaskWithAllFields("show3")
        );
        ArrayList<Task> fullList = helper.generateTaskList(
                helper.generateTaskWithOnlyDeadline("other1"),
                helper.generateTaskWithOnlyDeadline("other2")
        );
        fullList.addAll(targetList);

        List<Activity> expectedList = helper.tasksToActivity(targetList);

        helper.addToModel(model, fullList);
        logic.execute("list s");
        assertTrue(model.getSortedScheduleList().containsAll(expectedList));
        assertTrue(model.getSortedScheduleList().size() == expectedList.size());
    }

    @Test
    public void execute_listFloating_showsFloatingTasks() throws Exception {
        // floating refers to no deadline

        LogicManagerTestBase.TestDataHelper helper = new TestDataHelper();
        ArrayList<Task> targetList = helper.generateTaskList(
                helper.generateTaskWithOnlySchedule("other1"),
                helper.generateTaskWithOnlySchedule("other2")
        );
        ArrayList<Task> fullList = helper.generateTaskList(
                helper.generateTaskWithOnlyDeadline("floating1"),
                helper.generateTaskWithOnlyDeadline("floating2"),
                helper.generateTaskWithAllFields("other3")
        );
        fullList.addAll(targetList);

        List<Activity> expectedList = helper.tasksToActivity(targetList);

        helper.addToModel(model, fullList);
        logic.execute("list f");
        assertTrue(model.getSortedFloatingList().containsAll(expectedList));
        assertTrue(model.getSortedFloatingList().size() == expectedList.size());
    }

    @Test
    public void execute_listDeadlineTitleFilter_onlyMatchesFullWords() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Task taskTarget1 = helper.generateTaskWithAllFields("bla bla KEY bla");
        Task taskTarget2 = helper.generateTaskWithAllFields("bla KEY bla bceofeia");
        Task other1 = helper.generateTaskWithAllFields("KE Y");
        Task other2 = helper.generateTaskWithAllFields("KEYKEYKEY sduauo");

        List<Task> fourTasks = helper.generateTaskList(other1, taskTarget1, other2, taskTarget2);
        List<Activity> expectedList = helper.tasksToActivity(helper.generateTaskList(taskTarget1, taskTarget2));

        helper.addToModel(model, fourTasks);
        logic.execute("list d KEY");

        assertTrue(model.getSortedDeadlineList().containsAll(expectedList));
        assertTrue(model.getSortedDeadlineList().size() == expectedList.size());
    }

    @Test
    public void execute_listDeadlineTitleFilter_isNotCaseSensitive() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Task target1 = helper.generateTaskWithAllFields("bla bla KEY bla");
        Task target2 = helper.generateTaskWithAllFields("bla KEY bla bceofeia");
        Task target3 = helper.generateTaskWithAllFields("key key");
        Task target4 = helper.generateTaskWithAllFields("KEy sduauo");

        List<Task> fourTasks = helper.generateTaskList(target3, target1, target4, target2);
        List<Activity> expectedList = helper.tasksToActivity(helper.generateTaskList(target1, target2, target3, target4));

        helper.addToModel(model, fourTasks);

        logic.execute("list d KEY");
        assertTrue(model.getSortedDeadlineList().containsAll(expectedList));
        assertTrue(model.getSortedDeadlineList().size() == expectedList.size());
    }

    @Test
    public void execute_listDeadline_matchesIfAnyKeywordPresent() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Task taskTarget1 = helper.generateTaskWithAllFields("bla bla KEY bla");
        Task taskTarget2 = helper.generateTaskWithAllFields("bla rAnDoM bla bceofeia");
        Task taskTarget3 = helper.generateTaskWithAllFields("key key");
        Task other1 = helper.generateTaskWithAllFields("sduauo");

        List<Task> fourTasks = helper.generateTaskList(taskTarget1, other1, taskTarget2, taskTarget3);
        List<Activity> expectedList = helper.tasksToActivity(helper.generateTaskList(taskTarget1, taskTarget2, taskTarget3));

        helper.addToModel(model, fourTasks);
        logic.execute("list d KEY rAnDoM");
        assertTrue(model.getSortedDeadlineList().containsAll(expectedList));
        assertTrue(model.getSortedDeadlineList().size() == expectedList.size());
    }


    @Test
    public void execute_listDeadline_filter_tags() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();

        // setup task man state
        helper.addToModel(model, 4);

        TaskMan expectedTaskMan = helper.generateTaskMan(4);
        List<Activity> expectedList = expectedTaskMan.getActivityList().subList(0, 2);
        assertCommandStateChange("list t/tag2",
                expectedTaskMan
        );

        assertCommandStateChange("list t/tag6",
                expectedTaskMan
        );

        expectedList = new ArrayList<>(expectedTaskMan.getActivities());
        expectedList.remove(1);
        assertCommandStateChange("list t/tag1 t/tag4",
                expectedTaskMan
        );
    }

    @Test
    public void execute_list_filter_keywords_with_tags() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        TaskMan expectedTaskMan = helper.generateTaskMan(5);

        // setup task man state
        helper.addToModel(model, 5);

        List<Activity> expectedList = new ArrayList<>();
        expectedList.add(new Activity(helper.generateFullTask(1)));
        expectedList.add(new Activity(helper.generateFullTask(5)));
        // TODO: This passes and fails randomly
        assertCommandStateChange("list 1 5 t/tag2 t/tag6",
                expectedTaskMan
        );
    }


}