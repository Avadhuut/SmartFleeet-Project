package com.smartfleet.tracking_service.kafka;

import com.smartfleet.tracking_service.dto.TrackingUpdate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TrackingProducer {

    private final KafkaTemplate<String, TrackingUpdate> kafkaTemplate;
    private static final String TOPIC = "tracking.update"; // same topic alert-service listens on

    public TrackingProducer(KafkaTemplate<String, TrackingUpdate> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTrackingUpdate(TrackingUpdate update) {
        kafkaTemplate.send(TOPIC, update.getTripId().toString(), update);
        System.out.println("[Kafka] Sent tracking update → Trip: " + update.getTripId() +
                " | Lat: " + update.getLatitude() +
                " | Lon: " + update.getLongitude() +
                " | Time: " + update.getTimestamp());
    }
}
