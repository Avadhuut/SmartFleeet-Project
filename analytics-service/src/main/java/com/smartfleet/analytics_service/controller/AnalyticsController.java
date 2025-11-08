package com.smartfleet.analytics_service.controller;

import com.smartfleet.analytics_service.domain.AnalyticsSummary;
import com.smartfleet.analytics_service.repo.AnalyticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsRepository repo;

    @GetMapping("/summary")
    public AnalyticsSummary getLatestSummary() {
        return repo.findTopByOrderByDateDesc();
    }
}
