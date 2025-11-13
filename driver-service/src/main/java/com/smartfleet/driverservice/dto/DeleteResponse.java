package com.smartfleet.driverservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteResponse {
    private String message;
    private Long driverId;
}
