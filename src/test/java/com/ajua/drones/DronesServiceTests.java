package com.ajua.drones;

import com.ajua.drones.DTO.ResponseDTO;
import com.ajua.drones.models.Drone;
import com.ajua.drones.models.Medication;
import com.ajua.drones.models.Model;
import com.ajua.drones.models.State;
import com.ajua.drones.repository.DroneRepository;
import com.ajua.drones.repository.MedicationRepository;
import com.ajua.drones.services.DronesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DronesServiceTests {

    @Mock
    private DroneRepository droneRepository;

    @Mock
    private MedicationRepository medicationRepository;

    @InjectMocks
    private DronesService dronesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCreateDrone_Success() {
        // Mock data
        Drone drone = Drone.builder()
                .serialNumber("DRN123")
                .model(Model.CRUISERWEIGHT)
                .batteryCapacity(100.0)
                .state(State.IDLE)
                .weightLimit(500.0)
                .deleted(false)
                .build();
        ResponseDTO expectedResponse = new ResponseDTO();
        expectedResponse.setDrones(Collections.singletonList(drone));
        expectedResponse.setMessage("Drone registered successfully");
        expectedResponse.setStatusCode(HttpStatus.OK.value());

        // Mock repository behavior
        when(droneRepository.findBySerialNumber(drone.getSerialNumber())).thenReturn(Optional.empty());
        when(droneRepository.save(any(Drone.class))).thenReturn(drone);

        // Call service method
        ResponseEntity<ResponseDTO> responseEntity = dronesService.createDrone(drone);

        // Verify behavior
        verify(droneRepository, times(1)).findBySerialNumber(drone.getSerialNumber());
        verify(droneRepository, times(1)).save(any(Drone.class));
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());
    }

    @Test
    void testCreateDrone_DroneExists() {
        // Mock data
        Drone drone = Drone.builder()
                .serialNumber("DRN123")
                .model(Model.CRUISERWEIGHT)
                .batteryCapacity(100.0)
                .state(State.IDLE)
                .weightLimit(500.0)
                .deleted(false)
                .medicationList(new ArrayList<>()) // Initialize medicationList
                .build();
        // Mock repository behavior
        when(droneRepository.findBySerialNumber(drone.getSerialNumber())).thenReturn(Optional.of(drone));

        // Call service method
        ResponseEntity<ResponseDTO> responseEntity = dronesService.createDrone(drone);

        // Verify behavior
        verify(droneRepository, times(1)).findBySerialNumber(drone.getSerialNumber());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Drone could not be registered! Error Drone with this serial number already exists.", responseEntity.getBody().getMessage());
    }

    @Test
    void testLoadDrone_DroneNotFound() {
        // Mock data
        String serialNumber = "DRN123";
        List<Medication> medications = new ArrayList<>();
        medications.add(new Medication("MED001", "Medication A", 50.0));

        // Mock repository behavior
        when(droneRepository.findBySerialNumber(serialNumber)).thenReturn(Optional.empty());

        // Call service method
        ResponseEntity<ResponseDTO> responseEntity = dronesService.loadDrone(serialNumber, medications);

        // Verify behavior
        verify(droneRepository, times(1)).findBySerialNumber(serialNumber);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Drone with serial number " + serialNumber + " not found", responseEntity.getBody().getMessage());
    }


    @Test
    void testCheckLoadedItems_Success() {
        // Mock data
        String serialNumber = "DRN123";
        Drone drone = Drone.builder()
                .serialNumber(serialNumber)
                .model(Model.CRUISERWEIGHT)
                .batteryCapacity(100.0)
                .state(State.IDLE)
                .weightLimit(500.0)
                .deleted(false)
                .build();
        // Mock repository behavior
        when(droneRepository.findBySerialNumber(serialNumber)).thenReturn(Optional.of(drone));

        // Call service method
        ResponseEntity<ResponseDTO> responseEntity = dronesService.checkLoadedItems(serialNumber);

        // Verify behavior
        verify(droneRepository, times(1)).findBySerialNumber(serialNumber);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Drone Loads found", responseEntity.getBody().getMessage());
    }

    @Test
    void testCheckLoadedItems_DroneNotFound() {
        // Mock data
        String serialNumber = "DRN123";

        // Mock repository behavior
        when(droneRepository.findBySerialNumber(serialNumber)).thenReturn(Optional.empty());

        // Call service method
        ResponseEntity<ResponseDTO> responseEntity = dronesService.checkLoadedItems(serialNumber);

        // Verify behavior
        verify(droneRepository, times(1)).findBySerialNumber(serialNumber);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Drone with serial number " + serialNumber + " not found", responseEntity.getBody().getMessage());
    }

    @Test
    void testCheckAvailableDrones_NoDronesAvailable() {
        // Mock repository behavior
        when(droneRepository.findAllByState(State.IDLE)).thenReturn(Collections.emptyList());

        // Call service method
        ResponseEntity<ResponseDTO> responseEntity = dronesService.checkAvailableDrones();

        // Verify behavior
        verify(droneRepository, times(1)).findAllByState(State.IDLE);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("No available drones at the moment. Try again later", responseEntity.getBody().getMessage());
    }

    @Test
    void testCheckAvailableDrones_DronesAvailable() {
        // Mock data
        Drone drone = Drone.builder()
                .serialNumber("DRN123")
                .model(Model.CRUISERWEIGHT)
                .batteryCapacity(100.0)
                .state(State.IDLE)
                .weightLimit(500.0)
                .deleted(false)
                .build();
        List<Drone> drones = Collections.singletonList(drone);

        // Mock repository behavior
        when(droneRepository.findAllByState(State.IDLE)).thenReturn(drones);

        // Call service method
        ResponseEntity<ResponseDTO> responseEntity = dronesService.checkAvailableDrones();

        // Verify behavior
        verify(droneRepository, times(1)).findAllByState(State.IDLE);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Here are the available drones", responseEntity.getBody().getMessage());
        assertEquals(drones, responseEntity.getBody().getDrones());
    }

    @Test
    void testCheckBatteryLevel_Success() {
        // Mock data
        String serialNumber = "DRN123";
        Drone drone = Drone.builder()
                .serialNumber(serialNumber)
                .model(Model.CRUISERWEIGHT)
                .batteryCapacity(75.0)
                .state(State.IDLE)
                .weightLimit(500.0)
                .deleted(false)
                .build();
        // Mock repository behavior
        when(droneRepository.findBySerialNumber(serialNumber)).thenReturn(Optional.of(drone));

        // Call service method
        ResponseEntity<ResponseDTO> responseEntity = dronesService.checkBatteryLevel(serialNumber);

        // Verify behavior
        verify(droneRepository, times(1)).findBySerialNumber(serialNumber);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Drone battery at 75.0%", responseEntity.getBody().getMessage());
    }

    @Test
    void testCheckBatteryLevel_DroneNotFound() {
        // Mock data
        String serialNumber = "DRN123";

        // Mock repository behavior
        when(droneRepository.findBySerialNumber(serialNumber)).thenReturn(Optional.empty());

        // Call service method
        ResponseEntity<ResponseDTO> responseEntity = dronesService.checkBatteryLevel(serialNumber);

        // Verify behavior
        verify(droneRepository, times(1)).findBySerialNumber(serialNumber);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Drone with serial number " + serialNumber + " not found", responseEntity.getBody().getMessage());
    }
}




