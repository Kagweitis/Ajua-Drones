package com.ajua.drones.DTO;

import com.ajua.drones.models.Drone;
import com.ajua.drones.models.Medication;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDTO {

    private int statusCode;
    private String message;
    private List<Drone> drones;
    private List<Medication> medications;
}
