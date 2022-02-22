package org.example;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.server.AuthServer;

public class RegisterController {
    private Parent root;

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

    }

    @FXML
    private void clickСhange() {
        up.setText("Регистрация прошла успешно, пройдите Аунтефикацию");
    }

    @FXML
    void up(ActionEvent event) throws IOException {
        btn.getScene().getWindow().hide();
        root = FXMLLoader.load(getClass().getResource("passwordField.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();

    }



    @FXML
    void initialize() {
        AuthServer authServer = new AuthServer();
        btn.setOnAction(event -> {
            authServer.registration(name.getText(), login.getText(), password.getText());
            clickСhange();
        });

    }
}

