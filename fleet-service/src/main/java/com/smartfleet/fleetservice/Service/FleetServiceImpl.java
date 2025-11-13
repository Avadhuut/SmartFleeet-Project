package com.smartfleet.fleetservice.Service;

import com.smartfleet.fleetservice.dto.FleetRequest;
import com.smartfleet.fleetservice.dto.FleetResponse;
import com.smartfleet.fleetservice.Entity.Fleet;
import com.smartfleet.fleetservice.exception.DuplicateFleetException;
import com.smartfleet.fleetservice.exception.FleetNotFoundException;
import com.smartfleet.fleetservice.Repository.FleetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FleetServiceImpl implements FleetService {

    private final FleetRepository repo;

    // GET ALL FLEETS
    @Override
    public List<FleetResponse> getAll() {

        log.info("Fetching all fleet entries");

        return repo.findAll()
                .stream()
                .map(f -> new FleetResponse(
                        f.getId(),
                        f.getVehicleNumber(),
                        f.getType(),
                        f.getStatus()))
                .collect(Collectors.toList());
    }


    // CREATE FLEET
    @Override
    @Transactional
    public FleetResponse add(FleetRequest request) {

        log.info("Attempting to add fleet with vehicle number: {}", request.getVehicleNumber());

        // Duplicate check
        if (repo.existsByVehicleNumber(request.getVehicleNumber())) {
            log.warn("Duplicate fleet detected for vehicle: {}", request.getVehicleNumber());
            throw new DuplicateFleetException("Fleet with this vehicle number already exists");
        }

        Fleet fleet = new Fleet(
                null,
                request.getVehicleNumber(),
                request.getType(),
                request.getStatus()
        );

        Fleet saved = repo.save(fleet);

        log.info("Fleet saved successfully. ID = {}", saved.getId());

        return new FleetResponse(
                saved.getId(),
                saved.getVehicleNumber(),
                saved.getType(),
                saved.getStatus()
        );
    }


    // UPDATE FLEET
    @Override
    @Transactional
    public FleetResponse update(Long id, FleetRequest request) {

        log.info("Attempting to update fleet with ID: {}", id);

        Fleet fleet = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Fleet not found with ID: {}", id);
                    return new FleetNotFoundException("Fleet not found with id: " + id);
                });

        // If vehicleNumber is changed → check duplicate
        if (!fleet.getVehicleNumber().equals(request.getVehicleNumber())) {

            if (repo.existsByVehicleNumber(request.getVehicleNumber())) {
                log.warn("Duplicate vehicle number during update: {}", request.getVehicleNumber());
                throw new DuplicateFleetException("Fleet with this vehicle number already exists");
            }

            fleet.setVehicleNumber(request.getVehicleNumber());
        }

        fleet.setType(request.getType());
        fleet.setStatus(request.getStatus());

        Fleet updated = repo.save(fleet);

        log.info("Fleet updated successfully. ID = {}", updated.getId());

        return new FleetResponse(
                updated.getId(),
                updated.getVehicleNumber(),
                updated.getType(),
                updated.getStatus()
        );
    }


    // DELETE FLEET
    @Override
    @Transactional
    public void delete(Long id) {

        log.info("Attempting to delete fleet with ID: {}", id);

        if (!repo.existsById(id)) {
            log.warn("Delete failed. Fleet not found with ID: {}", id);
            throw new FleetNotFoundException("Fleet not found with id: " + id);
        }

        repo.deleteById(id);

        log.info("Fleet deleted successfully. ID = {}", id);
    }


    // GET FLEET BY ID
    @Override
    public FleetResponse getById(Long id) {

        log.info("Fetching fleet with ID: {}", id);

        Fleet fleet = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Fleet not found with ID: {}", id);
                    return new FleetNotFoundException("Fleet not found with id: " + id);
                });

        return new FleetResponse(
                fleet.getId(),
                fleet.getVehicleNumber(),
                fleet.getType(),
                fleet.getStatus()
        );
    }
}
