package org.example.hallmanagementsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ManageStudentsController {

    @FXML private Label formStatusLabel;

    @FXML
    protected void onSaveStudentClick() {
        formStatusLabel.setText("Save functionality coming next...");
    }
}