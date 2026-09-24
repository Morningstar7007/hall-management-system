package org.example.hallmanagementsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {

    @FXML
    private Label statusLabel;

    @FXML
    protected void onManageStudentsClick() {
        statusLabel.setText("System Status: Loading Student Data...");
        System.out.println("Navigating to Student Management...");
        // In the future, this will open the Student screen
    }

    @FXML
    protected void onManageRoomsClick() {
        statusLabel.setText("System Status: Loading Room Data...");
        System.out.println("Navigating to Room Management...");
        // In the future, this will open the Room screen
    }
}