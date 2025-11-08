package com.smartfleet.trip_service.controller;

import com.smartfleet.trip_service.dto.CreateTripRequest;
import com.smartfleet.trip_service.dto.TripResponse;
import com.smartfleet.trip_service.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/trip")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    /**
     * Create a new trip.
     */
    @PostMapping("/create")
    public ResponseEntity<TripResponse> createTrip(@Valid @RequestBody CreateTripRequest request) {
        TripResponse trip = tripService.createTrip(request);
        return ResponseEntity.created(URI.create("/trip/" + trip.getId())).body(trip);
    }

    /**
     * Complete a trip by ID.
     */
    @PostMapping("/complete/{tripId}")
    public ResponseEntity<TripResponse> completeTrip(@PathVariable Long tripId) {
        TripResponse trip = tripService.completeTrip(tripId);
        return ResponseEntity.ok(trip);
    }

    /**
     * Get all trips.
     */
    @GetMapping("/all")
    public List<TripResponse> getAllTrips() {
        return tripService.getAllTrips();
    }

    /**
     * Get a trip by ID.
     */
    @GetMapping("/{id}")
    public TripResponse getTripById(@PathVariable Long id) {
        return tripService.getTripById(id);
    }
}
