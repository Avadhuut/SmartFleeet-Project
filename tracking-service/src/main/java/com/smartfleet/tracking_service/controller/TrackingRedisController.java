package com.smartfleet.tracking_service.controller;

import com.smartfleet.tracking_service.dto.TrackingUpdate;
import com.smartfleet.tracking_service.store.LocationStore;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

/**
 * Controller to test storing and fetching from Redis.
 */
@RestController
@RequestMapping("/tracking-redis")
@RequiredArgsConstructor
public class TrackingRedisController {

    private final LocationStore store;

    @PostMapping("/update")
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@Valid @RequestBody TrackingUpdate body) {
        // if timestamp not set, use current time
        if (body.getTimestamp() == null) body.setTimestamp(Instant.now());
        store.save(body);
    }

    @GetMapping("/{tripId}")
    public TrackingUpdate latest(@PathVariable Long tripId) {
        TrackingUpdate data = store.get(tripId);
        if (data == null)
            throw new RuntimeException("No location found for trip " + tripId);
        return data;
    }
}
