package org.example.hallmanagementsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.example.hallmanagementsystem.core.BaseDashboardController;

public class HelloController extends BaseDashboardController {

    @FXML private javafx.scene.layout.BorderPane rootPane;
    @FXML private javafx.scene.layout.VBox sidebarVBox;
    @FXML private Label welcomeLabel;
    @FXML private StackPane contentArea;
    @FXML private Label statusLabel;
    @FXML private Label networkTimeLabel;

    @Override
    protected StackPane getContentArea() {
        return contentArea;
    }

    @Override
    protected Label getStatusLabel() {
        return statusLabel;
    }

    @FXML
    public void initialize() {
        // Platform.runLater ensures that the UI elements are fully loaded and attached to the layout before binding
        javafx.application.Platform.runLater(() -> {
            // Bind sidebar width to exactly 20% of the root pane's (window) width dynamically
            if (rootPane != null && sidebarVBox != null) {
                sidebarVBox.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.20));
            }

            // Bind welcome label font size to scale dynamically with window width
            if (welcomeLabel != null && rootPane != null) {
                welcomeLabel.styleProperty().bind(
                        javafx.beans.binding.Bindings.concat("-fx-font-size: ", rootPane.widthProperty().divide(35).asString(), "px;")
                );
            }
        });

        // Fetch Secure Network Time (JSON Parsing)
        if (networkTimeLabel != null) {
            org.example.hallmanagementsystem.core.ConcurrencyManager.execute(() -> {
                String secureTime = org.example.hallmanagementsystem.core.NetworkTimeService.fetchDhakaTime();
                javafx.application.Platform.runLater(() -> {
                    networkTimeLabel.setText(secureTime);
                });
            });
        }
    }

    @FXML
    protected void onProfileClick() {
        loadView("profile-view.fxml");
    }

    @FXML
    protected void onMealClick() {
        loadView("meal-view.fxml");
    }

    @FXML
    protected void onPaymentClick() {
        loadView("payment-view.fxml");
    }

    @FXML
    protected void onSettingsClick() {
        loadView("settings-view.fxml");
    }

    @FXML
    protected void onLogoutClick() {
        handleLogout();
    }
}
