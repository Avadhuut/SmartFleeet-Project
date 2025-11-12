package com.smartfleet.tracking_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackingUpdate {
    private Long tripId;
    private Double latitude;
    private Double longitude;
    private String timestamp;
}
