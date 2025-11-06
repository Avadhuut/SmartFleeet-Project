package com.smartfleet.tracking_service.controller;

import com.smartfleet.tracking_service.dto.TrackingUpdate;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tracking")
public class TrackingController {

    // just echo back what we received
    @PostMapping("/update")
    public TrackingUpdate update(@Valid @RequestBody TrackingUpdate update) {
        return update;
    }
}
