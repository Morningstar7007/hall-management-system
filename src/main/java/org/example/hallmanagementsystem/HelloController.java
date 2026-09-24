package org.example.hallmanagementsystem;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

    @FXML
    protected void onManageStudentsClick() {
        statusLabel.setText("System Status: Loading Student Data...");
    }

    @FXML
    protected void onManageRoomsClick() {
        statusLabel.setText("System Status: Loading Room Data...");
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

                // Parse the JSON response using Jackson's ObjectMapper
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(response.body());

                // Navigate the JSON tree: {"slip": { "advice": "..."}}
                String tip = rootNode.path("slip").path("advice").asText();

                // Update UI safely
                Platform.runLater(() -> apiDataLabel.setText("Manager Tip: " + tip));

            } catch (Exception e) {
                Platform.runLater(() -> apiDataLabel.setText("Manager Tip: Could not load data."));
            }
        }).start();
    }
}