package com.smartfleet.tracking_service.controller;

import com.smartfleet.tracking_service.dto.StartTrackingRequest;
import com.smartfleet.tracking_service.model.Tracker;
import com.smartfleet.tracking_service.service.TrackingManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    private final TrackingManager trackingManager;

    public TrackingController(TrackingManager trackingManager) {
        this.trackingManager = trackingManager;
    }

    @PostMapping("/start")
    public ResponseEntity<Void> start(@RequestBody StartTrackingRequest req) {
        trackingManager.startTracking(req);
        return ResponseEntity.created(URI.create("/api/tracking/" + req.getTripId())).build();
    }

    @PostMapping("/stop/{tripId}")
    public ResponseEntity<Void> stop(@PathVariable Long tripId) {
        trackingManager.stopTracking(tripId);
        return ResponseEntity.noContent().build();
    }

    // Optional: view active trips
    @GetMapping("/active")
    public Collection<Tracker> getActive() {
        return trackingManager.getActiveTrackers();
    }
}
