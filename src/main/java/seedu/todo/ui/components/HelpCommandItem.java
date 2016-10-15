package seedu.todo.ui.components;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import seedu.todo.ui.UiPartLoader;

public class HelpCommandItem extends MultiComponent {

    private static final String FXML_PATH = "components/HelpCommandItem.fxml";
    
    // Props
    public String commandName;
    public String commandDescription;
    public String commandSyntax;
    
    // FXML
    @FXML
    private Text commandNameText;
    @FXML
    private Text commandDescriptionText;
    @FXML
    private Text commandSyntaxText;

    public static HelpCommandItem load(Stage primaryStage, Pane placeholderPane) {
        return UiPartLoader.loadUiPart(primaryStage, placeholderPane, new HelpCommandItem());
    }
    
    @Override
    public String getFxmlPath() {
        return FXML_PATH;
    }
    
    @Override
    public void componentDidMount() {
        commandNameText.setText(commandName);
        commandDescriptionText.setText(commandDescription);
        commandSyntaxText.setText(commandSyntax);
    }

}