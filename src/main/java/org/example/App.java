package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Path;

public class App extends Application {

    private static Stage primaryStage;
    private static String previousWindow;
    private static Path currentFolder;

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent parent = FXMLLoader.load(getClass().getResource("passwordField.fxml"));
        this.primaryStage = primaryStage;
        primaryStage.setScene(new Scene(parent));
        primaryStage.show();
    }

}
