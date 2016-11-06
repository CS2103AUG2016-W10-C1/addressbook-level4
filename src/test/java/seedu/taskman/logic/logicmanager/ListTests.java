package seedu.taskman.logic.logicmanager;

import org.junit.Test;
import seedu.taskman.model.ReadOnlyTaskMan;
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

        // setup TaskMan state
        helper.addToModel(model, 4);

        TaskMan expectedTaskMan = helper.generateTaskMan(4);
        List<Activity> expectedList = expectedTaskMan.getActivityList().subList(0, 2);
        assertCommandStateChange("list d t/tag2",
                expectedTaskMan
        );
        assertTrue(model.getSortedDeadlineList().containsAll(expectedList));
        assertTrue(model.getSortedDeadlineList().size() == expectedList.size());

        assertCommandStateChange("list d t/tag6",
                expectedTaskMan
        );
        assertTrue(model.getSortedDeadlineList().isEmpty());

        expectedList = new ArrayList<>(expectedTaskMan.getActivities());
        expectedList.remove(1);
        assertCommandStateChange("list d t/tag1 t/tag4",
                expectedTaskMan
        );
        assertTrue(model.getSortedDeadlineList().containsAll(expectedList));
        assertTrue(model.getSortedDeadlineList().size() == expectedList.size());
    }

    @Test
    public void execute_listDeadline_filterKeywordsWithTags() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        TaskMan expectedTaskMan = helper.generateTaskMan(5);

        // setup TaskMan state
        helper.addToModel(model, 5);

        List<Activity> expectedList = new ArrayList<>();
        expectedList.add(new Activity(helper.generateFullTask(1)));
        expectedList.add(new Activity(helper.generateFullTask(5)));
        assertCommandStateChange("list d 1 5 t/tag2 t/tag6",
                expectedTaskMan
        );
        assertTrue(model.getSortedDeadlineList().containsAll(expectedList));
        assertTrue(model.getSortedDeadlineList().size() == expectedList.size());
    }

    private ReadOnlyTaskMan filterListAllSetup() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        // setup TaskMan state
        helper.addToModel(model, 5);
        TaskMan expectedTaskMan = helper.generateTaskMan(5);

        helper.addEventsToModel(model, 3);
        helper.addEventsToTaskMan(expectedTaskMan, 3);

        List<Task> floatingTasks = helper.generateTaskList(helper.generateFloatingTask(3),
                helper.generateFloatingTask(4),
                helper.generateFloatingTask(5));
        helper.addToModel(model, floatingTasks);
        helper.addToTaskMan(expectedTaskMan, floatingTasks);

        return expectedTaskMan;
    }

    private void assert_list_allPanels(String command, ReadOnlyTaskMan expectedTaskMan,
                                       List<Activity> expectedDeadlineList,
                                       List<Activity> expectedScheduleList,
                                       List<Activity> expectedFloatingList) throws Exception {
        assertCommandStateChange(command, expectedTaskMan);
        //check deadline
        assertTrue(model.getSortedDeadlineList().containsAll(expectedDeadlineList));
        assertTrue(model.getSortedDeadlineList().size() == expectedDeadlineList.size());
        //check schedule
        assertTrue(model.getSortedScheduleList().containsAll(expectedScheduleList));
        assertTrue(model.getSortedScheduleList().size() == expectedScheduleList.size());
        //check floating
        assertTrue(model.getSortedScheduleList().containsAll(expectedScheduleList));
        assertTrue(model.getSortedScheduleList().size() == expectedScheduleList.size());

    }

    @Test
    public void execute_listAll_filterSingleTag() throws Exception {
        // setup
        ReadOnlyTaskMan expectedTaskMan = filterListAllSetup();

        TestDataHelper helper = new TestDataHelper();
        // expected lists
        // deadline list
        List<Activity> expectedDeadlineList = new ArrayList<>();
        expectedDeadlineList.add(new Activity(helper.generateFullTask(2)));
        expectedDeadlineList.add(new Activity(helper.generateFullTask(3)));
        // schedule list
        // copy items from deadline list (both have schedules)
        List<Activity> expectedScheduleList = new ArrayList<>(expectedDeadlineList);
        expectedScheduleList.add(new Activity(helper.generateFullEvent(2)));
        expectedScheduleList.add(new Activity(helper.generateFullEvent(3)));
        // floating list
        List<Activity> expectedFloatingList = new ArrayList<>();
        expectedFloatingList.add(new Activity(helper.generateFloatingTask(3)));

        // test command
        assert_list_allPanels("list t/tag3", expectedTaskMan,
                expectedDeadlineList, expectedScheduleList, expectedFloatingList);
    }

    @Test
    public void execute_listAll_filterNotExistingTag() throws Exception {
        // setup
        ReadOnlyTaskMan expectedTaskMan = filterListAllSetup();

        // expected lists
        // deadline list
        List<Activity> expectedList = new ArrayList<>();

        // test command
        assert_list_allPanels("list t/tag7", expectedTaskMan,
                expectedList, expectedList, expectedList);
    }

    @Test
    public void execute_listAll_filterMultipleTags() throws Exception {
        // setup
        ReadOnlyTaskMan expectedTaskMan = filterListAllSetup();

        TestDataHelper helper = new TestDataHelper();
        // expected lists
        // deadline list
        List<Activity> expectedDeadlineList = new ArrayList<>();
        expectedDeadlineList.add(new Activity(helper.generateFullTask(1)));
        expectedDeadlineList.add(new Activity(helper.generateFullTask(3)));
        expectedDeadlineList.add(new Activity(helper.generateFullTask(4)));
        // schedule list
        // copy items from deadline list (both have schedules)
        List<Activity> expectedScheduleList = new ArrayList<>(expectedDeadlineList);
        expectedScheduleList.add(new Activity(helper.generateFullEvent(1)));
        expectedScheduleList.add(new Activity(helper.generateFullEvent(3)));
        // floating list
        List<Activity> expectedFloatingList = new ArrayList<>();
        expectedFloatingList.add(new Activity(helper.generateFloatingTask(3)));
        expectedFloatingList.add(new Activity(helper.generateFloatingTask(4)));

        // test command
        assert_list_allPanels("list t/tag1 t/tag4", expectedTaskMan,
                expectedDeadlineList, expectedScheduleList, expectedFloatingList);
    }


}