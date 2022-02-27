package org.example;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.example.command.*;
import org.example.server.AuthServer;
@Slf4j
public class RegisterController implements Initializable {
    private Parent root;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    private static Socket socket;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField repeatPassword;

    @FXML
    private TextField login;

    @FXML
    private TextField name;

    @FXML
    private Button btn;

    @FXML
    private Hyperlink up;

    @FXML
    void register(ActionEvent event) {
        try {
            os.writeObject(new RegistrationMessage(name.getText(), login.getText(), password.getText()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void clickСhange() {
        up.setText("Регистрация прошла успешно, пройдите Аунтефикацию");
    }

    @FXML
    void up(ActionEvent event) throws IOException {
//        btn.getScene().getWindow().hide();
        root = FXMLLoader.load(getClass().getResource("passwordField.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();

    }

    private void readLoop() {
        try {
            while (true) {
                CloudMessage message = (CloudMessage) is.readObject();
                log.info("received: {}", message);
                switch (message.getType()) {
                    case REG_RESPONSE:
                        if (((RegistrationResponse) message).getRegResponse()) {
                            System.out.println(((RegistrationResponse) message).getRegResponse());
                            clickСhange();
                            btn.setVisible(false);
                        }
                }
                System.out.println(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
////            Socket socket = new Socket("localhost", 8189);
            Socket socket = AuthController.getSocket();
            System.out.println("Network created...");

//            os = new ObjectEncoderOutputStream(socket.getOutputStream());
//            is = new ObjectDecoderInputStream(socket.getInputStream());
//
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());
            Thread readThread = new Thread(this::readLoop);
            readThread.setDaemon(true);
            readThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

