package com.smartfleet.alert_service.consumer;

import com.smartfleet.alert_service.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlertConsumer {

    private final AlertService alertService;

    @KafkaListener(topics = "tracking.update", groupId = "alert-group")
    public void consume(String message) {
        // message is the raw JSON string from Kafka
        alertService.processTrackingUpdate(message);
    }
}
