package seedu.taskman.logic.commands;


import seedu.taskman.commons.core.EventsCenter;
import seedu.taskman.commons.events.ui.ShowHelpRequestEvent;

/**
 * Format full help instructions for every command for display.
 */
public class HelpCommand extends Command {

    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = "Show command formats.\n"
            + "Example: " + COMMAND_WORD + " (or press F1)";

    public static final String SHOWING_HELP_MESSAGE = "Opened help window.";

    public HelpCommand() {
        super(false);
    }

    @Override
    public CommandResult execute() {
        EventsCenter.getInstance().post(new ShowHelpRequestEvent());
        return new CommandResult(SHOWING_HELP_MESSAGE, true);
    }
}
