package com.smartfleet.trip_service.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StartTrackingRequest {
    private Long tripId;
    private Long vehicleId;
    private Double startLatitude;
    private Double startLongitude;



}
