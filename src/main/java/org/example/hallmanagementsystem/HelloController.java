package org.example.hallmanagementsystem;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HelloController {

    @FXML
    private Label statusLabel;

    @FXML
    private Label timeLabel;

    @FXML
    public void initialize() {
        // Week 4: Multithreading and Concurrency
        // Create a background task using Runnable
        Runnable clockTask = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        // 1. Get the exact current time
                        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss a");
                        String currentTime = formatter.format(new Date());

                        // 2. Update the JavaFX UI safely using Platform.runLater
                        Platform.runLater(() -> timeLabel.setText("Time: " + currentTime));

                        // 3. Put this background thread to sleep for 1 second
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("Clock thread interrupted");
                        break;
                    }
                }
            }
        };

        // Start the background thread
        Thread clockThread = new Thread(clockTask);
        // setDaemon(true) ensures this background thread stops when you close the app window
        clockThread.setDaemon(true);
        clockThread.start();
    }

    @FXML
    protected void onManageStudentsClick() {
        statusLabel.setText("System Status: Loading Student Data...");
    }

    @FXML
    protected void onManageRoomsClick() {
        statusLabel.setText("System Status: Loading Room Data...");
    }
}