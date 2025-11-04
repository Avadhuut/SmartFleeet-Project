package com.smartfleet.driverservice.service;

import com.smartfleet.driverservice.dto.DriverRequest;
import com.smartfleet.driverservice.dto.DriverResponse;
import com.smartfleet.driverservice.entity.Driver;
import com.smartfleet.driverservice.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {


    private final DriverRepository repo;

    @Override
    public List<DriverResponse> getAll() {
        return repo.findAll().stream()
                .map(d -> new DriverResponse(d.getId(), d.getName(), d.getLicenseNumber(), d.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public DriverResponse add(DriverRequest request) {
        Driver driver = new Driver(null, request.getName(), request.getLicenseNumber(), request.getStatus());
        Driver saved = repo.save(driver);
        return new DriverResponse(saved.getId(), saved.getName(), saved.getLicenseNumber(), saved.getStatus());
    }

    @Override
    public DriverResponse update(Long id, DriverRequest request) {
        Driver d = repo.findById(id).orElseThrow(() -> new RuntimeException("Driver not found"));
        d.setName(request.getName());
        d.setLicenseNumber(request.getLicenseNumber());
        d.setStatus(request.getStatus());
        Driver updated = repo.save(d);
        return new DriverResponse(updated.getId(), updated.getName(), updated.getLicenseNumber(), updated.getStatus());
    }

    @Override
    public void delete(Long id) {

        repo.deleteById(id);

    }
}
