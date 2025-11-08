package com.smartfleet.analytics_service.service;

import com.smartfleet.analytics_service.domain.AnalyticsSummary;
import com.smartfleet.analytics_service.dto.TripStart;
import com.smartfleet.analytics_service.dto.TripComplete;
import com.smartfleet.analytics_service.repo.AnalyticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsRepository repo;

    /**
     * ✅ Safely get today's summary.
     * If it doesn't exist, create one.
     * If duplicates somehow exist, merge and keep only one.
     */
    @Transactional
    public AnalyticsSummary getOrCreateToday() {
        LocalDate today = LocalDate.now();

        // Step 1️⃣ - Fetch all summaries for today (handles duplicates)
        List<AnalyticsSummary> summaries = repo.findAll().stream()
                .filter(s -> s.getDate().equals(today))
                .toList();

        if (summaries.size() > 1) {
            System.err.println("⚠️ Multiple records found for " + today + " — merging into one...");
            AnalyticsSummary main = summaries.get(0);

            for (int i = 1; i < summaries.size(); i++) {
                AnalyticsSummary duplicate = summaries.get(i);
                main.setTotalTrips(main.getTotalTrips() + duplicate.getTotalTrips());
                main.setCompletedTrips(main.getCompletedTrips() + duplicate.getCompletedTrips());
                main.setTotalDurationMinutes(main.getTotalDurationMinutes() + duplicate.getTotalDurationMinutes());
                repo.delete(duplicate);
            }

            repo.save(main);
            return main;
        }

        // Step 2️⃣ - If one record exists, return it
        if (summaries.size() == 1) {
            return summaries.get(0);
        }

        // Step 3️⃣ - If no record exists, create a new one
        AnalyticsSummary newSummary = AnalyticsSummary.builder()
                .date(today)
                .totalTrips(0)
                .completedTrips(0)
                .totalDurationMinutes(0)
                .avgTripDuration(0)
                .build();

        repo.save(newSummary);
        System.out.println("🆕 Created new AnalyticsSummary for " + today);
        return newSummary;
    }

    /**
     * ✅ Handles 'trip.start' Kafka event.
     */
    @Transactional
    public void handleTripStart(TripStart event) {
        try {
            AnalyticsSummary summary = getOrCreateToday();
            summary.setTotalTrips(summary.getTotalTrips() + 1);
            repo.save(summary);
            System.out.println("📈 trip.start processed: tripId=" + event.getTripId()
                    + " → totalTrips=" + summary.getTotalTrips());
        } catch (Exception e) {
            System.err.println("❌ Error updating totalTrips: " + e.getMessage());
        }
    }

    /**
     * ✅ Handles 'trip.complete' Kafka event.
     */
    @Transactional
    public void handleTripComplete(TripComplete event) {
        try {
            AnalyticsSummary summary = getOrCreateToday();

            // Increment counts
            summary.setCompletedTrips(summary.getCompletedTrips() + 1);
            summary.setTotalDurationMinutes(summary.getTotalDurationMinutes() + event.getDurationMinutes());

            // Recalculate average
            if (summary.getCompletedTrips() > 0) {
                summary.setAvgTripDuration(
                        (double) summary.getTotalDurationMinutes() / summary.getCompletedTrips()
                );
            }

            repo.save(summary);
            System.out.println("✅ trip.complete processed: tripId=" + event.getTripId()
                    + " → completedTrips=" + summary.getCompletedTrips()
                    + ", avg=" + summary.getAvgTripDuration());
        } catch (Exception e) {
            System.err.println("❌ Error updating trip completion: " + e.getMessage());
        }
    }
}
