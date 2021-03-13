package client;

import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class RegController {
    public TextField loginField;
    public TextField nickField;
    public PasswordField passwordField;
    public TextArea textArea;
    private Controller controller;

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void tryToReg(ActionEvent actionEvent) {
        controller.tryToReg(loginField.getText().trim(),
                passwordField.getText().trim(),
                nickField.getText().trim());
    }

    public void addMsgToTextArea(String msg) {
        textArea.appendText(msg + "\n");
    }
}
