package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent1 = FXMLLoader.load(getClass().getResource("layoutV2.fxml"));
        primaryStage.setScene(new Scene(parent1));
        primaryStage.show();
    }
}
