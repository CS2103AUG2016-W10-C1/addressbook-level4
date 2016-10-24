package seedu.taskman.logic;

import javafx.collections.ObservableList;
import seedu.taskman.commons.core.ComponentManager;
import seedu.taskman.commons.core.LogsCenter;
import seedu.taskman.logic.commands.Command;
import seedu.taskman.logic.commands.CommandHistory;
import seedu.taskman.logic.commands.CommandResult;
import seedu.taskman.logic.parser.CommandParser;
import seedu.taskman.model.Model;
import seedu.taskman.model.event.Activity;
import seedu.taskman.storage.Storage;

import java.util.Stack;
import java.util.logging.Logger;

/**
 * The main LogicManager of the app.
 */
public class LogicManager extends ComponentManager implements Logic {
    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final CommandParser commandParser;
    private final Storage storage;
    private final Stack<CommandHistory> history;

    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.commandParser = new CommandParser();
        this.storage = storage;
        this.history = new Stack<>();
    }

    @Override
    public CommandResult execute(String commandText) {
        logger.info("----------------[USER COMMAND][" + commandText + "]");
        Command command = commandParser.parseCommand(commandText);
        command.setData(model, storage, history);
        CommandResult result = command.execute();

        if (result.succeeded) {
            history.push(new CommandHistory(commandText, result.feedbackToUser));
            // && is something that can be saved
            // save in historyStack stack
        }

        return result;
    }

    @Override
    public ObservableList<Activity> getFilteredActivityList() {
        return model.getFilteredActivityList();
    }
}
