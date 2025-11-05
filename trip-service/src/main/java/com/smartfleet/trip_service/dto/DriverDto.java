package com.smartfleet.trip_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverDto {
    private Long id;
    private String name;
    private String licenseNumber;
    private String status; // ACTIVE / INACTIVE / ON_TRIP
}
