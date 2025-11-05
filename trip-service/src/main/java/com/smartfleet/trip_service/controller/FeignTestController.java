package com.smartfleet.trip_service.controller;

import com.smartfleet.trip_service.client.DriverClient;
import com.smartfleet.trip_service.client.FleetClient;
import com.smartfleet.trip_service.dto.DriverDto;
import com.smartfleet.trip_service.dto.VehicleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trip/test/feign")
@RequiredArgsConstructor
public class FeignTestController {

    private final DriverClient driverClient;
    private final FleetClient fleetClient;

    @GetMapping("/driver/{id}")
    public DriverDto testDriver(@PathVariable Long id) {
        return driverClient.getDriverById(id);
    }

    @GetMapping("/vehicle/{id}")
    public VehicleDto testVehicle(@PathVariable Long id) {
        return fleetClient.getVehicleById(id);
    }
}
