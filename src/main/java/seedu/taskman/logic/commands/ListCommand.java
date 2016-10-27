package seedu.taskman.logic.commands;

import seedu.taskman.logic.parser.CommandParser;
import seedu.taskman.model.event.Activity;

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
    //@@author A0121299A-unused
    /*
    private enum ListFlag{
        OPTIONAL_SCHEDULE("s/", FilterMode.SCHEDULE_ONLY),
        OPTIONAL_DEADLINE("d/", FilterMode.DEADLINE_ONLY),
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
    */
    //@@author
    public static final String COMMAND_WORD = "list";
    
    /*
    public static final Pattern SPECIFY_PANEL_ARGS_FORMAT = Pattern.compile("(?<filter>" + ListFlag.matchingRegex() + ")?" +
            "(?<keywords>(?:\\s*[^/]+)*?)??(?<tagArguments>(?:\\s*t/[^/]+)*)?"); // one or more keywords separated by whitespace
    */

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all tasks whose titles contain any of "
            + "the specified keywords (case-sensitive) and displays them as a list with index numbers.\n"
            + "Parameters: [{d/, f/, all/}] [OPTIONAL_KEYWORDS]... [t/OPTIONAL_TAGS]...\n"
            + "Example: " + COMMAND_WORD + " all/ homework t/CS2103T";

    public static final String MESSAGE_SUCCESS = "Listed all tasks";
    private static final Pattern SPECIFY_PANEL_ARGS_FORMAT =
            Pattern.compile("" + CommandParser.ArgumentPattern.PANEL
                    + CommandParser.ArgumentPattern.OPTIONAL_KEYWORDS
                    + CommandParser.ArgumentPattern.OPTIONAL_TAGS);

    private final Activity.PanelType panelType;
    private final Set<String> keywords;
    private final Set<String> tagNames;

    public static Command prepareList(String args) {
        final String trimmedArgs = args.trim();
        final Matcher matcher = SPECIFY_PANEL_ARGS_FORMAT.matcher(trimmedArgs);

        if (trimmedArgs.isEmpty()) {
            // TODO: blocked for now
            //return new ListCommand(keywordSet);
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ListCommand.MESSAGE_USAGE));

        } else if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ListCommand.MESSAGE_USAGE));
        } else {
            
            String panelTypeRaw = matcher.group("panel").trim();
            Activity.PanelType panelType = Activity.PanelType.fromString(panelTypeRaw);

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
            return new ListCommand(panelType, keywordSet, tagSet);
        }
    }

    private ListCommand(Activity.PanelType panelType, Set<String> keywords, Set<String> tags) {
        super(false);
        this.panelType = panelType;
        this.keywords = keywords;
        this.tagNames = tags;
    }

    private ListCommand(Set<String> keywords) {
        this(null, keywords, new HashSet<>());
    }

    @Override
    public CommandResult execute() {
        model.updateFilteredPanel(panelType, keywords, tagNames);

        String displayMessage = getMessageForTaskListShownSummary(
                model.getActivityListForPanelType(Activity.PanelType.SCHEDULE).size()
                        + model.getActivityListForPanelType(Activity.PanelType.DEADLINE).size()
                        + model.getActivityListForPanelType(Activity.PanelType.FLOATING).size()
        );
        return new CommandResult(displayMessage, true);
    }

}
