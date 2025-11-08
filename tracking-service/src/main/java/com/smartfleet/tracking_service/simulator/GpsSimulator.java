package com.smartfleet.tracking_service.simulator;

import com.smartfleet.tracking_service.dto.TrackingUpdate;
import com.smartfleet.tracking_service.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.Random;

@Component
@ConditionalOnProperty(name = "simulator.enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class GpsSimulator {

    private final TrackingManager manager;
    private final TrackingService trackingService;
    private final Random random = new Random();

    @Scheduled(fixedRate = 10_000)
    public void simulateActiveTrips() {
        if (manager.getActiveTrips().isEmpty()) return;

        for (Map.Entry<Long, double[]> entry : manager.getActiveTrips().entrySet()) {
            Long tripId = entry.getKey();
            double[] coords = entry.getValue();

            coords[0] += random.nextDouble() * 0.0005;
            coords[1] += random.nextDouble() * 0.0005;

            TrackingUpdate update = TrackingUpdate.builder()
                    .tripId(tripId)
                    .latitude(coords[0])
                    .longitude(coords[1])
                    .timestamp(Instant.now())
                    .build();

            trackingService.processUpdate(update);
            log.info("📡 Trip {} → lat={}, lon={}", tripId, coords[0], coords[1]);
        }
    }
}
