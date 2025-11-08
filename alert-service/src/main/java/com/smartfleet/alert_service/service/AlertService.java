package com.smartfleet.alert_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartfleet.alert_service.domain.Alert;
import com.smartfleet.alert_service.dto.TrackingUpdate;
import com.smartfleet.alert_service.repo.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository repo;
    private final ObjectMapper mapper = new ObjectMapper();
    private final StringRedisTemplate redis;

    @Value("${alert.delay.minutes:10}")
    private int delayMinutes;

    private final ConcurrentHashMap<Long, String> lastLocation = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Integer> sameCount = new ConcurrentHashMap<>();

    public void processTrackingUpdate(String message) {
        try {
            TrackingUpdate update = mapper.readValue(message, TrackingUpdate.class);
            Long tripId = update.getTripId();
            String currentLocation = update.getLatitude() + "," + update.getLongitude();

            String previousLocation = lastLocation.get(tripId);

            if (previousLocation != null && previousLocation.equals(currentLocation)) {
                int count = sameCount.getOrDefault(tripId, 1) + 1;
                sameCount.put(tripId, count);
                if (count >= 3) {
                    saveAlert(tripId, "STOPPED",
                            "Vehicle " + tripId + " seems to be stopped (same location 3 times)");
                    sameCount.put(tripId, 0);
                }
            } else {
                sameCount.put(tripId, 1);
                lastLocation.put(tripId, currentLocation);
            }

            // ✅ Record current time in Redis for DELAY detection
            redis.opsForValue().set("trip:" + tripId, String.valueOf(System.currentTimeMillis()));

        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
        }
    }

    // ✅ Runs every 1 minute to check for delays
    @Scheduled(fixedRate = 60000)
    public void checkForDelayedTrips() {
        Set<String> keys = redis.keys("trip:*");
        if (keys == null) return;

        long now = System.currentTimeMillis();

        for (String key : keys) {
            try {
                String lastUpdate = redis.opsForValue().get(key);
                if (lastUpdate == null) continue;

                long minutesPassed = (now - Long.parseLong(lastUpdate)) / 60000;
                if (minutesPassed >= delayMinutes) {
                    Long tripId = Long.valueOf(key.split(":")[1]);
                    saveAlert(tripId, "DELAY",
                            "Trip " + tripId + " has no GPS updates for " + delayMinutes + " minutes");

                    redis.opsForValue().set("trip:" + tripId, String.valueOf(now));
                }
            } catch (Exception e) {
                System.err.println("Error checking delay: " + e.getMessage());
            }
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
