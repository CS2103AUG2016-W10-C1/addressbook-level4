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
    
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all entries whose titles contain any of "
            + "the specified keywords (case-sensitive) or tags and filters them out in their respective panels.\n"
            + "Parameters: [KEYWORDS]... [t/TAGS]...\n"
            + "Example: " + COMMAND_WORD + " d homework t/engineering";

    private static final Pattern SPECIFY_PANEL_ARGS_FORMAT =
            Pattern.compile("" + CommandParser.ArgumentPattern.PANEL + "?"
                    + CommandParser.ArgumentPattern.OPTIONAL_KEYWORDS
                    + CommandParser.ArgumentPattern.OPTIONAL_TAGS);

    private static final Pattern SPECIFY_PANELESS_ARGS_FORMAT =
            Pattern.compile("" + CommandParser.ArgumentPattern.KEYWORDS
                    + CommandParser.ArgumentPattern.OPTIONAL_TAGS);

    private final Activity.PanelType panelType;
    private final Set<String> keywords;
    private final Set<String> tagNames;

    public static Command prepareList(String args) {
        final String trimmedArgs = args.trim();
        final Matcher matcherWithPanel = SPECIFY_PANEL_ARGS_FORMAT.matcher(trimmedArgs);
        final Matcher matcherPaneless = SPECIFY_PANELESS_ARGS_FORMAT.matcher(trimmedArgs);

        if (trimmedArgs.isEmpty()) {
            // TODO: blocked for now
            //return new ListCommand(keywordSet);
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ListCommand.MESSAGE_USAGE));
        } else if (matcherWithPanel.matches()) {
            return panelSpecificListCommand(
                    matcherWithPanel.group("panel"),
                    matcherWithPanel.group("keywords"),
                    matcherWithPanel.group("tagArguments")
            );
        } else if(matcherPaneless.matches()) {
            return fullWindowListCommand(
                    matcherPaneless.group("keywords"),
                    matcherPaneless.group("tagArguments")
            );
        } else {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ListCommand.MESSAGE_USAGE));
        }
    }

    private static ListCommand fullWindowListCommand(String rawKeywords, String rawTagArguments) {
        return new ListCommand(null, processRawKeywords(rawKeywords), processRawTags(rawTagArguments));
    }

    private static ListCommand panelSpecificListCommand(String rawPanel, String rawKeywords, String rawTagArguments) {
        Activity.PanelType panelType = null;

        if (rawPanel != null) {
            rawPanel = rawPanel.trim();
            panelType = Activity.PanelType.fromString(rawPanel);
        }

        return new ListCommand(panelType, processRawKeywords(rawKeywords), processRawTags(rawTagArguments));
    }

    private static Set<String> processRawKeywords(String rawKeywords) {
        Set<String> keywordSet = Collections.EMPTY_SET;
        if (rawKeywords != null) {
            String[] keywords = rawKeywords.split("\\s+");
            keywordSet = new HashSet<>(Arrays.asList(keywords));
        }
        return keywordSet;
    }

    private static Set<String> processRawTags(String rawTags) {
        Set<String> tagSet = Collections.EMPTY_SET;
        if (rawTags != null) {
            tagSet = getTagsFromArgs(rawTags);
        }
        return tagSet;
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
        return new CommandResult("", true);
    }

}
