package com.smartfleet.tracking_service.service;

import com.smartfleet.tracking_service.dto.TrackingUpdate;
import com.smartfleet.tracking_service.event.TrackingProducer;
import com.smartfleet.tracking_service.store.LocationStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Main business logic for tracking updates:
 * - add timestamp
 * - save to Redis
 * - publish to Kafka
 */
@Service
@RequiredArgsConstructor
public class TrackingService {

    private final LocationStore store;        // handles Redis
    private final TrackingProducer producer;  // handles Kafka

    /** process incoming update */
    public void processUpdate(TrackingUpdate update) {
        if (update.getTimestamp() == null) update.setTimestamp(Instant.now());
        store.save(update);        // save latest in Redis
        producer.publish(update);  // broadcast to Kafka
    }

    /** get latest from Redis */
    public TrackingUpdate latest(Long tripId) {
        return store.get(tripId);
    }
}
