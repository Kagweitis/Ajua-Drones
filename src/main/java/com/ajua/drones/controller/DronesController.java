package com.ajua.drones.controller;


import com.ajua.drones.DTO.RequestDTO;
import com.ajua.drones.DTO.ResponseDTO;
import com.ajua.drones.models.Drone;
import com.ajua.drones.models.Medication;
import com.ajua.drones.services.DronesService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/v1/drones")
public class DronesController {

    private final DronesService dronesService;

    @PostMapping("/new")
    public ResponseEntity<ResponseDTO> createDrone(@RequestBody Drone drone){
        return dronesService.createDrone(drone);
    }

    @PostMapping("/load")
    public ResponseEntity<ResponseDTO> loadDrone(@RequestBody @NonNull RequestDTO requestDTO){
        return dronesService.loadDrone(requestDTO.getDroneSerialNumber(), requestDTO.getMedications());
    }

    @GetMapping("/get-loads")
    public ResponseEntity<ResponseDTO> getDroneLoads(@RequestParam String serialNumber){
        return dronesService.checkLoadedItems(serialNumber);
    }

    @GetMapping("/available")
    public ResponseEntity<ResponseDTO> checkAvailableDrones(){
        return dronesService.checkAvailableDrones();
    }

    @GetMapping("/battery")
    public ResponseEntity<ResponseDTO> getBatteryStatus(@RequestParam String serialNumber){
        return dronesService.checkBatteryLevel(serialNumber);
    }

}
