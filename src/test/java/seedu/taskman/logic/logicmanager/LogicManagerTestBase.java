package seedu.taskman.logic.logicmanager;

import com.google.common.eventbus.Subscribe;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import seedu.taskman.commons.core.EventsCenter;
import seedu.taskman.commons.core.config.Config;
import seedu.taskman.commons.events.model.TaskManChangedEvent;
import seedu.taskman.commons.events.ui.JumpToListRequestEvent;
import seedu.taskman.commons.events.ui.ShowHelpRequestEvent;
import seedu.taskman.logic.Logic;
import seedu.taskman.logic.LogicManager;
import seedu.taskman.logic.commands.CommandHistory;
import seedu.taskman.logic.commands.CommandResult;
import seedu.taskman.logic.commands.EditCommand;
import seedu.taskman.logic.parser.DateTimeParser;
import seedu.taskman.model.Model;
import seedu.taskman.model.ModelManager;
import seedu.taskman.model.ReadOnlyTaskMan;
import seedu.taskman.model.TaskMan;
import seedu.taskman.model.UserPrefs;
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

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import static org.junit.Assert.assertEquals;

public abstract class LogicManagerTestBase {

    /**
     * See https://github.com/junit-team/junit4/wiki/rules#temporaryfolder-rule
     */
    @Rule
    public TemporaryFolder saveFolder = new TemporaryFolder();

    protected Model model;
    protected Storage storage;
    protected Logic logic;
    protected Deque<CommandHistory> historyDeque;

    //These are for checking the correctness of the events raised
    protected ReadOnlyTaskMan latestSavedTaskMan;
    protected boolean helpShown;
    protected int targetedJumpIndex;
    protected Activity.PanelType targetedPanelType;


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
        targetedPanelType = je.panelType;
    }

    @Before
    public void setup() {
        model = new ModelManager(new TaskMan(), UserPrefs.getUserPrefsForNonGuiTest());
        historyDeque = new ArrayDeque<>(LogicManager.HISTORY_SIZE);
        String tempTaskManFile = saveFolder.getRoot().getPath() + "TempTaskMan.xml";
        String tempPreferencesFile = saveFolder.getRoot().getPath() + "TempPreferences.json";
        storage = new StorageManager(tempTaskManFile, tempPreferencesFile);
        logic = LogicManager.generateForTest(model, storage, historyDeque);
        EventsCenter.getInstance().registerHandler(this);

        latestSavedTaskMan = new TaskMan(model.getTaskMan()); // last saved assumed to be up to date before.
        helpShown = false;
        targetedJumpIndex = -1; // non yet
        targetedPanelType = null;

        Config.setConfigFile(Config.DEFAULT_CONFIG_FILE);
    }

    @After
    public void teardown() {
        EventsCenter.clearSubscribers();
    }

    /**
     * Executes the command and confirms that no state has changed in TaskMan
     */
    protected CommandResult assertCommandNoStateChange(String inputCommand) throws Exception {
        return assertCommandStateChange(inputCommand,
                new TaskMan(model.getTaskMan())
        );
    }

    /**
     * Executes the command and confirms the following three parts of the LogicManager object's state are as expected:<br>
     *      - the internal task man data are same as those in the {@code expectedTaskMan} <br>
     *      - the backing list shown by UI matches the {@code shownList} <br>
     *      - {@code expectedTaskMan} was saved to the storage file. <br>
     *
     * @return Result of executed command
     */
    protected CommandResult assertCommandStateChange(String inputCommand, ReadOnlyTaskMan expectedTaskMan) throws Exception {
        //Execute the command
        CommandResult result = logic.execute(inputCommand);

        //Confirm the state of data (saved and in-memory) is as expected
        ReadOnlyTaskMan actualTaskMan = model.getTaskMan();
        System.out.println(actualTaskMan.getActivityList().get(0).equals(expectedTaskMan.getActivityList().get(0)));
        assertEquals(expectedTaskMan, actualTaskMan);
        assertEquals(expectedTaskMan, latestSavedTaskMan);

        return result;
    }

    /**
     * Confirms the 'invalid argument index number behaviour' for the given command
     * targeting a single task in the shown list, using visible index.
     *
     * @param commandWord to test assuming it targets a single task in the last shown list based on visible index.
     */
    protected void assertIncorrectIndexFormatBehaviorForCommand(String commandWord) throws Exception {
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
    protected void assertIndexNotFoundBehaviorForCommand(String commandWord) throws Exception {
        TestDataHelper helper = new TestDataHelper();
        List<Task> taskList = helper.generateFullTaskList(2);

        // set TaskMan state to 2 tasks
        model.resetData(new TaskMan());
        for (Task p : taskList) {
            model.addActivity(p);
        }

        assertCommandStateChange(commandWord + " 3", model.getTaskMan());
    }

    /**
     * A utility class to generate test data.
     */
    static class TestDataHelper {

        public final String STRING_RANDOM = "random";

        List<Activity> tasksToActivity(List<Task> tasks) {
            ArrayList<Activity> activities = new ArrayList<>();
            for(Task task : tasks) {
                activities.add(new Activity(task));
            }
            return activities;
        }

        /**
         * Generates a valid task using the given seed.
         * Running this function with the same parameter values guarantees the returned task will have the same state.
         * Each unique seed will generate a unique Task object.
         *
         * @param seed used to generate the task data field values
         */
        Task generateFullTask(int seed) throws Exception {
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
                command.append(" d/"). append(task.getDeadline().get().toFormalString());
            }
            if (task.getFrequency().isPresent()) {
                throw new AssertionError("Frequency is not supported yet");
            }
            if (task.getSchedule().isPresent()) {
                command.append(" s/").append(task.getSchedule().get().toFormalString());
            }

            UniqueTagList tags = task.getTags();
            for(Tag t: tags) {
                command.append(" t/").append(t.tagName);
            }

            return command.toString();
        }

        String generateEditCommand(Model model, Activity.PanelType panel,  int targetIndex, Title title, Deadline deadline, Schedule schedule,
                                   Frequency frequency, UniqueTagList tags) {
            Activity task = model.getActivityListForPanelType(panel).get(targetIndex);
            StringBuilder command = new StringBuilder();

            command.append(EditCommand.COMMAND_WORD);
            command.append(" " + panel.toString());
            command.append(targetIndex);

            if (title != null) {
                command.append(" " + title.toString());
            }

            if (deadline != null) {
                Instant instant = Instant.ofEpochSecond(deadline.epochSecond);
                command.append(" d/").
                        append(instant.toString());
            }

            if (task.getFrequency().isPresent()) {
                throw new AssertionError("Frequency is not supported yet");
            }

            if (schedule != null) {
                String start = DateTimeParser.epochSecondToShortDateTime(schedule.startEpochSecond);
                String end = DateTimeParser.epochSecondToShortDateTime(schedule.endEpochSecond);
                command.append(" s/").
                        append(start).
                        append(" to ").
                        append(end);
            }

            for (Tag t : tags) {
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
            addToTaskMan(taskMan, generateFullTaskList(numGenerated));
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
            addToModel(model, generateFullTaskList(numGenerated));
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
         * Each task has all its fields filled.
         */
        List<Task> generateFullTaskList(int numGenerated) throws Exception {
            List<Task> tasks = new ArrayList<>();
            for (int i = 1; i <= numGenerated; i++) {
                tasks.add(generateFullTask(i));
            }
            return tasks;
        }

        ArrayList<Task> generateTaskList(Task... tasks) {
            return new ArrayList<>(Arrays.asList(tasks));
        }

        /**
         * Generates a Task object with given title. Other fields will have some dummy values.
         */
        Task generateTaskWithAllFields(String title) throws Exception {
            return new Task(
                    new Title(title),
                    new UniqueTagList(new Tag("t1"), new Tag("t2")),
                    new Deadline("in 4 days"),
                    new Schedule("02/05/2016 5pm, 05/05/2016 5pm"),
                    null // new Frequency("7 days")
            );
        }

        Task generateTaskWithOnlyDeadline(String title) throws Exception {
            return new Task(
                    new Title(title),
                    new UniqueTagList(),
                    new Deadline("in 4 days"),
                    null,
                    null
            );
        }

        Task generateTaskWithOnlySchedule(String title) throws Exception {
            return new Task(
                    new Title(title),
                    new UniqueTagList(),
                    null,
                    new Schedule("02/05/2016 5pm, 05/05/2016 5pm"),
                    null
            );
        }
    }
}
