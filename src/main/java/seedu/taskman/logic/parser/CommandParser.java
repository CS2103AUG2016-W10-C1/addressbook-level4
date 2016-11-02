package seedu.taskman.logic.parser;

import seedu.taskman.logic.commands.AddCommand;
import seedu.taskman.logic.commands.ClearCommand;
import seedu.taskman.logic.commands.Command;
import seedu.taskman.logic.commands.CompleteCommand;
import seedu.taskman.logic.commands.DeleteCommand;
import seedu.taskman.logic.commands.EditCommand;
import seedu.taskman.logic.commands.ExitCommand;
import seedu.taskman.logic.commands.HelpCommand;
import seedu.taskman.logic.commands.HistoryCommand;
import seedu.taskman.logic.commands.IncorrectCommand;
import seedu.taskman.logic.commands.ListCommand;
import seedu.taskman.logic.commands.AddECommand;
import seedu.taskman.logic.commands.SelectCommand;
import seedu.taskman.logic.commands.StoragelocCommand;
import seedu.taskman.logic.commands.TagsCommand;
import seedu.taskman.logic.commands.UndoCommand;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static seedu.taskman.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.taskman.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

/**
 * Parses user input.
 */
public class CommandParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    public enum Group {
        panel("panel"),
        targetIndex("targetIndex"),
        title("title"),
        keywords("keywords"),
        deadline("deadline"),
        schedule("schedule"),
        status("status"),
        frequency("frequency"),
        tagArguments("tagArguments"),
        commandWord("commandWord"),
        arguments("arguments");

        public String name;

        Group(String s) {
            name = s;
        }
    }

    //@@author A0121299A
    public enum ArgumentPattern {
        PANEL("(?<" + Group.panel.name + ">[dsf])"),
        TARGET_INDEX("(?<" + Group.targetIndex.name + ">[0-9]+)"),
        TITLE("(?<" + Group.title.name + ">[^/]+)"),
        KEYWORDS("(?<" + Group.keywords.name + ">[^/]+)"),
        OPTIONAL_KEYWORDS("(?<" + Group.keywords.name + ">(?:\\s+[^/]+)*)?"),
        OPTIONAL_DEADLINE("(?:\\s+d/(?<" + Group.deadline.name + ">[^/]+))?"),
        OPTIONAL_SCHEDULE("(?:\\s+s/(?<" + Group.schedule.name + ">[^/]+))?"),
        OPTIONAL_STATUS("(?:\\s+c/(?<" + Group.status.name + ">[^/]+))?"),
        OPTIONAL_FREQUENCY("(?:\\s+f/(?<" + Group.frequency.name + ">[^/]+))?"),
        OPTIONAL_TAGS("(?<" + Group.tagArguments.name + ">(?:\\s*t/[^/]+)*)?"),
        FILE_PATH(".+");

        public final String pattern;

        ArgumentPattern(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public String toString() {
            return pattern;
        }
    }
    //@@author

    public CommandParser() {
    }

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     */
    public Command parseCommand(String userInput) {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group(CommandParser.Group.commandWord.name);
        final String arguments = matcher.group(CommandParser.Group.arguments.name);
        switch (commandWord) {

            case AddCommand.COMMAND_WORD:
                return AddCommand.prepareAdd(arguments);
                
            case AddECommand.COMMAND_WORD:
                return AddECommand.prepareAddE(arguments);

            case EditCommand.COMMAND_WORD:
                return EditCommand.prepareEdit(arguments);

            case ListCommand.COMMAND_WORD:
                return ListCommand.prepareList(arguments);
                
            case TagsCommand.COMMAND_WORD:
                return new TagsCommand();

            case CompleteCommand.COMMAND_WORD:
                return CompleteCommand.prepareComplete(arguments);

            case SelectCommand.COMMAND_WORD:
                return SelectCommand.prepareSelect(arguments);

            case UndoCommand.COMMAND_WORD:
                return UndoCommand.prepareUndo(arguments);

            case HistoryCommand.COMMAND_WORD:
                return new HistoryCommand();

            case DeleteCommand.COMMAND_WORD:
                return DeleteCommand.prepareDelete(arguments);

            case StoragelocCommand.COMMAND_WORD:
                return StoragelocCommand.prepareStorageloc(arguments);

            case ClearCommand.COMMAND_WORD:
                return new ClearCommand();

            case ExitCommand.COMMAND_WORD:
                return new ExitCommand();

            case HelpCommand.COMMAND_WORD:
                return new HelpCommand();

            default:
                return new IncorrectCommand(MESSAGE_UNKNOWN_COMMAND);
        }
    }
}