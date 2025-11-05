package com.smartfleet.fleetservice.Service;

import com.smartfleet.fleetservice.dto.FleetRequest;
import com.smartfleet.fleetservice.dto.FleetResponse;

import java.util.List;

public interface FleetService {
    List<FleetResponse> getAll();
    FleetResponse add(FleetRequest request);
    FleetResponse update(Long id, FleetRequest request);
    void delete(Long id);

    FleetResponse getById(Long id);

}
