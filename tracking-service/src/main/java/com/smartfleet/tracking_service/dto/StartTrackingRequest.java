package com.smartfleet.tracking_service.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartTrackingRequest {
    private Long tripId;
    private Long vehicleId;
    private Double startLatitude;
    private Double startLongitude;


}
