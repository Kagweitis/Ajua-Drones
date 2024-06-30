package com.ajua.drones.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Drone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "serial_number")
    @Size(max = 100, message = "The serial number must be less than or equal to 100 characters or more")
    private String serialNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "model")
    private Model model;

    @NotNull
    @Min(value = 0, message = "Battery capacity must be greater than or equal to 0.")
    @Max(value = 100, message = "Battery capacity must be less than or equal to 100.")
    @Column(name = "battery_capacity")
    private Double batteryCapacity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private State state;

    @NotNull
    @Max(value = 500, message = "Weight limit must be less than or equal to 500 grams.")
    @Column(name = "weight_limit")
    @Builder.Default
    private Double weightLimit = 500.0;

    @OneToMany(targetEntity = Medication.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "serial_number", referencedColumnName = "serial_number")
    private List<Medication> medicationList;

    @Builder.Default
    private Boolean deleted = false;


}
