package com.smartfleet.trip_service.client;

import com.smartfleet.trip_service.dto.VehicleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "fleet-service", url = "${client.fleet-service.url}")
public interface FleetClient {

    @GetMapping("/api/fleet/{id}")
    VehicleDto getVehicleById(@PathVariable Long id);
}
