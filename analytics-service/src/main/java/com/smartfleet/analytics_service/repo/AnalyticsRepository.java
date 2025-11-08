package com.smartfleet.analytics_service.repo;

import com.smartfleet.analytics_service.domain.AnalyticsSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface AnalyticsRepository extends JpaRepository<AnalyticsSummary, Long> {
    Optional<AnalyticsSummary> findByDate(LocalDate date);
    AnalyticsSummary findTopByOrderByDateDesc();
}
