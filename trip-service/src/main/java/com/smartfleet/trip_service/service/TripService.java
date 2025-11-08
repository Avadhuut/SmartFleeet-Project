package com.smartfleet.trip_service.service;

import com.smartfleet.trip_service.client.DriverClient;
import com.smartfleet.trip_service.client.FleetClient;
import com.smartfleet.trip_service.client.TrackingClient;
import com.smartfleet.trip_service.dto.*;
import com.smartfleet.trip_service.entity.Trip;
import com.smartfleet.trip_service.exception.BadRequestException;
import com.smartfleet.trip_service.exception.NotFoundException;
import com.smartfleet.trip_service.model.TripStatus;
import com.smartfleet.trip_service.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final DriverClient driverClient;
    private final FleetClient fleetClient;

    private final TrackingClient trackingClient;


    @Transactional
    public TripResponse createTrip(CreateTripRequest request) {

        // 1️⃣ Fetch driver and vehicle using Feign clients
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

        // 5️⃣ Notify Tracking-Service to start tracking this trip
        try {
            trackingClient.startTracking(savedTrip.getId(), 18.5204, 73.8567); // static start coords for now
            System.out.println("✅ Tracking started for Trip ID: " + savedTrip.getId());
        } catch (Exception e) {
            System.err.println("⚠️ Failed to notify tracking-service for Trip ID: " + savedTrip.getId());
            e.printStackTrace();
        }

        // 6 Convert entity to response DTO
        return toResponse(savedTrip);
    }

    public List<TripResponse> getAllTrips() {
        return tripRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public TripResponse getTripById(Long id) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Trip not found with ID: " + id));
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
