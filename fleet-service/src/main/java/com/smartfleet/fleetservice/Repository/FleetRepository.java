package com.smartfleet.fleetservice.Repository;

import com.smartfleet.fleetservice.Entity.Fleet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FleetRepository extends JpaRepository<Fleet, Long> {

    // To check duplicate vehicle number before saving or updating
    boolean existsByVehicleNumber(String vehicleNumber);
}
