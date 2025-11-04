package com.smartfleet.fleetservice.Service;

import com.smartfleet.fleetservice.Entity.Fleet;
import com.smartfleet.fleetservice.Repository.FleetRepository;
import com.smartfleet.fleetservice.dto.FleetRequest;
import com.smartfleet.fleetservice.dto.FleetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FleetServiceImpl implements FleetService {

    private final FleetRepository repo;


    @Override
    public List<FleetResponse> getAll() {
        return repo.findAll().stream()
                .map(f -> new FleetResponse(f.getId(), f.getVehicleNumber(), f.getType(), f.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public FleetResponse add(FleetRequest request) {
        Fleet fleet = new Fleet(null, request.getVehicleNumber(), request.getType(), request.getStatus());
        Fleet saved = repo.save(fleet);
        return new FleetResponse(saved.getId(), saved.getVehicleNumber(), saved.getType(), saved.getStatus());
    }


    @Override
    public FleetResponse update(Long id, FleetRequest request) {
        Fleet f = repo.findById(id).orElseThrow(() -> new RuntimeException("Fleet not found"));
        f.setType(request.getType());
        f.setStatus(request.getStatus());
        Fleet updated = repo.save(f);
        return new FleetResponse(updated.getId(), updated.getVehicleNumber(), updated.getType(), updated.getStatus());
    }

    @Override
    public void delete(Long id) {

        if (!repo.existsById(id)) {
            throw new RuntimeException("Fleet not found with ID: " + id);
        }
        repo.deleteById(id);

    }
}
