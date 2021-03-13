package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    public HBox authPanel;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;

    @FXML
    public TextArea textArea;
    @FXML
    public HBox msgPanel;
    @FXML
    public TextField textField;

    private String nickName;
    private boolean isAuthenticated;
    private final int PORT = 8189;
    private final String IP_ADDRESS = "localhost";
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private final String TITLE = "Chat";

    public void setAuthenticated(boolean isAuthenticated){
        this.isAuthenticated = isAuthenticated;
        authPanel.setVisible(!isAuthenticated);
        authPanel.setManaged(!isAuthenticated);
        msgPanel.setVisible(isAuthenticated);
        msgPanel.setManaged(isAuthenticated);

        if(!isAuthenticated){
            nickName = "";
        }
        setTitle(nickName);
    }

    private void setTitle(String nickName){
        Platform.runLater(()-> ((Stage)textField.getScene()
                .getWindow())
                .setTitle(String.format("%s: %s", TITLE , nickName)));
    }

    public void tryToAuth(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) {
            connect();
        }

        try {
            String str = String.format("/auth %s %s", loginField.getText(), passwordField.getText());
            outputStream.writeUTF(str);
            passwordField.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendMsg(ActionEvent actionEvent) {
        String str = textField.getText();
        try {
            outputStream.writeUTF(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        textField.clear();
        textField.requestFocus();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAuthenticated(false);
    }

    public void connect() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());

            Runnable t1 = (() -> {
                while (true) {
                    try {
                        String str = inputStream.readUTF();
                        if (str.startsWith("/authok")) {
                            nickName = str.split(" ", 2)[1];
                            setAuthenticated(true);
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                while (true) {
                    try {
                        String str = inputStream.readUTF();
                        textArea.appendText(str + "\n");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            new Thread(t1).start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
