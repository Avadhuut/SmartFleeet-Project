package com.smartfleet.driverservice.service;

import com.smartfleet.driverservice.dto.DriverRequest;
import com.smartfleet.driverservice.dto.DriverResponse;
import com.smartfleet.driverservice.entity.Driver;
import com.smartfleet.driverservice.exception.DriverNotFoundException;
import com.smartfleet.driverservice.exception.DuplicateDriverException;
import com.smartfleet.driverservice.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository repo;


    @Override
    public List<DriverResponse> getAll() {
        log.info("Fetching all drivers");

        return repo.findAll()
                .stream()
                .map(d -> new DriverResponse(
                        d.getId(),
                        d.getName(),
                        d.getLicenseNumber(),
                        d.getStatus()))
                .collect(Collectors.toList());
    }



    @Override
    @Transactional
    public DriverResponse add(DriverRequest request) {

        log.info("Attempting to create driver with license: {}", request.getLicenseNumber());

        // Check duplicate
        if (repo.existsByLicenseNumber(request.getLicenseNumber())) {
            log.warn("Duplicate driver detected: {}", request.getLicenseNumber());
            throw new DuplicateDriverException("Driver with this license number already exists");
        }

        Driver driver = new Driver(
                null,
                request.getName(),
                request.getLicenseNumber(),
                request.getStatus()
        );

        Driver saved = repo.save(driver);

        log.info("Driver created successfully. ID = {}", saved.getId());

        return new DriverResponse(
                saved.getId(),
                saved.getName(),
                saved.getLicenseNumber(),
                saved.getStatus()
        );
    }


    @Override
    @Transactional
    public DriverResponse update(Long id, DriverRequest request) {

        log.info("Attempting to update driver with ID: {}", id);

        Driver d = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Update failed. Driver not found with ID: {}", id);
                    return new DriverNotFoundException("Driver not found with id: " + id);
                });

        // Only check duplicate if license number is being changed
        if (!d.getLicenseNumber().equals(request.getLicenseNumber())) {

            if (repo.existsByLicenseNumber(request.getLicenseNumber())) {
                log.warn("Duplicate license detected during update: {}", request.getLicenseNumber());
                throw new DuplicateDriverException("Driver with this license number already exists");
            }
        }

        d.setName(request.getName());
        d.setLicenseNumber(request.getLicenseNumber());
        d.setStatus(request.getStatus());

        Driver updated = repo.save(d);

        log.info("Driver updated successfully. ID = {}", updated.getId());

        return new DriverResponse(
                updated.getId(),
                updated.getName(),
                updated.getLicenseNumber(),
                updated.getStatus()
        );
    }



    @Override
    @Transactional
    public void delete(Long id) {

        log.info("Deleting driver with ID: {}", id);

        if (!repo.existsById(id)) {
            log.warn("Delete failed. Driver not found with ID: {}", id);
            throw new DriverNotFoundException("Driver not found with id: " + id);
        }

        repo.deleteById(id);

        log.info("Driver deleted successfully. ID = {}", id);
    }




    @Override
    public DriverResponse getById(Long id) {

        log.info("Fetching driver with ID: {}", id);

        Driver driver = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Driver not found with ID: {}", id);
                    return new DriverNotFoundException("Driver not found with id: " + id);
                });

        return DriverResponse.builder()
                .id(driver.getId())
                .name(driver.getName())
                .licenseNumber(driver.getLicenseNumber())
                .status(driver.getStatus())
                .build();
    }

    @Override
    @Transactional
    public DriverResponse updateStatus(Long id, String status) {

        log.info("Updating driver status -> id={}, status={}", id, status);

        var driver = repo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Driver not found for status update -> id={}", id);
                    return new com.smartfleet.driverservice.exception.DriverNotFoundException("Driver not found with id: " + id);
                });

        driver.setStatus(status);
        var updated = repo.save(driver);

        log.info("Driver status updated -> id={}, status={}", updated.getId(), updated.getStatus());

        return DriverResponse.builder()
                .id(updated.getId())
                .name(updated.getName())
                .licenseNumber(updated.getLicenseNumber())
                .status(updated.getStatus())
                .build();
    }

}
