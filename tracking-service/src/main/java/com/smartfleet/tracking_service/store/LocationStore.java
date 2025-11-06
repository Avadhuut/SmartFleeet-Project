package com.smartfleet.tracking_service.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smartfleet.tracking_service.dto.TrackingUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * Handles saving and fetching latest trip locations from Redis.
 */
@Component
@RequiredArgsConstructor
public class LocationStore {

    private final StringRedisTemplate redis;   // built-in Redis helper
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Value("${tracking.redis.ttl-seconds:900}")
    private long ttlSeconds;

    private String key(Long tripId) {
        return "trip:" + tripId; // ex: trip:101
    }

    /** Save latest location for a trip */
    public void save(TrackingUpdate t) {
        try {
            String json = mapper.writeValueAsString(t);
            redis.opsForValue().set(key(t.getTripId()), json, Duration.ofSeconds(ttlSeconds));
        } catch (Exception e) {
            throw new RuntimeException("Redis save failed", e);
        }
    }

    /** Fetch latest location for a trip */
    public TrackingUpdate get(Long tripId) {
        try {
            String json = redis.opsForValue().get(key(tripId));
            if (json == null) return null;
            return mapper.readValue(json, TrackingUpdate.class);
        } catch (Exception e) {
            throw new RuntimeException("Redis read failed", e);
        }
    }
}
