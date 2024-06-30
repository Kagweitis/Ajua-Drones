package com.ajua.drones.DTO;

import com.ajua.drones.models.Medication;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestDTO {

    private List<Medication> medications;
    private String droneSerialNumber;
}
