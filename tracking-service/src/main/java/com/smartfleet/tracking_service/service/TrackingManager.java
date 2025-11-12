package com.smartfleet.tracking_service.service;

import com.smartfleet.tracking_service.dto.StartTrackingRequest;
import com.smartfleet.tracking_service.dto.TrackingUpdate;
import com.smartfleet.tracking_service.kafka.TrackingProducer;
import com.smartfleet.tracking_service.model.Tracker;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TrackingManager {

    private final Map<Long, Tracker> trackers = new ConcurrentHashMap<>();
    private final TrackingProducer trackingProducer; //

    public TrackingManager(TrackingProducer trackingProducer) {
        this.trackingProducer = trackingProducer;
    }

    public void startTracking(StartTrackingRequest req) {
        Tracker tracker = new Tracker(req.getTripId(), req.getStartLatitude(), req.getStartLongitude());
        trackers.put(req.getTripId(), tracker);
        System.out.println("✅ Started tracking trip: " + req.getTripId());
    }

    public void stopTracking(Long tripId) {
        Tracker tracker = trackers.remove(tripId);
        if (tracker != null) {
            tracker.stop();
            System.out.println("Stopped tracking trip: " + tripId);
        }
    }

    public Collection<Tracker> getActiveTrackers() {
        return trackers.values();
    }

    //  Now we also send updates to Kafka here
    public void handleSimulatedPosition(TrackingUpdate update) {
        Tracker t = trackers.get(update.getTripId());
        if (t == null || !t.isActive()) return;

        t.setLatitude(update.getLatitude());
        t.setLongitude(update.getLongitude());

        System.out.println("📍 Trip: " + t.getTripId() +
                " | Lat: " + update.getLatitude() +
                " | Lon: " + update.getLongitude() +
                " | Time: " + update.getTimestamp());

        //  send this tracking update to Kafka
        trackingProducer.sendTrackingUpdate(update);
    }
}
