package com.smartfleet.trip_service.controller;

import com.smartfleet.trip_service.dto.CreateTripRequest;
import com.smartfleet.trip_service.dto.TripResponse;
import com.smartfleet.trip_service.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/trip")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    /**
     * Create a new trip.
     */
    @PostMapping
    public ResponseEntity<TripResponse> createTrip(@Valid @RequestBody CreateTripRequest request) {

        log.info("API Request → Create Trip | tripName={}, driverId={}, vehicleId={}",
                request.getTripName(), request.getDriverId(), request.getVehicleId());

        TripResponse trip = tripService.createTrip(request);

        log.info("Trip Created Successfully → tripId={}", trip.getId());

        return ResponseEntity.created(URI.create("/api/trip/" + trip.getId()))
                .body(trip);
    }

    /**
     * Complete a trip by ID.
     */
    @PostMapping("/{tripId}")
    public ResponseEntity<TripResponse> completeTrip(@PathVariable Long tripId) {

        log.info("API Request → Complete Trip | tripId={}", tripId);

        TripResponse trip = tripService.completeTrip(tripId);

        log.info("Trip Completed Successfully → tripId={}", trip.getId());

        return ResponseEntity.ok(trip);
    }

    /**
     * Get all trips.
     */
    @GetMapping
    public List<TripResponse> getAllTrips() {

        log.info("API Request → Get All Trips");

        return tripService.getAllTrips();
    }

    /**
     * Get a trip by ID.
     */
    @GetMapping("/{id}")
    public TripResponse getTripById(@PathVariable Long id) {

        log.info("API Request → Get Trip by ID | tripId={}", id);

        return tripService.getTripById(id);
    }
}
