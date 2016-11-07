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
import seedu.taskman.model.event.*;
import seedu.taskman.model.tag.Tag;
import seedu.taskman.model.tag.UniqueTagList;
import seedu.taskman.storage.Storage;
import seedu.taskman.storage.StorageManager;

import java.time.Instant;
import java.util.*;

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
        model = new ModelManager(new TaskMan());
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

        Config.setConfigFile("./src/test/data/sandbox/LogicManagerTestBase/TypicalConfig.json");
    }

    @After
    public void teardown() {
        EventsCenter.clearSubscribers();
    }

    /**
     * Executes the command and confirms that the result message is correct.
     * Generally for commands which do not mutate the data.
     * Both the 'TaskMan' and the 'list' are expected to be empty.
     *
     * @see #assertCommandBehavior(String, String, TaskMan, List)
     */
    private void assertCommandBehavior(String inputCommand, String expectedMessage) throws Exception {
        assertCommandBehavior(inputCommand, expectedMessage, null, null);
    }

    /**
     * Executes the command and confirms that the result message is correct and
     * also confirms that the following three parts of the LogicManager object's state are as expected:
     * - the internal address book data are same as those in the {@code expectedTaskMan}
     * - the backing list shown by UI matches the expected list here in the method
     * - {@code expectedTaskMan} was saved to the storage file.
     */
    private void assertCommandBehavior(String inputCommand, String expectedMessage,
                                       TaskMan expectedTaskMan,
                                       List<Activity> activityList) throws Exception {

        // Execute the command
        CommandResult result = logic.execute(inputCommand);

        // Confirm the ui display elements should contain the right data
        assertEquals(expectedMessage, result.feedbackToUser);
        // Add assert for the expected list versus actual list on the UI here, List<Activity> is placeholder data type

        // Confirm the state of data (saved and in-memory) is as expected
        assertEquals(expectedTaskMan, model.getTaskMan());
        assertEquals(expectedTaskMan, latestSavedTaskMan);
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
     * - the internal TaskMan data are same as those in the {@code expectedTaskMan} <br>
     * - the backing list shown by UI matches the {@code shownList} <br>
     * - {@code expectedTaskMan} was saved to the storage file. <br>
     *
     * @return Result of executed command
     */
    protected CommandResult assertCommandStateChange(String inputCommand, ReadOnlyTaskMan expectedTaskMan) throws Exception {
        //Execute the command
        CommandResult result = logic.execute(inputCommand);

        //Confirm the state of data (saved and in-memory) is as expected
        ReadOnlyTaskMan actualTaskMan = model.getTaskMan();
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
    protected static class TestDataHelper {

        public final String STRING_RANDOM = "random";

        protected List<Activity> tasksToActivity(List<Task> tasks) {
            ArrayList<Activity> activities = new ArrayList<>();
            for (Task task : tasks) {
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
        protected Task generateFullTask(int seed) throws Exception {
            return new Task(
                    new Title("Task " + seed),
                    new UniqueTagList(new Tag("tag" + Math.abs(seed)), new Tag("tag" + Math.abs(seed + 1))),
                    new Deadline((seed + 1) + " Dec " + seed % 12 + "am"),
                    new Schedule(seed + " nov " + (seed % 12 + 1) + "am" +
                            ", " + seed + " nov " + ((seed + 1) % 12 + 1) + "pm")
            );
        }

        /**
         * Generates a valid task using the given seed.
         * Running this function with the same parameter values guarantees the returned task will have the same state.
         * Each unique seed will generate a unique Task object.
         *
         * @param seed used to generate the task data field values
         */
        protected Task generateFloatingTask(int seed) throws Exception {
            return new Task(
                    new Title("Floating " + seed),
                    new UniqueTagList(new Tag("tag" + Math.abs(seed)), new Tag("tag" + Math.abs(seed + 1))),
                    null,
                    null
            );
        }

        /**
         * Generates a valid event using the given seed.
         * Running this function with the same parameter values guarantees the returned task will have the same state.
         * Each unique seed will generate a unique Event object.
         *
         * @param seed used to generate the task data field values
         */
        protected Event generateFullEvent(int seed) throws Exception {
            return new Event(
                    new Title("Event " + seed),
                    new UniqueTagList(new Tag("tag" + Math.abs(seed)), new Tag("tag" + Math.abs(seed + 1))),
                    new Schedule(seed + " nov " + (seed % 12 + 1) + "am" +
                            ", " + seed + " nov " + ((seed + 1) % 12 + 1) + "pm")
            );
        }

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        protected String generateAddCommand(Task task) {
            StringBuilder command = new StringBuilder();

            command.append("add ");
            command.append(task.getTitle().toString());

            if (task.getDeadline().isPresent()) {
                command.append(" d/").append(task.getDeadline().get().toFormalString());
            }
            if (task.getSchedule().isPresent()) {
                command.append(" s/").append(task.getSchedule().get().toFormalString());
            }

            UniqueTagList tags = task.getTags();
            for (Tag t : tags) {
                command.append(" t/").append(t.tagName);
            }

            return command.toString();
        }

        protected String generateAddCommandWithOnlyTaskTitle(String title) throws Exception {
            return generateAddCommand(generateTaskWithOnlyTitle(title)) ;
        }

        protected String generateAddECommand(Event event) {
            StringBuilder command = new StringBuilder();

            command.append("adde ");
            command.append(event.getTitle().toString());

            if (event.getSchedule().isPresent()) {
                command.append(" s/").append(event.getSchedule().get().toFormalString());
            }

            UniqueTagList tags = event.getTags();
            for (Tag t : tags) {
                command.append(" t/").append(t.tagName);
            }

            return command.toString();
        }

        protected String generateEditCommand(Model model, Activity.PanelType panel,  int targetIndex, Title title,
                                   Deadline deadline, Schedule schedule, UniqueTagList tags) {
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
        protected TaskMan generateTaskMan(int numGenerated) throws Exception {
            TaskMan taskMan = new TaskMan();
            addToTaskMan(taskMan, numGenerated);
            return taskMan;
        }

        /**
         * Generates an TaskMan based on the list of Tasks given.
         */
        protected TaskMan generateTaskMan(List<Task> tasks) throws Exception {
            TaskMan taskMan = new TaskMan();
            addToTaskMan(taskMan, tasks);
            return taskMan;
        }

        /**
         * Adds auto-generated Task objects to the given TaskMan
         *
         * @param taskMan The TaskMan to which the Tasks will be added
         */
        protected void addToTaskMan(TaskMan taskMan, int numGenerated) throws Exception {
            addToTaskMan(taskMan, generateFullTaskList(numGenerated));
        }

        /**
         * Adds the given list of Tasks to the given TaskMan
         */
        protected void addToTaskMan(TaskMan taskMan, List<Task> tasksToAdd) throws Exception {
            for (Task p : tasksToAdd) {
                taskMan.addActivity(p);
            }
        }

        /**
         * Adds auto-generated Events objects to the given TaskMan
         *
         * @param taskMan The TaskMan to which the Events will be added
         */
        protected void addEventsToTaskMan(TaskMan taskMan, int numGenerated) throws Exception {
            addEventsToTaskMan(taskMan, generateFullEventList(numGenerated));
        }

        /**
         * Adds the given list of Events to the given TaskMan
         */
        protected void addEventsToTaskMan(TaskMan taskMan, List<Event> eventsToAdd) throws Exception {
            for (Event p : eventsToAdd) {
                taskMan.addActivity(p);
            }
        }

        /**
         * Adds auto-generated Task objects to the given model
         *
         * @param model The model to which the Tasks will be added
         */
        protected void addToModel(Model model, int numGenerated) throws Exception {
            addToModel(model, generateFullTaskList(numGenerated));
        }

        /**
         * Adds the given list of Tasks to the given model
         */
        protected void addToModel(Model model, List<? extends Event> tasksToAdd) throws Exception {
            for (Event p : tasksToAdd) {
                model.addActivity(p);
            }
        }

        /**
         * Adds auto-generated Event objects to the given model
         *
         * @param model The model to which the Events will be added
         */
        protected void addEventsToModel(Model model, int numGenerated) throws Exception {
            addToModel(model, generateFullEventList(numGenerated));
        }

        /**
         * Generates a list of Tasks based on the flags.
         * Each task has all its fields filled.
         */
        protected List<Task> generateFullTaskList(int numGenerated) throws Exception {
            List<Task> tasks = new ArrayList<>();
            for (int i = 1; i <= numGenerated; i++) {
                tasks.add(generateFullTask(i));
            }
            return tasks;
        }

        /**
         * Generates a list of Events based on the flags.
         * Each event has all its fields filled.
         */
        protected List<Event> generateFullEventList(int numGenerated) throws Exception {
            List<Event> events = new ArrayList<>();
            for (int i = 1; i <= numGenerated; i++) {
                events.add(generateFullEvent(i));
            }
            return events;
        }

        protected ArrayList<Task> generateTaskList(Task... tasks) {
            return new ArrayList<>(Arrays.asList(tasks));
        }

        /**
         * Generates a Task object with given title. Other fields will have some dummy values.
         */
        protected Task generateTaskWithAllFields(String title) throws Exception {
            return new Task(
                    new Title(title),
                    new UniqueTagList(new Tag("t1"), new Tag("t2")),
                    new Deadline("07/05/2016 6pm"),
                    new Schedule("02/05/2016 5pm, 05/05/2016 5pm")
            );
        }

        protected Task generateTaskWithOnlyDeadline(String title) throws Exception {
            return new Task(
                    new Title(title),
                    new UniqueTagList(),
                    new Deadline("in 4 days"),
                    null
            );
        }

        protected Task generateTaskWithOnlySchedule(String title) throws Exception {
            return new Task(
                    new Title(title),
                    new UniqueTagList(),
                    null,
                    new Schedule("02/05/2016 5pm, 05/05/2016 5pm")
            );
        }

        protected Task generateTaskWithOnlyTitle(String title) throws Exception {
            return new Task(
                    new Title(title),
                    new UniqueTagList(),
                    null,
                    null
            );
        }
    }
}
