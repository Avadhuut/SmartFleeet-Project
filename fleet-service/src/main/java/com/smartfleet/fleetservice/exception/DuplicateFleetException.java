package com.smartfleet.fleetservice.exception;

public class DuplicateFleetException extends RuntimeException {
    public DuplicateFleetException(String message) {
        super(message);
    }
}
