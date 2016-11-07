package seedu.taskman.ui;

import javafx.beans.property.SimpleStringProperty;

//@@author A0140136W
/**
 * Class for to contain data help TableView row.
 * Contains a command and its respective format.
 */
public class HelpTableViewRow {

    private final SimpleStringProperty command;
    private final SimpleStringProperty format;
 
    protected HelpTableViewRow(String command, String format) {
        this.command = new SimpleStringProperty(command);
        this.format = new SimpleStringProperty(format);
    }
 
    public String getCommand() {
        return command.get();
    }
    public void setCommand(String command) {
        this.command.set(command);
    }
        
    public String getFormat() {
        return format.get();
    }
    public void setFormat(String format) {
        this.format.set(format);
    }
    
}
//@@author