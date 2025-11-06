package com.smartfleet.tracking_service.event;

import com.smartfleet.tracking_service.dto.TrackingUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Sends tracking updates to Kafka topic.
 */
@Component
@RequiredArgsConstructor
public class TrackingProducer {

    private final KafkaTemplate<String, Object> kafka;
    @Value("${topic.tracking.update}") private String topic;

    public void publish(TrackingUpdate update) {
        kafka.send(topic, String.valueOf(update.getTripId()), update);
    }
}
