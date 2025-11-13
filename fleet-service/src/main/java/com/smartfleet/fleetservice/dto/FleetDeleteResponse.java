package com.smartfleet.fleetservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FleetDeleteResponse {
    private String message;
    private Long deletedId;
}
