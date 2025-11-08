package com.smartfleet.tracking_service.controller;

import com.smartfleet.tracking_service.simulator.TrackingManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoint called by Trip-Service when a new trip starts.
 */
@RestController
@RequestMapping("/tracking")
@RequiredArgsConstructor
public class TripTrackingController {

    private final TrackingManager manager;

    @PostMapping("/start/{tripId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void startTracking(@PathVariable Long tripId,
                              @RequestParam(defaultValue = "18.5204") double lat,
                              @RequestParam(defaultValue = "73.8567") double lon) {
        manager.startTracking(tripId, lat, lon);
    }

    @DeleteMapping("/stop/{tripId}")
    public void stopTracking(@PathVariable Long tripId) {
        manager.stopTracking(tripId);
    }
}
