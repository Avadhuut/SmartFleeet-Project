package com.smartfleet.trip_service.client;

import com.smartfleet.trip_service.dto.DriverDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "DRIVER-SERVICE")
public interface DriverClient {

    @GetMapping("/api/driver/{id}")
    DriverDto getDriverById(@PathVariable Long id);

    @PutMapping("/api/driver/{id}/status")
    void updateDriverStatus(@PathVariable Long id, @RequestBody Map<String, String> status);

}
