package seedu.taskman.ui;

import javafx.beans.property.SimpleStringProperty;

/**
 * Controller for a help page
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