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
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Integrated Assignment Environment!");

        stage.setScene(scene);
        stage.show();

        stage.setMinWidth(600);
        stage.setMinHeight(500);
        stage.setMaxWidth(750);
        stage.setMaxHeight(600);
        stage.setResizable(true);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Welcome to the Integrated Assignment Environment");
        alert.setHeaderText(null);
        alert.setContentText("Select an option to start. In each page a help menu will guide you! You can click on the question mark to see instructions.");

        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}