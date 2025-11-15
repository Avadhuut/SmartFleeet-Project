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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
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

        log.info("Creating new Trip | tripName={}, driverId={}, vehicleId={}",
                request.getTripName(), request.getDriverId(), request.getVehicleId());

        // Fetch driver & vehicle
        DriverDto driver;
        VehicleDto vehicle;

        try {
            driver = driverClient.getDriverById(request.getDriverId());
            log.info("Driver validation successful → driverId={}", request.getDriverId());
        } catch (Exception e) {
            log.warn("Driver not found → driverId={}", request.getDriverId());
            throw new NotFoundException("Driver not found with ID: " + request.getDriverId());
        }

        try {
            vehicle = fleetClient.getVehicleById(request.getVehicleId());
            log.info("Vehicle validation successful → vehicleId={}", request.getVehicleId());
        } catch (Exception e) {
            log.warn("Vehicle not found → vehicleId={}", request.getVehicleId());
            throw new NotFoundException("Vehicle not found with ID: " + request.getVehicleId());
        }

        // Status validation
        if (!"AVAILABLE".equalsIgnoreCase(driver.getStatus())) {
            log.warn("Driver not AVAILABLE → driverId={}, status={}", driver.getId(), driver.getStatus());
            throw new BadRequestException("Driver is not AVAILABLE");
        }

        if (!"AVAILABLE".equalsIgnoreCase(vehicle.getStatus())) {
            log.warn("Vehicle not AVAILABLE → vehicleId={}, status={}", vehicle.getId(), vehicle.getStatus());
            throw new BadRequestException("Vehicle is not AVAILABLE");
        }

        // Create Trip entity
        Trip trip = Trip.builder()
                .tripName(request.getTripName())
                .driverId(request.getDriverId())
                .vehicleId(request.getVehicleId())
                .status(TripStatus.CREATED)
                .build();

        // Save to database
        Trip savedTrip = tripRepository.save(trip);
        log.info("Trip saved successfully → tripId={}", savedTrip.getId());

        //  UPDATE DRIVER & FLEET STATUS
        try {
            driverClient.updateDriverStatus(savedTrip.getDriverId(), Map.of("status", "ON_TRIP"));
            log.info("Driver status updated → id={}, status=ON_TRIP", savedTrip.getDriverId());
        } catch (Exception e) {
            log.error("Failed to update driver status!", e);
        }

        try {
            fleetClient.updateVehicleStatus(savedTrip.getVehicleId(), Map.of("status", "ASSIGNED"));
            log.info("Fleet status updated → id={}, status=ASSIGNED", savedTrip.getVehicleId());
        } catch (Exception e) {
            log.error("Failed to update fleet status!", e);
        }

        // Notify Tracking-Service
        try {
            StartTrackingRequest trackingRequest = new StartTrackingRequest(
                    savedTrip.getId(),
                    savedTrip.getVehicleId(),
                    18.5204,
                    73.8567
            );

            trackingClient.startTracking(trackingRequest);
            log.info("Tracking started for tripId={}", savedTrip.getId());

        } catch (Exception e) {
            log.error("Failed to notify Tracking-Service for tripId={}", savedTrip.getId());
        }

        // Send Kafka event
        tripEventProducer.sendTripStart(
                new TripStartEvent(
                        savedTrip.getId(),
                        savedTrip.getDriverId(),
                        savedTrip.getVehicleId(),
                        savedTrip.getTripName(),
                        LocalDateTime.now().toString()
                )
        );

        log.info("trip.start event sent → tripId={}", savedTrip.getId());

        return toResponse(savedTrip);
    }

    /**
     * Complete a trip and send Kafka event.
     */
    @Transactional
    public TripResponse completeTrip(Long tripId) {

        log.info("Completing Trip → tripId={}", tripId);

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> {
                    log.warn("Trip not found → tripId={}", tripId);
                    return new NotFoundException("Trip not found with ID: " + tripId);
                });

        if (trip.getStatus() == TripStatus.COMPLETED) {
            log.warn("Trip already completed → tripId={}", tripId);
            throw new BadRequestException("Trip is already completed");
        }

        // Update trip status
        trip.setStatus(TripStatus.COMPLETED);
        Trip updatedTrip = tripRepository.save(trip);

        log.info("Trip marked COMPLETED → tripId={}", updatedTrip.getId());

        // Stop tracking
        try {
            trackingClient.stopTracking(updatedTrip.getId());
            log.info("Tracking stopped for tripId={}", updatedTrip.getId());
        } catch (Exception e) {
            log.error("Failed to stop tracking → tripId={}", updatedTrip.getId());
        }

        long duration = (long) (60 + Math.random() * 60);

        // Send Kafka event
        tripEventProducer.sendTripComplete(
                new TripCompleteEvent(
                        updatedTrip.getId(),
                        updatedTrip.getDriverId(),
                        updatedTrip.getVehicleId(),
                        updatedTrip.getTripName(),
                        duration,
                        LocalDateTime.now().toString()
                )
        );

        log.info("trip.complete event sent → tripId={}", updatedTrip.getId());

        // Reset Driver & Fleet status to AVAILABLE
        try {
            driverClient.updateDriverStatus(updatedTrip.getDriverId(), Map.of("status", "AVAILABLE"));
            log.info("Driver status reset → id={}, status=AVAILABLE", updatedTrip.getDriverId());
        } catch (Exception e) {
            log.error("Failed to reset driver status → driverId={}", updatedTrip.getDriverId());
        }

        try {
            fleetClient.updateVehicleStatus(updatedTrip.getVehicleId(), Map.of("status", "AVAILABLE"));
            log.info("Fleet status reset → id={}, status=AVAILABLE", updatedTrip.getVehicleId());
        } catch (Exception e) {
            log.error("Failed to reset fleet status → vehicleId={}", updatedTrip.getVehicleId());
        }


        return toResponse(updatedTrip);
    }

    /**
     * Fetch all trips.
     */
    public List<TripResponse> getAllTrips() {
        log.info("Fetching all trips");
        return tripRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Fetch single trip by ID.
     */
    public TripResponse getTripById(Long id) {
        log.info("Fetching Trip by ID → tripId={}", id);

        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Trip not found → tripId={}", id);
                    return new NotFoundException("Trip not found with ID: " + id);
                });

        return toResponse(trip);
    }

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
