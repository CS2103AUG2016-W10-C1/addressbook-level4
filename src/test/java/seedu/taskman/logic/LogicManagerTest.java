package seedu.taskman.logic;

import com.google.common.eventbus.Subscribe;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import seedu.taskman.commons.core.EventsCenter;
import seedu.taskman.commons.core.Messages;
import seedu.taskman.commons.core.config.Config;
import seedu.taskman.commons.core.config.ConfigData;
import seedu.taskman.commons.events.model.TaskManChangedEvent;
import seedu.taskman.commons.events.ui.JumpToListRequestEvent;
import seedu.taskman.commons.events.ui.ShowHelpRequestEvent;
import seedu.taskman.commons.exceptions.DataConversionException;
import seedu.taskman.commons.util.FileUtil;
import seedu.taskman.logic.commands.CommandResult;
import seedu.taskman.logic.commands.StoragelocCommand;
import seedu.taskman.logic.parser.DateTimeParser;
import seedu.taskman.model.Model;
import seedu.taskman.model.ModelManager;
import seedu.taskman.model.ReadOnlyTaskMan;
import seedu.taskman.model.TaskMan;
import seedu.taskman.model.event.Activity;
import seedu.taskman.model.event.Deadline;
import seedu.taskman.model.event.Frequency;
import seedu.taskman.model.event.Schedule;
import seedu.taskman.model.event.Task;
import seedu.taskman.model.event.Title;
import seedu.taskman.model.tag.Tag;
import seedu.taskman.model.tag.UniqueTagList;
import seedu.taskman.storage.Storage;
import seedu.taskman.storage.StorageManager;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LogicManagerTest {

    /**
     * See https://github.com/junit-team/junit4/wiki/rules#temporaryfolder-rule
     */
    @Rule
    public TemporaryFolder saveFolder = new TemporaryFolder();

    private Model model;
    private Storage storage;
    private Logic logic;

    //These are for checking the correctness of the events raised
    private ReadOnlyTaskMan latestSavedTaskMan;
    private boolean helpShown;
    private int targetedJumpIndex;

    @Subscribe
    private void handleLocalModelChangedEvent(TaskManChangedEvent abce) {
        latestSavedTaskMan = new TaskMan(abce.data);
    }

    @Subscribe
    private void handleShowHelpRequestEvent(ShowHelpRequestEvent she) {
        helpShown = true;
    }

    @Subscribe
    private void handleJumpToListRequestEvent(JumpToListRequestEvent je) {
        targetedJumpIndex = je.targetIndex;
    }

    @Before
    public void setup() {
        model = new ModelManager();
        String tempTaskManFile = saveFolder.getRoot().getPath() + "TempTaskMan.xml";
        String tempPreferencesFile = saveFolder.getRoot().getPath() + "TempPreferences.json";
        storage = new StorageManager(tempTaskManFile, tempPreferencesFile);
        logic = new LogicManager(model, storage);
        EventsCenter.getInstance().registerHandler(this);

        latestSavedTaskMan = new TaskMan(model.getTaskMan()); // last saved assumed to be up to date before.
        helpShown = false;
        targetedJumpIndex = -1; // non yet

        Config.setConfigFile(Config.DEFAULT_CONFIG_FILE);
    }

    @After
    public void teardown() {
        EventsCenter.clearSubscribers();
    }

    /**
     * Executes the command and confirms that no state has changed in TaskMan
     */
    private CommandResult assertCommandNoStateChange(String inputCommand) throws Exception {
        return assertCommandStateChange(inputCommand, new TaskMan(), Collections.emptyList());
    }

    /**
     * Executes the command and confirms the following three parts of the LogicManager object's state are as expected:<br>
     *      - the internal task man data are same as those in the {@code expectedTaskMan} <br>
     *      - the backing list shown by UI matches the {@code shownList} <br>
     *      - {@code expectedTaskMan} was saved to the storage file. <br>
     *
     * @return Result of executed command
     */
    private CommandResult assertCommandStateChange(String inputCommand, ReadOnlyTaskMan expectedTaskMan,
                                                   List<? extends Activity> expectedShownList) throws Exception {
        //Execute the command
        CommandResult result = logic.execute(inputCommand);

        assertEquals(expectedShownList, model.getFilteredActivityList());

        //Confirm the state of data (saved and in-memory) is as expected
        assertEquals(expectedTaskMan, model.getTaskMan());
        assertEquals(expectedTaskMan, latestSavedTaskMan);

        return result;
    }

    @Test
    public void execute_invalid() throws Exception {
        String invalidCommand = "       ";
        assertCommandNoStateChange(invalidCommand);
    }

    @Test
    public void execute_unknownCommandWord() throws Exception {
        String unknownCommand = "uicfhmowqewca";
        assertCommandNoStateChange(unknownCommand);
    }

    // todo: decide
    //@Test
    public void execute_help() throws Exception {
        assertCommandNoStateChange("help");
        assertTrue(helpShown);
    }

    // todo: decide
    //@Test
    public void execute_exit() throws Exception {
        assertCommandNoStateChange("exit");
    }

    // todo: decide
    //@Test
    public void execute_clear() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        model.addActivity(helper.generateTask(1));
        model.addActivity(helper.generateTask(2));
        model.addActivity(helper.generateTask(3));

        assertCommandStateChange("clear", new TaskMan(), Collections.emptyList());
    }

    @Test
    public void execute_do_invalidArgsFormat() throws Exception {
        // no args
        assertCommandNoStateChange("do");

        // non-existent flag
        assertCommandNoStateChange("do x/");
    }

    @Test
    public void execute_do_invalidTaskData() throws Exception {
        // bad deadline
        assertCommandNoStateChange("do Valid Title d/invalid Deadline");

        // bad schedule
        assertCommandNoStateChange("do Valid Title s/invalid Schedule");

        // bad title
        assertCommandNoStateChange("do []\\[;]");
    }

    @Test
    public void execute_do_successful() throws Exception {
        // setup expectations
        TestDataHelper helper = new TestDataHelper();
        Task toBeAdded = helper.food();
        TaskMan expectedTaskMan = new TaskMan();
        expectedTaskMan.addActivity(toBeAdded);

        assertCommandStateChange(helper.generateDoCommand(toBeAdded),
                expectedTaskMan,
                expectedTaskMan.getActivityList());
    }

    @Test
    public void execute_doDuplicate_notAllowed() throws Exception {
        // setup expected
        TestDataHelper helper = new TestDataHelper();
        Task toBeAdded = helper.food();
        TaskMan expectedTaskMan = new TaskMan();
        expectedTaskMan.addActivity(toBeAdded);

        // setup actual
        model.addActivity(toBeAdded);

        // execute command and verify result
        assertCommandStateChange(
                helper.generateDoCommand(toBeAdded),
                expectedTaskMan,
                expectedTaskMan.getActivityList());

    }

    /**
     * Confirms the 'invalid argument index number behaviour' for the given command
     * targeting a single task in the shown list, using visible index.
     *
     * @param commandWord to test assuming it targets a single task in the last shown list based on visible index.
     */
    private void assertIncorrectIndexFormatBehaviorForCommand(String commandWord) throws Exception {
        assertCommandNoStateChange(commandWord); //index missing
        assertCommandNoStateChange(commandWord + " +1"); //index should be unsigned
        assertCommandNoStateChange(commandWord + " -1"); //index should be unsigned
        assertCommandNoStateChange(commandWord + " 0"); //index cannot be 0
        assertCommandNoStateChange(commandWord + " not_a_number");
    }

    /**
     * Confirms the 'invalid argument index number behaviour' for the given command
     * targeting a single task in the shown list, using visible index.
     *
     * @param commandWord to test assuming it targets a single task in the last shown list based on visible index.
     */
    private void assertIndexNotFoundBehaviorForCommand(String commandWord) throws Exception {
        TestDataHelper helper = new TestDataHelper();
        List<Task> taskList = helper.generateTaskList(2);

        // set TaskMan state to 2 tasks
        model.resetData(new TaskMan());
        for (Task p : taskList) {
            model.addActivity(p);
        }

        List<Activity> expectedList = taskList.stream().map(Activity::new).collect(Collectors.toList());
        assertCommandStateChange(commandWord + " 3", model.getTaskMan(), expectedList);
    }

    //@Test
    public void execute_selectInvalidArgsFormat_errorMessageShown() throws Exception {
        assertIncorrectIndexFormatBehaviorForCommand("select");
    }

    //@Test
    public void execute_selectIndexNotFound_errorMessageShown() throws Exception {
        assertIndexNotFoundBehaviorForCommand("select");
    }

    //@Test
    public void execute_select_jumpsToCorrectTask() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        List<Task> threeTasks = helper.generateTaskList(3);

        TaskMan expectedTaskMan = helper.generateTaskMan(threeTasks);
        helper.addToModel(model, threeTasks);

        assertCommandStateChange("select 2",
                expectedTaskMan,
                expectedTaskMan.getActivityList());
        assertEquals(1, targetedJumpIndex);
        assertEquals(model.getFilteredActivityList().get(1), new Activity(threeTasks.get(1)));
    }

    @Test
    public void execute_deleteInvalidArgsFormat_errorMessageShown() throws Exception {
        assertIncorrectIndexFormatBehaviorForCommand("delete");
    }

    @Test
    public void execute_deleteIndexNotFound_errorMessageShown() throws Exception {
        assertIndexNotFoundBehaviorForCommand("delete");
    }

    @Test
    public void execute_delete_removesCorrectTask() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        List<Task> threeTasks = helper.generateTaskList(3);

        TaskMan expectedTaskMan = helper.generateTaskMan(threeTasks);
        // TODO: overload method to accept tasks?
        expectedTaskMan.removeActivity(new Activity(threeTasks.get(1)));
        helper.addToModel(model, threeTasks);

        assertCommandStateChange("delete 2",
                expectedTaskMan,
                expectedTaskMan.getActivityList());
    }
    
    //@Test
    public void execute_completeInvalidArgsFormat_errorMessageShown() throws Exception {
        assertIncorrectIndexFormatBehaviorForCommand("complete");
    }

    //@Test
    public void execute_completeIndexNotFound_errorMessageShown() throws Exception {
        assertIndexNotFoundBehaviorForCommand("complete");
    }

    //@Test
    public void execute_complete_completesCorrectTask() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        List<Task> threeTasks = helper.generateTaskList(3);

        TaskMan expectedTaskMan = helper.generateTaskMan(threeTasks);
        // Wrap Task in Activity to complete
        expectedTaskMan.completeActivity(new Activity(threeTasks.get(1)));
        helper.addToModel(model, threeTasks);

        // Fails sometimes when generated Activity is not a Task but Event
        assertCommandStateChange("complete 2",
                expectedTaskMan,
                expectedTaskMan.getActivityList());
    }

    @Test
    public void execute_list_emptyArgsFormat() throws Exception {
        assertCommandNoStateChange("list ");
    }

    @Test
    public void execute_list_showsDeadlineTasks() throws Exception {
        // setup expected
        TestDataHelper helper = new TestDataHelper();
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


    private void assert_storage_location(String inputCommand, String expectedFeedback,
                                         String expectedPath, boolean success)
            throws IOException, DataConversionException {
        Config.resetInstance();
        CommandResult result = logic.execute(inputCommand);
        assertEquals(result.feedbackToUser, expectedFeedback);
        if (success) {
            TaskMan storageTaskMan = new TaskMan(storage.readTaskMan().get());
            assertEquals(model.getTaskMan(), storageTaskMan);
            assertEquals(storage.getTaskManFilePath(), expectedPath);
        }
        assertEquals(Config.getInstance().getTaskManFilePath(), expectedPath);
    }

    private String getStoragelocFeedback(String path, boolean success) {
        String message = success
                ? StoragelocCommand.MESSAGE_SUCCESS
                : StoragelocCommand.MESSAGE_FAILURE;
        return String.format(message, path);
    }

    private void execute_storageloc_general(int generatedTasks,
                                            String commandArgs,
                                            String expectedPath,
                                            boolean isExpectedSuccess) throws Exception {
        TestDataHelper helper = new TestDataHelper();
        helper.addToModel(model, generatedTasks);
        assert_storage_location(StoragelocCommand.COMMAND_WORD + " " + commandArgs,
                getStoragelocFeedback(expectedPath, isExpectedSuccess), expectedPath, isExpectedSuccess);
    }

    @Test
    public void execute_storageloc_default() throws Exception {
        execute_storageloc_general(3, "default",
                FileUtil.getAbsolutePath(ConfigData.DEFAULT_TASK_MAN_FILE_PATH),
                true);
    }

    @Test
    public void execute_storageloc_absolutePath() throws Exception {
        String givenPath = FileUtil.getAbsolutePath("./src/test/data/sandbox/LogicManagerTest/absolute.xml");
        execute_storageloc_general(4, givenPath, givenPath, true);
    }

    @Test
    public void execute_storageloc_relativePath() throws Exception {
        String givenPath = "./src/test/data/sandbox/LogicManagerTest/relative.xml";
        execute_storageloc_general(4, givenPath, FileUtil.getAbsolutePath(givenPath), true);
    }

    @Test
    public void execute_storageloc_invalidFileName() throws Exception {
        String invalidFile = "/<3invalidFileName.txt";
        execute_storageloc_general(2, invalidFile, ConfigData.DEFAULT_TASK_MAN_FILE_PATH, false);
    }

    @Test
    public void execute_storageloc_whitespace() throws Exception {
        assert_storage_location(StoragelocCommand.COMMAND_WORD + "     ",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, StoragelocCommand.MESSAGE_USAGE), ConfigData.DEFAULT_TASK_MAN_FILE_PATH, false);
    }

    @Test
    public void execute_storageloc_empty() throws Exception {
        assert_storage_location(StoragelocCommand.COMMAND_WORD + "",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, StoragelocCommand.MESSAGE_USAGE), ConfigData.DEFAULT_TASK_MAN_FILE_PATH, false);
    }


    // TODO: change util class to static, if it makes sense
    /**
     * A utility class to generate test data.
     */
    class TestDataHelper {

        Task food() throws Exception {
            Title title = new Title("Procure dinner");
            Deadline privateDeadline = new Deadline("7.00pm");
            Frequency frequency = null;// new Frequency("1 day");
            Schedule schedule = new Schedule("6pm, 7pm");
            Tag tag1 = new Tag("tag1");
            Tag tag2 = new Tag("tag2");
            UniqueTagList tags = new UniqueTagList(tag1, tag2);
            return new Task(title, tags, privateDeadline, schedule, frequency);
        }

        /**
         * Generates a valid task using the given seed.
         * Running this function with the same parameter values guarantees the returned task will have the same state.
         * Each unique seed will generate a unique Task object.
         *
         * @param seed used to generate the task data field values
         */
        Task generateTask(int seed) throws Exception {
            return new Task(
                    new Title("Task " + seed),
                    new UniqueTagList(new Tag("tag" + Math.abs(seed)), new Tag("tag" + Math.abs(seed + 1))),
                    new Deadline(Math.abs(seed)),
                    new Schedule(Instant.ofEpochSecond(Math.abs(seed * seed * 10000000 - 1)) +
                            ", " + Instant.ofEpochSecond(Math.abs(seed * seed * 100000000))),
                    null //new Frequency(seed + " mins")
            );
        }

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        String generateDoCommand(Task task) {
            StringBuilder command = new StringBuilder();

            command.append("do ");
            command.append(task.getTitle().toString());

            if (task.getDeadline().isPresent()) {
                Instant instant = Instant.ofEpochSecond(task.getDeadline().get().epochSecond);
                command.append(" d/").
                        append(instant.toString());
            }
            if (task.getFrequency().isPresent()) {
                throw new AssertionError("Frequency is not supported yet");
            }
            if (task.getSchedule().isPresent()) {
                String start = DateTimeParser.epochSecondToShortDateTime(task.getSchedule().get().startEpochSecond);
                String end = DateTimeParser.epochSecondToShortDateTime(task.getSchedule().get().endEpochSecond);
                command.append(" s/").
                        append(start).
                        append(" to ").
                        append(end);
            }

            UniqueTagList tags = task.getTags();
            for(Tag t: tags) {
                command.append(" t/").append(t.tagName);
            }

            return command.toString();
        }

        /**
         * Generates an TaskMan with auto-generated tasks.
         */
        TaskMan generateTaskMan(int numGenerated) throws Exception {
            TaskMan taskMan = new TaskMan();
            addToTaskMan(taskMan, numGenerated);
            return taskMan;
        }

        /**
         * Generates an TaskMan based on the list of Tasks given.
         */
        TaskMan generateTaskMan(List<Task> tasks) throws Exception {
            TaskMan taskMan = new TaskMan();
            addToTaskMan(taskMan, tasks);
            return taskMan;
        }

        /**
         * Adds auto-generated Task objects to the given TaskMan
         *
         * @param taskMan The TaskMan to which the Tasks will be added
         */
        void addToTaskMan(TaskMan taskMan, int numGenerated) throws Exception {
            addToTaskMan(taskMan, generateTaskList(numGenerated));
        }

        /**
         * Adds the given list of Tasks to the given TaskMan
         */
        void addToTaskMan(TaskMan taskMan, List<Task> tasksToAdd) throws Exception{
            for (Task p: tasksToAdd) {
                taskMan.addActivity(p);
            }
        }

        /**
         * Adds auto-generated Task objects to the given model
         *
         * @param model The model to which the Tasks will be added
         */
        void addToModel(Model model, int numGenerated) throws Exception {
            addToModel(model, generateTaskList(numGenerated));
        }

        /**
         * Adds the given list of Tasks to the given model
         */
        void addToModel(Model model, List<Task> tasksToAdd) throws Exception{
            for(Task p: tasksToAdd){
                model.addActivity(p);
            }
        }

        /**
         * Generates a list of Tasks based on the flags.
         */
        List<Task> generateTaskList(int numGenerated) throws Exception {
            List<Task> tasks = new ArrayList<>();
            for (int i = 1; i <= numGenerated; i++) {
                tasks.add(generateTask(i));
            }
            return tasks;
        }

        List<Task> generateTaskList(Task... tasks) {
            return Arrays.asList(tasks);
        }

        /**
         * Generates a Task object with given title. Other fields will have some dummy values.
         */
        Task generateTaskWithTitle(String title) throws Exception {
            return new Task(
                    new Title(title),
                    new UniqueTagList(new Tag("t1"), new Tag("t2")),
                    new Deadline("in 4 days"),
                    new Schedule("02/05/2016 5pm, 05/05/2016 5pm"),
                    null // new Frequency("7 days")
            );
        }
    }
}
