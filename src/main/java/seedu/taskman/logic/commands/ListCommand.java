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
 * Finds and lists all tasks in TaskMan whose title contains any of the argument keywords and contains any of the given tags.
 * Keyword matching is case sensitive.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";
    
    public static final String MESSAGE_USAGE = "Find all activities whose titles contain "
            + "the specified keywords or holds the specified tags.\n"
            + "Parameters: [PANEL]... [KEYWORDS]... [t/TAGS]...\n"
            + "Example: " + COMMAND_WORD + " d homework t/engineering";

    public static final String MESSAGE_FEEDBACK_DEFAULT = "All entries are listed.";
    public static final String MESSAGE_FEEDBACK_FILTERED = "Entries with the following keywords and tags are listed.\nKeywords: %1$s\nTags: %2$s\n";
    public static final String MESSAGE_FEEDBACK_WORD_NOT_SPECIFIED = "(Not specified)";
    public static final String MESSAGE_FEEDBACK_WORD_SEPARATOR = " ";

    private static final Pattern SPECIFY_PANEL_ARGS_FORMAT =
            Pattern.compile("" + CommandParser.ArgumentPattern.PANEL
                    + CommandParser.ArgumentPattern.OPTIONAL_KEYWORDS
                    + CommandParser.ArgumentPattern.OPTIONAL_TAGS);

    private static final Pattern SPECIFY_NO_PANEL_ARGS_FORMAT =
            Pattern.compile("" + CommandParser.ArgumentPattern.OPTIONAL_KEYWORDS
                    + CommandParser.ArgumentPattern.OPTIONAL_TAGS);

    private final Activity.PanelType panelType;
    private final Set<String> keywords;
    private final Set<String> tagNames;

    public static Command prepareList(String args) {
        final String trimmedArgs = args.trim();
        final Matcher matcherWithPanel = SPECIFY_PANEL_ARGS_FORMAT.matcher(trimmedArgs);
        final Matcher matcherWithoutPanel = SPECIFY_NO_PANEL_ARGS_FORMAT.matcher(trimmedArgs);

        if (trimmedArgs.isEmpty()) {
            return new ListCommand(
                    Activity.PanelType.ALL,
                    processRawKeywords(null),
                    processRawTags(null)
            );
        } else if (matcherWithPanel.matches()) {
            return panelSpecificListCommand(
                    matcherWithPanel.group(CommandParser.Group.panel.name),
                    matcherWithPanel.group(CommandParser.Group.keywords.name),
                    matcherWithPanel.group(CommandParser.Group.tagArguments.name)
            );
        } else if(matcherWithoutPanel.matches()) {
            return allPanelsListCommand(
                    matcherWithoutPanel.group(CommandParser.Group.keywords.name),
                    matcherWithoutPanel.group(CommandParser.Group.tagArguments.name)
            );
        } else {
            return new IncorrectCommand(MESSAGE_INVALID_COMMAND_FORMAT
                    + "\n" + COMMAND_WORD + ": " + MESSAGE_USAGE);
        }
    }

    private static ListCommand allPanelsListCommand(String rawKeywords, String rawTagArguments) {
        return new ListCommand(Activity.PanelType.ALL, processRawKeywords(rawKeywords), processRawTags(rawTagArguments));
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

    @Override
    public CommandResult execute() {
        String feedback = MESSAGE_FEEDBACK_DEFAULT;
        if (panelType == null && keywords == null && tagNames == null) {
            model.updateAllPanelsToShowAll();
        } else {
            model.updateFilteredPanel(panelType, keywords, tagNames);
            StringBuilder words = new StringBuilder();
            String keywordString = MESSAGE_FEEDBACK_WORD_NOT_SPECIFIED;
            String tagString = keywordString;
            if (!keywords.isEmpty()) {
                for (String word : keywords) {
                    words.append(word);
                    words.append(MESSAGE_FEEDBACK_WORD_SEPARATOR);
                }
                keywordString = words.toString().trim();
                feedback = String.format(MESSAGE_FEEDBACK_FILTERED, keywordString, tagString);
            }
            if (!tagNames.isEmpty()) {
                words = new StringBuilder();
                for (String word : tagNames) {
                    words.append(word);
                    words.append(MESSAGE_FEEDBACK_WORD_SEPARATOR);
                }
                tagString = words.toString().trim();
                feedback = String.format(MESSAGE_FEEDBACK_FILTERED, keywordString, tagString);
            }
        }
        return new CommandResult(feedback, true);
    }

}
