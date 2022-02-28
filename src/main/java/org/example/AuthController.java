package org.example;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.example.command.*;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
@Slf4j
public class AuthController implements Initializable {
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    private Parent root;
    private boolean response;
    public static Socket socket;
    @FXML
    private PasswordField password;
    @FXML
    private TextField login;
    @FXML
    private Button btn;
    @FXML
    private Button btnSignIn;
    @FXML
    private void click(ActionEvent event) {
        btn.setText("Enter again!");
    }
    @FXML
    void authButton(ActionEvent event) throws IOException {
        os.writeObject(new AuthMessage(login.getText(), password.getText()));
    }
    @FXML
    void registration(ActionEvent event) throws IOException {
        btn.getScene().getWindow().hide();
        root = FXMLLoader.load(getClass().getResource("registrationField.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("localhost", 8189);
            System.out.println("Network created...");
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());
            Thread readThread = new Thread(this::readLoop);
            readThread.setDaemon(true);
            readThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readLoop() {
        try {
            while (true) {
                CloudMessage message = (CloudMessage) is.readObject();
                log.info("received: {}",message);
                System.out.println(message);
                switch (message.getType()) {
                    case LIST:
                        System.out.println((ListMessage) message);
                        break;
                    case AUTH_RESPONSE:
                        if (((AuthResponse) message).getAuthResponse()){
                            btnSignIn.setVisible(true);
                            response = true;
                            System.out.println(((AuthResponse) message));
                            Platform.runLater(() -> {
                                try {
                            btn.getScene().getWindow().hide();
                            root = FXMLLoader.load(getClass().getResource("layoutV2.fxml"));
                            Stage stage = new Stage();
                            stage.setScene(new Scene(root));
                            stage.show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                        break;

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void changeScene() throws IOException {
        System.out.println("*");
//        btn.getScene().getWindow().hide();
        root = FXMLLoader.load(getClass().getResource("layoutV2.fxml"));
        System.out.println("**");

//        root = FXMLLoader.load(getClass().getResource("registrationField.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
        stage.close();
        System.out.println("***");
    }

//    public static Socket getSocket() {
//        return socket;
//    }

    @FXML
    public void authButtonSignIn(ActionEvent event) throws IOException {
        System.out.println("No War");
        registration(event);
    }
}



