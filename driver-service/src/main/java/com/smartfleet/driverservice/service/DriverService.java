package com.smartfleet.driverservice.service;

import com.smartfleet.driverservice.dto.DriverRequest;
import com.smartfleet.driverservice.dto.DriverResponse;

import java.util.List;

public interface DriverService {
    List<DriverResponse> getAll();
    DriverResponse add(DriverRequest request);
    DriverResponse update(Long id, DriverRequest request);
    void delete(Long id);

    DriverResponse getById(Long id);

    DriverResponse updateStatus(Long id, String status);

}
