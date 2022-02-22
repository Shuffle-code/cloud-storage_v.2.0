package org.example;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.server.AuthServer;

import java.io.IOException;
import java.util.Optional;

public class AuthController {
    private Parent root;
    @FXML
    private PasswordField password;

    @FXML
    private TextField login;

    @FXML
    private Button btn;





    @FXML
    private void click(ActionEvent event) {
        btn.setText("Enter again!");
    }

    @FXML
    void authButton(ActionEvent event) throws IOException {
        AuthServer authServer = new AuthServer();
        click(event);
        btn.getScene().getWindow().hide();
        if (authServer.authorization(login.getText(), password.getText()) == true){
            root = FXMLLoader.load(getClass().getResource("layoutV2.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        }

    }

    @FXML
    void registration(ActionEvent event) throws IOException {
        btn.getScene().getWindow().hide();
        root = FXMLLoader.load(getClass().getResource("registrationField.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();

    }


//    private void loginUser (String loginText, String passwordText) {
//        AuthServer authServer = new AuthServer();
//        Optional<AuthServer.Entry> maybeUser = authServer
//                .getLoginAndPass(loginText, passwordText);
//        if (maybeUser.isPresent()) {
//            AuthServer.Entry user = maybeUser.get();
//            if (server.isNotUserOccupied(user.getName())) {
//                name = user.getName();
//                sendMessage("AUTH OK.");
//                sendMessage("Welcome.");
//                server.broadcastMessage(String.format("User[%s] entered chat.", name));
//                server.subscribe(this);
//                return;
//            } else {
//                sendMessage("Current user is already logged in");
//            }
//        } else {
//            sendMessage("Invalid credentials.");
//
////        user.setLogin(loginText);
////        user.setPass(passwordText);
//
//        }
//    }



        @FXML
        void initialize () {
//            btn.setOnAction(event -> {
//                String loginField = login.getText().trim();
//                String passwordField = password.getText().trim();
//
//                if (!loginField.equals("") && !passwordField.equals("")) {
//                    loginUser(loginField, passwordField);
//                } else System.out.println("Error");
//            });
        }
    }



