package com.smartfleet.trip_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "tracking-service", url = "${client.tracking-service.url}")
public interface TrackingClient {

    @PostMapping("/tracking/start/{tripId}")
    void startTracking(@PathVariable Long tripId,
                       @RequestParam("lat") double latitude,
                       @RequestParam("lon") double longitude);
}
