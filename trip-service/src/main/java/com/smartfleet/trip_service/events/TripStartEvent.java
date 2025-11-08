package com.smartfleet.trip_service.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TripStartEvent {
    private Long tripId;
    private Long driverId;
    private Long vehicleId;
    private String tripName;
    private String startedAt;
}
