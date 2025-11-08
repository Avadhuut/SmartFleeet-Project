package com.smartfleet.alert_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartfleet.alert_service.domain.Alert;
import com.smartfleet.alert_service.dto.TrackingUpdate;
import com.smartfleet.alert_service.repo.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository repo;
    private final ObjectMapper mapper = new ObjectMapper();

    // Memory maps to keep track of vehicle locations and repetition
    private final ConcurrentHashMap<Long, String> lastLocation = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Integer> sameCount = new ConcurrentHashMap<>();

    // Main logic to process GPS data coming from Kafka
    public void processTrackingUpdate(String message) {
        try {
            // Convert JSON message into Java object
            TrackingUpdate update = mapper.readValue(message, TrackingUpdate.class);

            Long tripId = update.getTripId();
            String currentLocation = update.getLatitude() + "," + update.getLongitude();

            // Check the last known location
            String previousLocation = lastLocation.get(tripId);

            if (previousLocation != null && previousLocation.equals(currentLocation)) {
                int count = sameCount.getOrDefault(tripId, 1) + 1;
                sameCount.put(tripId, count);

                // If same location comes 3 times → "STOPPED"
                if (count >= 3) {
                    saveAlert(tripId, "STOPPED",
                            "Vehicle " + tripId + " seems to be stopped (same location 3 times)");
                    sameCount.put(tripId, 0); // reset after alert
                }
            } else {
                // new location or first time
                sameCount.put(tripId, 1);
                lastLocation.put(tripId, currentLocation);
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }

    private void saveAlert(Long tripId, String type, String message) {
        Alert alert = Alert.builder()
                .tripId(tripId)
                .alertType(type)
                .message(message)
                .createdAt(LocalDateTime.now())
                .build();

        repo.save(alert);
    }
}
