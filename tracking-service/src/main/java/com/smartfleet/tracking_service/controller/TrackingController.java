package com.smartfleet.tracking_service.controller;

import com.smartfleet.tracking_service.dto.TrackingUpdate;
import com.smartfleet.tracking_service.service.TrackingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Public API for tracking updates.
 */
@RestController
@RequestMapping("/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService service;

    /** save + publish update */
    @PostMapping("/update")
    @ResponseStatus(HttpStatus.CREATED)
    public void update(@Valid @RequestBody TrackingUpdate body) {
        service.processUpdate(body);
    }

    /** get latest location */
    @GetMapping("/{tripId}")
    public TrackingUpdate latest(@PathVariable Long tripId) {
        TrackingUpdate t = service.latest(tripId);
        if (t == null) throw new RuntimeException("No location found for trip " + tripId);
        return t;
    }
}
