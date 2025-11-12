package com.smartfleet.tracking_service.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tracker {
    private final Long tripId;
    private double latitude;
    private double longitude;
    private boolean active = true;

    // ✅ Custom constructor for your simulation
    public Tracker(Long tripId, double latitude, double longitude) {
        this.tripId = tripId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void stop() {
        this.active = false;
    }
}
