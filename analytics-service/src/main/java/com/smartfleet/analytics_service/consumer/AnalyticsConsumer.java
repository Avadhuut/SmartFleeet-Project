package com.smartfleet.analytics_service.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartfleet.analytics_service.dto.TripStart;
import com.smartfleet.analytics_service.dto.TripComplete;
import com.smartfleet.analytics_service.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalyticsConsumer {

    private final AnalyticsService analyticsService;
    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "trip.start", groupId = "analytics-group")
    public void onTripStart(String message) {
        System.out.println("📩 Received trip.start message: " + message);
        try {
            TripStart event = mapper.readValue(message, TripStart.class);
            analyticsService.handleTripStart(event);
        } catch (Exception e) {
            System.err.println("❌ Error processing trip.start: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @KafkaListener(topics = "trip.complete", groupId = "analytics-group")
    public void onTripComplete(String message) {
        try {
            TripComplete event = mapper.readValue(message, TripComplete.class);
            analyticsService.handleTripComplete(event);
        } catch (Exception e) {
            System.err.println("❌ Error reading trip.complete event: " + e.getMessage());
        }
    }
}
