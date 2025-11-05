package com.smartfleet.trip_service.client;

import com.smartfleet.trip_service.dto.DriverDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "driver-service", url = "${client.driver-service.url}")
public interface DriverClient {

    @GetMapping("/api/driver/{id}")
    DriverDto getDriverById(@PathVariable Long id);
}
