package com.smartfleet.trip_service.client;


import com.smartfleet.trip_service.dto.VehicleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "FLEET-SERVICE")
public interface FleetClient {

    @GetMapping("/api/fleet/{id}")
    VehicleDto getVehicleById(@PathVariable Long id);

    @PutMapping("/api/fleet/{id}/status")
    void updateVehicleStatus(@PathVariable Long id, @RequestBody Map<String, String> status);
}
