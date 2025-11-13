package com.smartfleet.driverservice.controller;

import com.smartfleet.driverservice.dto.*;
import com.smartfleet.driverservice.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


}
