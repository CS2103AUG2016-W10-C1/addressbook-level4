package seedu.taskman;

public class Constants {
    public static final String APP_ICON_FILE_PATH = "/images/clipboard.png";

    //@@author A0121299A
    public enum Icon {
        OVERDUE("/images/surprised.png"),
        INCOMPLETE("/images/confused.png"),
        COMPLETE("/images/cute.png");

        private final String path;

        Icon(String path){
            this.path = path;
        }

        public String getPath(){
            return path;
        }
    }

    public enum TextStyle {
        AMBER("label-amber"),
        GREEN("label-green"),
        RED("label-red");

        private final String styleClass;

        TextStyle(String path){
            this.styleClass = path;
        }

        public String getStyleClass(){
            return styleClass;
        }
    }
}
