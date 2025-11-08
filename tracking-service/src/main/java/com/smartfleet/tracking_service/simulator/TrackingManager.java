package com.smartfleet.tracking_service.simulator;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages list of active trips to track.
 */
@Component
@Getter
@Slf4j
public class TrackingManager {

    private final Map<Long, double[]> activeTrips = new ConcurrentHashMap<>();

    public void startTracking(Long tripId, double latitude, double longitude) {
        activeTrips.put(tripId, new double[]{latitude, longitude});
        log.info("🛰️ Started tracking Trip {}", tripId);
    }

    public void stopTracking(Long tripId) {
        activeTrips.remove(tripId);
        log.info("🛑 Stopped tracking Trip {}", tripId);
    }
}
