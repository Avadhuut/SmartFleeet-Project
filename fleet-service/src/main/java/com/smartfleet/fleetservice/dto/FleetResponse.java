package com.smartfleet.fleetservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FleetResponse {
    private Long id;
    private String vehicleNumber;
    private String type;
    private String status;
}
