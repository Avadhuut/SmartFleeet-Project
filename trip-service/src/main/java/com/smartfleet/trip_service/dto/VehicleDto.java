package com.smartfleet.trip_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDto {
    private Long id;
    private String vehicleNumber;
    private String type;
    private String status; // AVAILABLE / ASSIGNED / MAINTENANCE
}
