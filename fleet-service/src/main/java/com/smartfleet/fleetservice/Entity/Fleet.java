package com.smartfleet.fleetservice.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(
        name = "fleet",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "vehicle_number")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fleet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_number", nullable = false, unique = true)
    @NotBlank(message = "Vehicle number is required")
    private String vehicleNumber;

    @NotBlank(message = "Type is required")
    private String type;

    private String status; // AVAILABLE / ASSIGNED / MAINTENANCE
}
