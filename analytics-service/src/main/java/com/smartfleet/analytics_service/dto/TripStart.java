package com.smartfleet.analytics_service.dto;
import lombok.Data;

@Data
public class TripStart {
    private Long tripId;
    private Long driverId;
    private Long vehicleId;
    private String tripName;
    private String startedAt;
}
