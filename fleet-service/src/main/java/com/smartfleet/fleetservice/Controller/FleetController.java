package com.smartfleet.fleetservice.Controller;

import com.smartfleet.fleetservice.dto.FleetDeleteResponse;
import com.smartfleet.fleetservice.dto.FleetRequest;
import com.smartfleet.fleetservice.dto.FleetResponse;
import com.smartfleet.fleetservice.Service.FleetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/fleet")
@RequiredArgsConstructor
public class FleetController {

    private final FleetService service;

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<FleetResponse> getById(@PathVariable Long id) {
        log.info("API Request: Get fleet by ID {}", id);
        return ResponseEntity.ok(service.getById(id));
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<FleetResponse>> getAll() {
        log.info("API Request: Get all fleets");
        return ResponseEntity.ok(service.getAll());
    }

    // CREATE FLEET
    @PostMapping
    public ResponseEntity<FleetResponse> add(@Valid @RequestBody FleetRequest request) {
        log.info("API Request: Add new fleet {}", request.getVehicleNumber());
        return ResponseEntity.ok(service.add(request));
    }

    // UPDATE FLEET
    @PutMapping("/{id}")
    public ResponseEntity<FleetResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody FleetRequest request
    ) {
        log.info("API Request: Update fleet ID {}", id);
        return ResponseEntity.ok(service.update(id, request));
    }

    // DELETE FLEET
    @DeleteMapping("/{id}")
    public ResponseEntity<FleetDeleteResponse> delete(@PathVariable Long id) {

        log.info("API Request: Delete fleet ID {}", id);

        service.delete(id);

        FleetDeleteResponse response = new FleetDeleteResponse(
                "Fleet deleted successfully",
                id
        );

        return ResponseEntity.ok(response);
    }

}
