package com.smartfleet.trip_service.client;

import com.smartfleet.trip_service.dto.StartTrackingRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "TRACKING-SERVICE")
public interface TrackingClient {

    @PostMapping("/api/tracking/start")
    void startTracking(@RequestBody StartTrackingRequest request);

    @PostMapping("/api/tracking/stop/{tripId}")
    void stopTracking(@PathVariable("tripId") Long tripId);
}
