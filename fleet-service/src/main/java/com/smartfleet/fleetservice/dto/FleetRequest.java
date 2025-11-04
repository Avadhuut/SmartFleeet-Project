package com.smartfleet.fleetservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FleetRequest {
    @NotBlank(message = "Vehicle number required")
    private String vehicleNumber;

    @NotBlank(message = "Type required")
    private String type;

    @NotBlank(message = "Status required")
    private String status;
}
