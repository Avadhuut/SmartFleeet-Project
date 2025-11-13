package com.smartfleet.tracking_service.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor  // Required for Redis deserialization
@AllArgsConstructor // Keeps your existing constructor pattern
public class Tracker implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long tripId;  // Remove 'final' so Redis can set it
    private double latitude;
    private double longitude;
    private boolean active = true;

    // Custom constructor for your simulation (kept for backward compatibility)
    public Tracker(Long tripId, double latitude, double longitude) {
        this.tripId = tripId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.active = true;
    }

    public void stop() {
        this.active = false;
    }
}