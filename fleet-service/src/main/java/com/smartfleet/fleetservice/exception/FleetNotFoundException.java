package com.smartfleet.fleetservice.exception;

public class FleetNotFoundException extends RuntimeException {
    public FleetNotFoundException(String message) {
        super(message);
    }
}
