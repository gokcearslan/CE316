package com.example.ce316project10may;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("WelcomePage.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 250);
        stage.setTitle("Integrated Assignment Environment!");

        stage.setScene(scene);
        stage.show();

        stage.setMinWidth(700);
        stage.setMinHeight(550);
        stage.setMaxWidth(750);
        stage.setMaxHeight(600);
        stage.setResizable(true);

    }

    public static void main(String[] args) {
        launch();
    }
}