package org.example.hallmanagementsystem;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HelloController {

    @FXML
    private Label statusLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Label apiDataLabel;

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        // Week 4: Multithreading (Clock)
        Runnable clockTask = () -> {
            while (true) {
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss a");
                    String currentTime = formatter.format(new Date());
                    Platform.runLater(() -> timeLabel.setText("Time: " + currentTime));
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        };
        Thread clockThread = new Thread(clockTask);
        clockThread.setDaemon(true);
        clockThread.start();

        // Week 7: Trigger API fetch in the background
        fetchDailyTip();
    }

    private void loadView(String fxmlFileName) {
        try {
            javafx.fxml.FXMLLoader fxmlLoader = new javafx.fxml.FXMLLoader(HelloApplication.class.getResource(fxmlFileName));
            javafx.scene.Node view = fxmlLoader.load();
            contentArea.getChildren().setAll(view);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error loading view.");
        }
    }

    @FXML
    protected void onProfileClick() {
        statusLabel.setText("Viewing Profile");
        loadView("profile-view.fxml");
    }

    @FXML
    protected void onMealClick() {
        statusLabel.setText("Viewing Meal Management");
        loadView("meal-view.fxml");
    }

    @FXML
    protected void onPaymentClick() {
        statusLabel.setText("Viewing Payment Details");
        loadView("payment-view.fxml");
    }
    @FXML
    protected void onSettingsClick() {
        statusLabel.setText("Loading Settings View...");
    }

    // Week 7: JSON Parsing and API Response using Jackson
    private void fetchDailyTip() {
        new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.adviceslip.com/advice"))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                // Parse the JSON response using Jackson
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(response.body());

                String tip = rootNode.path("slip").path("advice").asText();

                // Update UI safely
                Platform.runLater(() -> apiDataLabel.setText("Manager Tip: " + tip));

            } catch (Exception e) {
                Platform.runLater(() -> apiDataLabel.setText("Manager Tip: Could not load data."));
            }
        }).start();
    }
}