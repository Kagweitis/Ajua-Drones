package com.ajua.drones.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Medication {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9-_]+$", message = "Name can only contain letters, numbers, '-', and '_'.")
    private String name;

    @NotNull
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "Code can only contain upper case letters, numbers, and '_'.")
    private String code;

    @NotNull
    private Double weight;

    @Lob
    private byte[] image;

    private String droneSerial;

    @Builder.Default
    private Boolean deleted = false;


    public Medication(String med002, String medicationB, double v) {
    }
}
