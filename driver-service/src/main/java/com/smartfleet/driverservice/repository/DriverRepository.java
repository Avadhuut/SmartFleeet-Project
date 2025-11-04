package com.smartfleet.driverservice.repository;

import com.smartfleet.driverservice.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

public interface DriverRepository extends JpaRepository<Driver, Long> {


}