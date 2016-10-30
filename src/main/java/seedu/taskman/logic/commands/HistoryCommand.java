package seedu.taskman.logic.commands;

import java.util.ArrayList;
import java.util.List;

//@@author A0139019E
public class HistoryCommand extends Command {

    public static final int CAPACITY_LOW_BOUND_HISTORY_COMMAND = 0; // cannot be negative
    public static final int CAPACITY_UPP_BOUND_HISTORY_COMMAND = 10;

    public static final String COMMAND_WORD = "history";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows the "
            + CAPACITY_UPP_BOUND_HISTORY_COMMAND
            + " most recently executed commands the change the data.\n"
            + "Example: " + COMMAND_WORD;

    public static final String HISTORY_STRING_HEADER = "Most Recent History:\n";
    public static final String HISTORY_STRING_EMPTY_PLACEHOLDER = "\tNIL";
    public static final String HISTORY_STRING_BULLET_POINT = "\t%d. ";
    public static final String NEWLINE = "\n";
    public static final String TAB = "\t";

    public HistoryCommand() {
        super(false);
    }

    @Override
    public CommandResult execute() {

        StringBuilder builder = new StringBuilder(HISTORY_STRING_HEADER);
        int commandCount = 1;

        List<CommandHistory> historyForExtraction = new ArrayList<>(historyDeque);

        for (CommandHistory history : historyForExtraction) {
            builder.append(String.format(HISTORY_STRING_BULLET_POINT, commandCount++))
                    .append(history.inputCommand)
                    .append(NEWLINE)
                    .append(TAB)
                    .append(history.resultMessage)
                    .append(NEWLINE);
        }

        if (commandCount == 1) {
            builder.append(HISTORY_STRING_EMPTY_PLACEHOLDER);
        }
        return new CommandResult(builder.toString().trim(), true);
    }

}
