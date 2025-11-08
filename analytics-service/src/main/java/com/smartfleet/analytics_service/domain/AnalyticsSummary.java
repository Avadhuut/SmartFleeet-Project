package com.smartfleet.analytics_service.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;              // Daily summary date
    private long totalTrips;             // Total trips created
    private long completedTrips;         // Total completed trips
    private long totalDurationMinutes;   // Sum of trip durations
    private double avgTripDuration;      // Average duration
}
