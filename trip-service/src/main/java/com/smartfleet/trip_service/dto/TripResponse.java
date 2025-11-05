package com.smartfleet.trip_service.dto;

import com.smartfleet.trip_service.model.TripStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripResponse {
    private Long id;
    private String tripName;
    private Long driverId;
    private Long vehicleId;
    private TripStatus status;
}
