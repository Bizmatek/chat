package client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
    @FXML
    public ListView<String> clientList;

    private Stage stage;
    private Stage regStage;
    private RegController regController;

    private String nickName;
    private boolean isAuthenticated;
    private final int PORT = 8189;
    private final String IP_ADDRESS = "localhost";
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    private final String TITLE = "Chat";

    public void setAuthenticated(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
        authPanel.setVisible(!isAuthenticated);
        authPanel.setManaged(!isAuthenticated);
        msgPanel.setVisible(isAuthenticated);
        msgPanel.setManaged(isAuthenticated);
        clientList.setVisible(isAuthenticated);
        clientList.setManaged(isAuthenticated);

        if (!isAuthenticated) {
            nickName = "";
        }
        setTitle(nickName);
    }

    private void setTitle(String nickName) {
        Platform.runLater(() -> ((Stage) textField.getScene()
                .getWindow())
                .setTitle(String.format("%s: %s", TITLE, nickName)));
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
        createRegWindow();
        Platform.runLater(() -> {
            stage = (Stage) textField.getScene().getWindow();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    if (socket != null && !socket.isClosed()) {
                        try {
                            outputStream.writeUTF("/end");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        });
    }

    private void createRegWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/reg.fxml"));
            Parent root = fxmlLoader.load();
            regStage = new Stage();
            regStage.setTitle("Reg window");
            regStage.setScene(new Scene(root, 400, 250));

            regController = fxmlLoader.getController();
            regController.setController(this);

            regStage.initModality(Modality.APPLICATION_MODAL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            socket = new Socket(IP_ADDRESS, PORT);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());

            Runnable t1 = (() -> {

                try {
                    while (true) {
                        String str = inputStream.readUTF();
                            if (str.startsWith("/authok")) {
                                nickName = str.split(" ", 2)[1];
                                setAuthenticated(true);
                                break;
                            }
                            if (str.startsWith("/regok")) {
                                regController.addMsgToTextArea("Registration successful");
                            }
                            if (str.startsWith("/regno")) {
                                regController.addMsgToTextArea("Registration failed. Try to use another login or nickname");
                            }
                        textArea.appendText(str + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


                while (true) {
                    try {
                        String str = inputStream.readUTF();

                        if(str.startsWith("/clientsList")){
                            String[] token = str.split("\\s+");
                            Platform.runLater(()->{
                                clientList.getItems().clear();
                                for (int i = 1; i < token.length; i++) {
                                    clientList.getItems().add(token[i]);
                                }
                            });
                        }

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

    public void signUp(ActionEvent actionEvent) {
        regStage.show();
    }

    public void tryToReg(String login, String password, String nickName) {
        String message = String.format("/reg %s %s %s", login, password, nickName);
        if (socket == null || socket.isClosed()) {
            connect();
        }
        try {
            outputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clickClientList(MouseEvent mouseEvent) {
        String receiver = clientList.getSelectionModel().getSelectedItem();
        textField.setText("/w " + receiver + " ");
    }
}
