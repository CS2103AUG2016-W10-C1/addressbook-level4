package seedu.taskman.logic.parser;

import seedu.taskman.commons.exceptions.IllegalValueException;
import seedu.taskman.commons.util.StringUtil;
import seedu.taskman.logic.commands.*;
import seedu.taskman.model.Model;
import seedu.taskman.model.Model.FilterMode;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static seedu.taskman.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.taskman.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

/**
 * Parses user input.
 * // todo: rename to CommandParser when refactoring
 */
public class CommandParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    private static final Pattern TASK_INDEX_ARGS_FORMAT = Pattern.compile(Argument.TARGET_INDEX.pattern);

    private enum ListFlag{
        LIST_EVENT("e/", FilterMode.EVENT_ONLY),
        LIST_ALL("all/", FilterMode.ALL);
        
        public final String flag;
        public final FilterMode filterMode;
        
        ListFlag(String flag, FilterMode filterMode){
            this.flag = flag;
            this.filterMode = filterMode;
        }
        
        public static String get_Pattern(){
            ListFlag[] values = ListFlag.values();
            if(values.length == 0){
                return "";
            }
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i< values.length; i++){
                if(i != 0){
                    builder.append('|');
                }
                builder.append("(?:");
                builder.append(values[i].flag);
                builder.append(")");
            }
            return builder.toString();
        }
    }
    
    private static final Pattern LIST_ARGS_FORMAT =
            Pattern.compile("(?<filter>" + ListFlag.get_Pattern() + ")?" +
                    "(?<keywords>(?:\\s*[^/]+)*?)??(?<tagArguments>(?:\\s*t/[^/]+)*)?"); // one or more keywords separated by whitespace

    private enum Argument{
        TARGET_INDEX("(?<targetIndex>.+)"),
        TITLE("(?<title>[^/]+)"),
        DEADLINE("(?:\\s+d/(?<deadline>[^/]+))?"),
        SCHEDULE("(?:\\s+s/(?<schedule>[^/]+))?"),
        STATUS("(?:\\s+c/(?<status>[^/]+))?"),
        FREQUENCY("(?:\\s+f/(?<frequency>[^/]+))?"),
        TAG("(?<tagArguments>(?:\\s*t/[^/]+)*)?");
        
        private final String pattern;
        
        Argument(String pattern){
            this.pattern = pattern;
        }
        
        @Override
        public String toString(){
            return pattern;
        }
    }
    
    // todo: all fields currently compulsory
    private static final Pattern TASK_ADD_ARGS_FORMAT = // '/' forward slashes are reserved for delimiter prefixes
            Pattern.compile("" + Argument.TITLE
                    + Argument.DEADLINE
                    + Argument.SCHEDULE
                    + Argument.FREQUENCY
                    + Argument.TAG); // variable number of tags

    // todo: all fields currently compulsory
    private static final Pattern TASK_EDIT_ARGS_FORMAT = // '/' forward slashes are reserved for delimiter prefixes
            Pattern.compile("" + Argument.TARGET_INDEX
                    + Argument.TITLE
                    + Argument.DEADLINE
                    + Argument.STATUS
                    + Argument.SCHEDULE
                    + Argument.FREQUENCY
                    + Argument.TAG); // variable number of tags

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

            case AddCommand.COMMAND_WORD:
                return prepareAdd(arguments);

            case EditCommand.COMMAND_WORD:
                return prepareEdit(arguments);

            case SelectCommand.COMMAND_WORD:
                return prepareSelect(arguments);

            case DeleteCommand.COMMAND_WORD:
                return prepareDelete(arguments);

            case ClearCommand.COMMAND_WORD:
                return new ClearCommand();

            case ListCommand.COMMAND_WORD:
                return prepareList(arguments);

            case ExitCommand.COMMAND_WORD:
                return new ExitCommand();

            case HelpCommand.COMMAND_WORD:
                return new HelpCommand();

            default:
                return new IncorrectCommand(MESSAGE_UNKNOWN_COMMAND);
        }
    }

    /**
     * Parses arguments in the context of the add task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareAdd(String args){
        final Matcher matcher = TASK_ADD_ARGS_FORMAT.matcher(args.trim());
        // Validate arg string format
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
        try {
            return new AddCommand(
                    matcher.group("title"),
                    matcher.group("deadline"),
                    matcher.group("schedule"),
                    matcher.group("frequency"),
                    getTagsFromArgs(matcher.group("tagArguments"))
            );
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }

    /**
     * Extracts the new task's tags from the add command's tag arguments string.
     * Merges duplicate tag strings.
     */
    private static Set<String> getTagsFromArgs(String tagArguments) throws IllegalValueException {
        // no tags
        if (tagArguments.isEmpty()) {
            return Collections.emptySet();
        }
        // replace first delimiter prefix, then split
        final Collection<String> tagStrings = Arrays.asList(tagArguments.replaceFirst("t/", "").split(" t/"));
        return new HashSet<>(tagStrings);
    }

    private Command prepareEdit(String args) {
        final Matcher matcher = TASK_EDIT_ARGS_FORMAT.matcher(args.trim());

        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }

        String indexString = matcher.group("targetIndex").trim();
        int index = Integer.parseInt(indexString);

        try {
            return new EditCommand(
                    index,
                    matcher.group("title"),
                    matcher.group("deadline"),
                    matcher.group("status"),
                    matcher.group("schedule"),
                    matcher.group("frequency"),
                    getTagsFromArgs(matcher.group("tagArguments"))
            );
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }

    /**
     * Parses arguments in the context of the delete task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareDelete(String args) {

        Optional<Integer> index = parseIndex(args);
        if(!index.isPresent()){
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }

        return new DeleteCommand(index.get());
    }

    /**
     * Parses arguments in the context of the select task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareSelect(String args) {
        Optional<Integer> index = parseIndex(args);
        if(!index.isPresent()){
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SelectCommand.MESSAGE_USAGE));
        }

        return new SelectCommand(index.get());
    }

    /**
     * Returns the specified index in the {@code command} IF a positive unsigned integer is given as the index.
     *   Returns an {@code Optional.empty()} otherwise.
     */
    private Optional<Integer> parseIndex(String command) {
        final Matcher matcher = TASK_INDEX_ARGS_FORMAT.matcher(command.trim());
        if (!matcher.matches()) {
            return Optional.empty();
        }

        String index = matcher.group("targetIndex");
        if(!StringUtil.isUnsignedInteger(index)){
            return Optional.empty();
        }
        return Optional.of(Integer.parseInt(index));

    }

    /**
     * Parses arguments in the context of the list task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareList(String args) {
        final String trimmedArgs = args.trim();
        final Matcher matcher = LIST_ARGS_FORMAT.matcher(trimmedArgs);
        
        if (trimmedArgs.isEmpty()) {
            final Set<String> keywordSet = new HashSet<>();
            return new ListCommand(keywordSet);
        } else if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ListCommand.MESSAGE_USAGE));
        } else {
            //filter
            final String filter = matcher.group("filter");
            Model.FilterMode filterMode = Model.FilterMode.TASK_ONLY;
            for(ListFlag listFlag: ListFlag.values()){
                if(listFlag.flag.equals(filter)){
                    filterMode = listFlag.filterMode;
                }
            }

            // keywords delimited by whitespace
            Set<String> keywordSet = Collections.EMPTY_SET;
            if(matcher.group("keywords") != null){
                String[] keywords = matcher.group("keywords").split("\\s+");
                keywordSet = new HashSet<>(Arrays.asList(keywords));
            }

            Set<String> tagSet = Collections.EMPTY_SET;
            try {
                if(matcher.group("tagArguments") != null){
                    tagSet = getTagsFromArgs(matcher.group("tagArguments"));
                }
                return new ListCommand(filterMode, keywordSet, tagSet);
            } catch (IllegalValueException ive) {
                return new IncorrectCommand(ive.getMessage());
            }
        }
    }

}