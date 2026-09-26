package org.example.hallmanagementsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        // Remove hardcoded width/height so it scales naturally
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Hall Management System");
        stage.setScene(scene);

        // Force the window to launch Maximized
        stage.setMaximized(true);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}