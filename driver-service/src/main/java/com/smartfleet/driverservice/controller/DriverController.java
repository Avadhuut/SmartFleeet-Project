package com.smartfleet.driverservice.controller;

import com.smartfleet.driverservice.dto.*;
import com.smartfleet.driverservice.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/driver")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService service;

    @GetMapping("/{id}")
    public DriverResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<DriverResponse> getAll() {
        return service.getAll();
    }


    @PostMapping
    public DriverResponse add(@Valid @RequestBody DriverRequest request) {
        return service.add(request);
    }

    @PutMapping("/{id}")
    public DriverResponse update(@PathVariable Long id, @Valid @RequestBody DriverRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteResponse> delete(@PathVariable Long id) {

        service.delete(id);

        DeleteResponse response = new DeleteResponse(
                "Driver deleted successfully",
                id
        );

        return ResponseEntity.ok(response); // 200 OK with message
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DriverResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String newStatus = body.get("status");
        log.info("API Request: Update driver status -> id={}, status={}", id, newStatus);

        DriverResponse updated = service.updateStatus(id, newStatus);

        return ResponseEntity.ok(updated);
    }



}
