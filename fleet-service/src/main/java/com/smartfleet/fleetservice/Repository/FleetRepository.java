package com.smartfleet.fleetservice.Repository;

import com.smartfleet.fleetservice.Entity.Fleet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

public interface FleetRepository extends JpaRepository<Fleet, Long> {

}