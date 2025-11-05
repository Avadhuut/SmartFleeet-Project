package com.smartfleet.fleetservice.Controller;

import com.smartfleet.fleetservice.dto.*;
import com.smartfleet.fleetservice.Service.FleetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fleet")
@RequiredArgsConstructor
public class FleetController {

    private final FleetService service;

    @GetMapping("/{id}")
    public FleetResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<FleetResponse> getAll() {
        return service.getAll();
    }

    @PostMapping
    public FleetResponse add(@Valid @RequestBody FleetRequest request) {
        return service.add(request);
    }

    @PutMapping("/{id}")
    public FleetResponse update(@PathVariable Long id, @RequestBody FleetRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().body(Map.of("message", "Fleet deleted successfully with ID: " + id));
    }
}
