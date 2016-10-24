package seedu.taskman.logic.commands;

public class CommandHistory {
    public final String inputCommand;
    public final String resultMessage;

    public CommandHistory(String inputCommand, String resultMessage) {
        this.inputCommand = inputCommand;
        this.resultMessage = resultMessage;
    }

}
