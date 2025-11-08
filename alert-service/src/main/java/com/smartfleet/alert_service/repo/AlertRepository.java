package com.smartfleet.alert_service.repo;

import com.smartfleet.alert_service.domain.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findTop50ByOrderByCreatedAtDesc();
}
