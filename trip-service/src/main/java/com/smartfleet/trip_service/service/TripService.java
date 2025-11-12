package com.smartfleet.trip_service.service;

import com.smartfleet.trip_service.client.DriverClient;
import com.smartfleet.trip_service.client.FleetClient;
import com.smartfleet.trip_service.client.TrackingClient;
import com.smartfleet.trip_service.dto.*;
import com.smartfleet.trip_service.entity.Trip;
import com.smartfleet.trip_service.events.TripStartEvent;
import com.smartfleet.trip_service.events.TripCompleteEvent;
import com.smartfleet.trip_service.exception.BadRequestException;
import com.smartfleet.trip_service.exception.NotFoundException;
import com.smartfleet.trip_service.model.TripStatus;
import com.smartfleet.trip_service.producer.TripEventProducer;
import com.smartfleet.trip_service.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final DriverClient driverClient;
    private final FleetClient fleetClient;
    private final TrackingClient trackingClient;
    private final TripEventProducer tripEventProducer;

    /**
     * Create a new trip, validate driver/vehicle, start tracking, and send Kafka event.
     */
    @Transactional
    public TripResponse createTrip(CreateTripRequest request) {

        // 1️⃣ Fetch driver and vehicle details via Feign clients
        DriverDto driver;
        VehicleDto vehicle;

        try {
            driver = driverClient.getDriverById(request.getDriverId());
        } catch (Exception e) {
            throw new NotFoundException("Driver not found with ID: " + request.getDriverId());
        }

        try {
            vehicle = fleetClient.getVehicleById(request.getVehicleId());
        } catch (Exception e) {
            throw new NotFoundException("Vehicle not found with ID: " + request.getVehicleId());
        }

        // 2️⃣ Validate statuses
        if (!"AVAILABLE".equalsIgnoreCase(driver.getStatus())) {
            throw new BadRequestException("Driver is not AVAILABLE");
        }

        if (!"AVAILABLE".equalsIgnoreCase(vehicle.getStatus())) {
            throw new BadRequestException("Vehicle is not AVAILABLE");
        }

        // 3️⃣ Create Trip entity
        Trip trip = Trip.builder()
                .tripName(request.getTripName())
                .driverId(request.getDriverId())
                .vehicleId(request.getVehicleId())
                .status(TripStatus.CREATED)
                .build();

        // 4️⃣ Save to DB
        Trip savedTrip = tripRepository.save(trip);

        // 5️⃣ Notify Tracking-Service
        try {
            // Create a tracking request with hardcoded latitude and longitude
            StartTrackingRequest trackingRequest = new StartTrackingRequest(
                    savedTrip.getId(),        // Trip ID
                    savedTrip.getVehicleId(), // Vehicle ID (optional)
                    18.5204,                  // Hardcoded Latitude (Pune)
                    73.8567                   // Hardcoded Longitude (Pune)
            );

            trackingClient.startTracking(trackingRequest);
            System.out.println("Tracking started for Trip ID: " + savedTrip.getId());

        } catch (Exception e) {
            System.err.println(" Failed to notify Tracking-Service for Trip ID: " + savedTrip.getId());
            e.printStackTrace();
        }

        // 6️⃣ Send Kafka Event - trip.start
        tripEventProducer.sendTripStart(new TripStartEvent(
                savedTrip.getId(),
                savedTrip.getDriverId(),
                savedTrip.getVehicleId(),
                savedTrip.getTripName(),
                LocalDateTime.now().toString()
        ));

        // 7️⃣ Convert and return response
        return toResponse(savedTrip);
    }

    /**
     * Mark a trip as completed and send Kafka event.
     */
    @Transactional
    public TripResponse completeTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new NotFoundException("Trip not found with ID: " + tripId));

        if (trip.getStatus() == TripStatus.COMPLETED) {
            throw new BadRequestException("Trip is already completed");
        }

        // 1) Update trip status and save
        trip.setStatus(TripStatus.COMPLETED);
        Trip updatedTrip = tripRepository.save(trip);

        // 2) Optionally compute duration
        long durationMinutes = (long) (60 + Math.random() * 60);

        // 3) Tell Tracking-Service to stop tracking (best-effort)
        try {
            trackingClient.stopTracking(updatedTrip.getId());
            System.out.println("Tracking stopped for Trip ID: " + updatedTrip.getId());
        } catch (Exception e) {
            // Do not fail the whole transaction because of a tracking service issue.
            // Log and continue. If you want stronger guarantees, consider retry policy.
            System.err.println("Failed to stop tracking for Trip ID: " + updatedTrip.getId());
            e.printStackTrace();
        }

        // 4) Send Kafka Event - trip.complete
        tripEventProducer.sendTripComplete(new TripCompleteEvent(
                updatedTrip.getId(),
                updatedTrip.getDriverId(),
                updatedTrip.getVehicleId(),
                updatedTrip.getTripName(),
                durationMinutes,
                LocalDateTime.now().toString()
        ));

        return toResponse(updatedTrip);
    }

    /**
     * Fetch all trips.
     */
    public List<TripResponse> getAllTrips() {
        return tripRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Fetch a single trip by ID.
     */
    public TripResponse getTripById(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Trip not found with ID: " + id));
        return toResponse(trip);
    }

    /**
     * Convert entity to response DTO.
     */
    private TripResponse toResponse(Trip trip) {
        return TripResponse.builder()
                .id(trip.getId())
                .tripName(trip.getTripName())
                .driverId(trip.getDriverId())
                .vehicleId(trip.getVehicleId())
                .status(trip.getStatus())
                .build();
    }
}
