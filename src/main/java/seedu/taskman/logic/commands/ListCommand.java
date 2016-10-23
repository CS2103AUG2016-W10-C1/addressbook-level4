package seedu.taskman.logic.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static seedu.taskman.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

/**
 * Finds and lists all tasks in task man whose title contains any of the argument keywords and contains any of the given tags.
 * Keyword matching is case sensitive.
 */
public class ListCommand extends Command {

    public enum FilterMode {
        SCHEDULE_ONLY,
        DEADLINE_ONLY,
        FLOATING_ONLY,
        ALL
    }

    private enum ListFlag{
        SCHEDULE("s/", FilterMode.SCHEDULE_ONLY),
        DEADLINE("d/", FilterMode.DEADLINE_ONLY),
        FLOATING("f/", FilterMode.FLOATING_ONLY),
        ALL("all/", FilterMode.ALL);

        public final String flag;
        public final FilterMode filterMode;

        ListFlag(String flag, FilterMode filterMode){
            this.flag = flag;
            this.filterMode = filterMode;
        }

        public static String matchingRegex(){
            ListFlag[] values = ListFlag.values();
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < values.length; i++){
                if(i != 0){
                    builder.append('|');
                }
                builder.append("(?:").
                        append(values[i].flag).
                        append(")");
            }
            return builder.toString();
        }
    }

    public static final String COMMAND_WORD = "list";

    public static final Pattern LIST_ARGS_FORMAT = Pattern.compile("(?<filter>" + ListFlag.matchingRegex() + ")?" +
            "(?<keywords>(?:\\s*[^/]+)*?)??(?<tagArguments>(?:\\s*t/[^/]+)*)?"); // one or more keywords separated by whitespace

    // UG/DG
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all tasks whose titles contain any of "
            + "the specified keywords (case-sensitive) and displays them as a list with index numbers.\n"
            + "Parameters: [{d/, f/, all/}] [KEYWORDS]... [t/TAG]...\n"
            + "Example: " + COMMAND_WORD + " all/ homework t/CS2103T";

    public static final String MESSAGE_SUCCESS = "Listed all tasks";

    private final FilterMode filterMode;
    private final Set<String> keywords;
    private final Set<String> tagNames;

    public static Command prepareList(String args) {
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
            FilterMode filterMode = FilterMode.DEADLINE_ONLY;
            for(ListFlag listFlag: ListFlag.values()){
                if(listFlag.flag.equals(filter)){
                    filterMode = listFlag.filterMode;
                }
            }

            // keywords delimited by whitespace
            Set<String> keywordSet = Collections.EMPTY_SET;
            if (matcher.group("keywords") != null) {
                String[] keywords = matcher.group("keywords").split("\\s+");
                keywordSet = new HashSet<>(Arrays.asList(keywords));
            }

            Set<String> tagSet = Collections.EMPTY_SET;
            if (matcher.group("tagArguments") != null) {
                tagSet = getTagsFromArgs(matcher.group("tagArguments"));
            }
            return new ListCommand(filterMode, keywordSet, tagSet);
        }
    }

    private ListCommand(FilterMode filterMode, Set<String> keywords, Set<String> tags) {
        this.filterMode = filterMode;
        this.keywords = keywords;
        this.tagNames = tags;
    }

    private ListCommand(Set<String> keywords) {
        this(FilterMode.DEADLINE_ONLY, keywords, new HashSet<>());
    }

    @Override
    public CommandResult execute() {
        model.updateFilteredActivityList(filterMode, keywords, tagNames);
        return new CommandResult(getMessageForTaskListShownSummary(model.getFilteredActivityList().size()));
    }

}
