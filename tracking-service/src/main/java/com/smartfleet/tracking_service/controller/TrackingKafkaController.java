package com.smartfleet.tracking_service.controller;

import com.smartfleet.tracking_service.dto.TrackingUpdate;
import com.smartfleet.tracking_service.event.TrackingProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to test publishing messages to Kafka.
 */
@RestController
@RequestMapping("/tracking-kafka")
@RequiredArgsConstructor
public class TrackingKafkaController {

    private final TrackingProducer producer;

    @PostMapping("/publish")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void publish(@Valid @RequestBody TrackingUpdate update) {
        producer.publish(update);
    }
}
