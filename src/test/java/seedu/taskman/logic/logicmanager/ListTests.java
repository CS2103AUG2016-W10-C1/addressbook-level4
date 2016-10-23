package seedu.taskman.logic.logicmanager;

import org.junit.Test;
import seedu.taskman.model.TaskMan;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListTests extends LogicManagerTestBase {

    @Test
    public void execute_list_emptyArgsFormat() throws Exception {
        assertCommandNoStateChange("list ");
    }

    @Test
    public void execute_list_showsDeadlineTasks() throws Exception {
        // setup expected
        LogicManagerTestBase.TestDataHelper helper = new TestDataHelper();
        TaskMan expectedTaskMan = helper.generateTaskMan(2);
        List<? extends Activity> expectedList = expectedTaskMan.getActivityList();

        // setup actual
        helper.addToModel(model, 2);

        assertCommandStateChange("list",
                expectedTaskMan,
                expectedList);
    }

    @Test
    public void execute_list_filter_all() throws Exception{
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        TaskMan expectedTaskMan = helper.generateTaskMan(2);
        List<? extends Activity> expectedList = expectedTaskMan.getActivityList();

        // setup task man state
        helper.addToModel(model, 2);

        assertCommandStateChange("list all/",
                expectedTaskMan,
                expectedList);
    }

    @Test
    public void execute_list_onlyMatchesFullWordsInTitles() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Task taskTarget1 = helper.generateTaskWithTitle("bla bla KEY bla");
        Task taskTarget2 = helper.generateTaskWithTitle("bla KEY bla bceofeia");
        Task other1 = helper.generateTaskWithTitle("KE Y");
        Task other2 = helper.generateTaskWithTitle("KEYKEYKEY sduauo");

        List<Task> fourTasks = helper.generateTaskList(other1, taskTarget1, other2, taskTarget2);
        TaskMan expectedTaskMan = helper.generateTaskMan(fourTasks);
        Activity[] toBeListed = {new Activity(taskTarget1), new Activity(taskTarget2)};
        List<Activity> expectedList = Arrays.asList(toBeListed);

        helper.addToModel(model, fourTasks);
        assertCommandStateChange("list all/ KEY",
                expectedTaskMan,
                expectedList);
    }

    @Test
    public void execute_list_isNotCaseSensitive() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Task target1 = helper.generateTaskWithTitle("bla bla KEY bla");
        Task target2 = helper.generateTaskWithTitle("bla KEY bla bceofeia");
        Task target3 = helper.generateTaskWithTitle("key key");
        Task target4 = helper.generateTaskWithTitle("KEy sduauo");

        List<Task> fourTasks = helper.generateTaskList(target3, target1, target4, target2);
        TaskMan expectedTaskMan = helper.generateTaskMan(fourTasks);
        Activity[] toBeListed = {new Activity(target3), new Activity(target1), new Activity(target4), new Activity(target2)};
        List<Activity> expectedList = Arrays.asList(toBeListed);

        helper.addToModel(model, fourTasks);
        assertCommandStateChange("list all/ KEY",
                expectedTaskMan,
                expectedList);
    }

    @Test
    public void execute_list_matchesIfAnyKeywordPresent() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Task taskTarget1 = helper.generateTaskWithTitle("bla bla KEY bla");
        Task taskTarget2 = helper.generateTaskWithTitle("bla rAnDoM bla bceofeia");
        Task taskTarget3 = helper.generateTaskWithTitle("key key");
        Task other1 = helper.generateTaskWithTitle("sduauo");

        List<Task> fourTasks = helper.generateTaskList(taskTarget1, other1, taskTarget2, taskTarget3);
        TaskMan expectedTaskMan = helper.generateTaskMan(fourTasks);
        Activity[] toBeListed = {new Activity(taskTarget1), new Activity(taskTarget2), new Activity(taskTarget3)};
        List<Activity> expectedList = Arrays.asList(toBeListed);

        helper.addToModel(model, fourTasks);
        assertCommandStateChange("list all/ key rAnDoM",
                expectedTaskMan,
                expectedList);
    }

    // TODO: LIST: write tests for deadline filter, schedule filter, floating filter

    //@Test
    public void execute_list_filter_tags() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();

        // setup task man state
        helper.addToModel(model, 4);

        TaskMan expectedTaskMan = helper.generateTaskMan(4);
        List<Activity> expectedList = expectedTaskMan.getActivityList().subList(0, 2);
        assertCommandStateChange("list t/tag2",
                expectedTaskMan,
                expectedList);

        assertCommandStateChange("list t/tag6",
                expectedTaskMan,
                Collections.EMPTY_LIST);

        expectedList = new ArrayList<>(expectedTaskMan.getActivities());
        expectedList.remove(1);
        assertCommandStateChange("list t/tag1 t/tag4",
                expectedTaskMan,
                expectedList);
    }

    //@Test
    public void execute_list_filter_keywords_with_tags() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        TaskMan expectedTaskMan = helper.generateTaskMan(5);

        // setup task man state
        helper.addToModel(model, 5);

        List<Activity> expectedList = new ArrayList<>();
        expectedList.add(new Activity(helper.generateTask(1)));
        expectedList.add(new Activity(helper.generateTask(5)));
        // TODO: This passes and fails randomly
        assertCommandStateChange("list 1 5 t/tag2 t/tag6",
                expectedTaskMan,
                expectedList);
    }


}
