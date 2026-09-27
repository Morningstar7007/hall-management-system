package org.example.hallmanagementsystem.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Service to fetch the secure network time from the internet.
 * Demonstrates Networking and JSON Parsing.
 */
public class NetworkTimeService {

    public static String fetchDhakaTime() {
        try {
            // 1. Setup the HTTP Client and Request using a more reliable API (TimeAPI.io)
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://timeapi.io/api/Time/current/zone?timeZone=Asia/Dhaka"))
                    .GET()
                    .build();

            // 2. Send the request over the network
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 3. Parse the JSON response using Jackson
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());
            
            // Extract the 'date' and 'time' fields from the JSON object
            String date = root.path("date").asText();
            String time = root.path("time").asText();

            // 4. Return the beautifully formatted string
            return date + " at " + time;

        } catch (Exception e) {
            e.printStackTrace();
            return "Unable to fetch secure time (Network Error)";
        }
    }
}
