package com.smartfleet.tracking_service.simulator;

import com.smartfleet.tracking_service.dto.TrackingUpdate;
import com.smartfleet.tracking_service.model.Tracker;
import com.smartfleet.tracking_service.service.TrackingManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class GpsSimulator {

    private final TrackingManager trackingManager;

    public GpsSimulator(TrackingManager trackingManager) {
        this.trackingManager = trackingManager;
    }

    // ✅ Runs every 7 seconds to simulate movement
    @Scheduled(fixedRate = 7000)
    public void generateFakeLocations() {

        // Loop through all active trackers
        for (Tracker t : trackingManager.getActiveTrackers()) {
            if (!t.isActive()) continue; // Skip inactive ones

            // ✅ 1️⃣ Slightly adjust latitude & longitude (simulate movement)
            double newLat = t.getLatitude() + 0.0001;
            double newLon = t.getLongitude() + 0.0001;

        // For Testing Purpose not incrementing Location
//            double newLat = t.getLatitude();
//            double newLon = t.getLongitude();

            //  Create timestamp string (matches DTO)
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            // Create TrackingUpdate object with String timestamp
            TrackingUpdate update = new TrackingUpdate();
            update.setTripId(t.getTripId());
            update.setLatitude(newLat);
            update.setLongitude(newLon);
            update.setTimestamp(timestamp);

            // Send it to TrackingManager → which sends it to Kafka
            trackingManager.handleSimulatedPosition(update);
        }
    }
}
