package com.smartfleet.trip_service.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartfleet.trip_service.events.TripStartEvent;
import com.smartfleet.trip_service.events.TripCompleteEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public void sendTripStart(TripStartEvent event) {
        try {
            String json = mapper.writeValueAsString(event);
            kafkaTemplate.send("trip.start", json);
            System.out.println("✅ Sent trip.start event: " + json);
        } catch (Exception e) {
            System.err.println("❌ Failed to send trip.start event: " + e.getMessage());
        }
    }

    public void sendTripComplete(TripCompleteEvent event) {
        try {
            String json = mapper.writeValueAsString(event);
            kafkaTemplate.send("trip.complete", json);
            System.out.println("✅ Sent trip.complete event: " + json);
        } catch (Exception e) {
            System.err.println("❌ Failed to send trip.complete event: " + e.getMessage());
        }
    }
}
