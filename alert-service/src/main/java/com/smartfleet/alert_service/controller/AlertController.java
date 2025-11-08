package com.smartfleet.alert_service.controller;

import com.smartfleet.alert_service.domain.Alert;
import com.smartfleet.alert_service.repo.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertRepository repo;

    // GET /api/alerts
    @GetMapping
    public List<Alert> getRecentAlerts() {
        return repo.findTop50ByOrderByCreatedAtDesc();
    }
}
