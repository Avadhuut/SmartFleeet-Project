package com.smartfleet.tracking_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingUpdate {
    @NotNull private Long tripId;
    @NotNull private Double latitude;
    @NotNull private Double longitude;

    private Instant timestamp;

}
