package com.smartfleet.driverservice.repository;

import com.smartfleet.driverservice.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    // This method is required to check duplicates before saving
    boolean existsByLicenseNumber(String licenseNumber);
}
