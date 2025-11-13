package com.smartfleet.driverservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "driver",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "license_number")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @Column(name = "license_number", nullable = false, unique = true)
    @NotBlank(message = "License number is required")
    private String licenseNumber;

    private String status; // ACTIVE / INACTIVE / ON_TRIP
}
