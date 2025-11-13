package com.smartfleet.tracking_service.service;

import com.smartfleet.tracking_service.dto.StartTrackingRequest;
import com.smartfleet.tracking_service.dto.TrackingUpdate;
import com.smartfleet.tracking_service.kafka.TrackingProducer;
import com.smartfleet.tracking_service.model.Tracker;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TrackingManager {

    // 1️⃣ Redis key prefix - all tracker data in Redis will start with "tracker:"
    private static final String REDIS_KEY_PREFIX = "tracker:";

    private final Map<Long, Tracker> trackers = new ConcurrentHashMap<>();
    private final TrackingProducer trackingProducer;

    // 2️⃣ RedisTemplate - this is how we talk to Redis database
    private final RedisTemplate<String, Object> redisTemplate;

    // 3️⃣ Updated constructor - inject RedisTemplate
    public TrackingManager(TrackingProducer trackingProducer,
                           RedisTemplate<String, Object> redisTemplate) {
        this.trackingProducer = trackingProducer;
        this.redisTemplate = redisTemplate;
    }


    @EventListener(ApplicationReadyEvent.class)
    public void loadTrackersFromRedis() {
        System.out.println("Loading trackers from Redis...");

        // Get all keys that start with "tracker:"
        Set<String> keys = redisTemplate.keys(REDIS_KEY_PREFIX + "*");

        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                // Load each tracker from Redis
                Tracker tracker = (Tracker) redisTemplate.opsForValue().get(key);

                // Only load if tracker exists and is still active
                if (tracker != null && tracker.isActive()) {
                    trackers.put(tracker.getTripId(), tracker);
                    System.out.println("Restored trip: " + tracker.getTripId()
                            + " at (" + tracker.getLatitude() + ", " + tracker.getLongitude() + ")");
                }
            }
        }

        System.out.println("📦 Total active trackers restored: " + trackers.size());
    }

    // 5️⃣ Updated startTracking - now saves to Redis too
    public void startTracking(StartTrackingRequest req) {
        Tracker tracker = new Tracker(req.getTripId(), req.getStartLatitude(), req.getStartLongitude());

        // Save to memory (ConcurrentHashMap) for fast access
        trackers.put(req.getTripId(), tracker);

        // Save to Redis for persistence
        String redisKey = REDIS_KEY_PREFIX + req.getTripId();
        redisTemplate.opsForValue().set(redisKey, tracker);

        System.out.println("Started tracking trip: " + req.getTripId() + " ( saved to Redis)");
    }

    // 6️⃣ Updated stopTracking - updates Redis when stopping
    public void stopTracking(Long tripId) {
        Tracker tracker = trackers.remove(tripId);

        if (tracker != null) {
            tracker.stop();

            // Update in Redis - mark as inactive
            String redisKey = REDIS_KEY_PREFIX + tripId;
            redisTemplate.opsForValue().set(redisKey, tracker);

            // OR delete from Redis completely (choose one approach)
            // redisTemplate.delete(redisKey);

            System.out.println("Stopped tracking trip: " + tripId + " (updated in Redis)");
        }
    }

    public Collection<Tracker> getActiveTrackers() {
        return trackers.values();
    }

    // 7️⃣ Updated handleSimulatedPosition - saves position updates to Redis
    public void handleSimulatedPosition(TrackingUpdate update) {
        Tracker t = trackers.get(update.getTripId());
        if (t == null || !t.isActive()) return;

        // Update position in memory
        t.setLatitude(update.getLatitude());
        t.setLongitude(update.getLongitude());

        // Save updated position to Redis (so it's persisted)
        String redisKey = REDIS_KEY_PREFIX + update.getTripId();
        redisTemplate.opsForValue().set(redisKey, t);

        System.out.println(" Trip: " + t.getTripId() +
                " | Lat: " + update.getLatitude() +
                " | Lon: " + update.getLongitude() +
                " | Time: " + update.getTimestamp() );

        // Send this tracking update to Kafka
        trackingProducer.sendTrackingUpdate(update);
    }
}