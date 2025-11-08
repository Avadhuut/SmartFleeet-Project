package com.smartfleet.alert_service.dto;

import lombok.Data;

@Data
public class TrackingUpdate {
    private Long tripId;
    private double latitude;
    private double longitude;
    private String timestamp;  // e.g. "2025-11-08T10:00:00"
}
