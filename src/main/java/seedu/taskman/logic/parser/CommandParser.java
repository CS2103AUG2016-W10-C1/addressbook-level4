package seedu.taskman.logic.parser;

import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.commons.util.StringUtil;
import seedu.taskman.logic.commands.*;

import java.util.*;
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

    public enum Argument{
        TARGET_INDEX("(?<targetIndex>[0-9]+)"),
        TITLE("(?<title>[^/]+)"),
        DEADLINE("(?:\\s+d/(?<deadline>[^/]+))?"),
        SCHEDULE("(?:\\s+s/(?<schedule>[^/]+))?"),
        STATUS("(?:\\s+c/(?<status>[^/]+))?"),
        FREQUENCY("(?:\\s+f/(?<frequency>[^/]+))?"),
        TAG("(?<tagArguments>(?:\\s*t/[^/]+)*)?");
        
        public final String pattern;
        
        Argument(String pattern){
            this.pattern = pattern;
        }
        
        @Override
        public String toString(){
            return pattern;
        }
    }

    public CommandParser() {}

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

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");
        switch (commandWord) {

            case DoCommand.COMMAND_WORD:
                return DoCommand.prepareDo(arguments);
                
            case MarkCommand.COMMAND_WORD:
                return MarkCommand.prepareMark(arguments);

            case EditCommand.COMMAND_WORD:
                return EditCommand.prepareEdit(arguments);

            case ListCommand.COMMAND_WORD:
                return ListCommand.prepareList(arguments);

            case CompleteCommand.COMMAND_WORD:
                return CompleteCommand.prepareComplete(arguments);

            case SelectCommand.COMMAND_WORD:
                return SelectCommand.prepareSelect(arguments);

            case DeleteCommand.COMMAND_WORD:
                return DeleteCommand.prepareDelete(arguments);

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