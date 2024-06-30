package com.ajua.drones.services;


import com.ajua.drones.DTO.ResponseDTO;
import com.ajua.drones.models.Drone;
import com.ajua.drones.models.Medication;
import com.ajua.drones.models.State;
import com.ajua.drones.repository.DroneRepository;
import com.ajua.drones.repository.MedicationRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DronesService {

    private final DroneRepository droneRepository;
    private final MedicationRepository medicationRepository;
    public ResponseEntity<ResponseDTO> createDrone(Drone drone) {
        ResponseDTO responseDTO = new ResponseDTO();

        Optional<Drone> drone1 =  droneRepository.findBySerialNumber(drone.getSerialNumber());

        try {

            drone1.ifPresentOrElse(drone2 -> {
                        // Action if drone with the given serial number already exists
                        log.info("Drone with serial number " + drone.getSerialNumber() + " already exists.");
                        throw new IllegalArgumentException("Drone with this serial number already exists.");
                    },
                    ()-> {
                        droneRepository.save(drone);
                        responseDTO.setDrones(Collections.singletonList(drone));
                        responseDTO.setMessage("Drone registered successfully");
                        responseDTO.setStatusCode(200);

                    });
        }catch (ConstraintViolationException e) {
            StringBuilder errorMessage = new StringBuilder("Drone could not be registered! ");
            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                errorMessage.append(extractMessage(violation.toString())).append(" ");
            }
            responseDTO.setMessage(errorMessage.toString().trim());
            responseDTO.setStatusCode(400);
        } catch (Exception e){
            responseDTO.setMessage("Drone could not be registered! Error "+e.getMessage());
            responseDTO.setStatusCode(500);
        }
    return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
    }

    public ResponseEntity<ResponseDTO> loadDrone(String serialNumber, List<Medication> medications) {
        ResponseDTO responseDTO = new ResponseDTO();

        log.info("serial number "+serialNumber);
        Optional<Drone> optionalDrone = droneRepository.findBySerialNumber(serialNumber);

        try {
            // Check if medications is null
            if (medications == null) {
                throw new IllegalArgumentException("Medications list cannot be empty.");
            }
            optionalDrone.ifPresentOrElse(
                    drone -> {
                        if (drone.getState() == State.LOADING) {
                            throw new IllegalStateException("Drone is already in LOADING state.");
                        }
                        if (drone.getBatteryCapacity() < 25.0) {
                            throw new IllegalStateException("Drone battery level is below 25%. Cannot load.");
                        }

                        double totalMedicationWeight = medications.stream()
                                .mapToDouble(Medication::getWeight)
                                .sum();

                        if (totalMedicationWeight +
                                drone.getMedicationList().stream().mapToDouble(Medication::getWeight).sum()
                                > 500.0) {
                            throw new IllegalArgumentException("Drone weight limit exceeded. Cannot load.");
                        }

                        log.info("loading ");
                        // Perform operations within a transactional context
                        drone.getMedicationList().addAll(medications);
                        drone.setMedicationList(drone.getMedicationList());
                        drone.setState(State.LOADING);
                        droneRepository.save(drone);

                        // Associate medications with the drone and save medications
                        medications.forEach(medication -> medication.setDroneSerial(drone.getSerialNumber()));
                        medicationRepository.saveAll(medications);

                        responseDTO.setStatusCode(HttpStatus.OK.value());
                        responseDTO.setDrones(Collections.singletonList(drone));
                        responseDTO.setMessage("Drone Loaded Successfully");
                    },
                    () -> {
                        responseDTO.setMessage("Drone with serial number " + serialNumber + " not found");
                        responseDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
                    }
            );
        } catch (IllegalStateException | IllegalArgumentException e) {
            log.error("error loading drone "+e);
            responseDTO.setMessage("Drone could not be loaded! " + e.getMessage());
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
        } catch (ConstraintViolationException e) {
            StringBuilder errorMessage = new StringBuilder("Drone could not be loaded! ");
            for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
                errorMessage.append(extractMessage(violation.toString())).append(" ");
            }
            responseDTO.setMessage(errorMessage.toString().trim());
            responseDTO.setStatusCode(400);
        }
        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
    }

    public ResponseEntity<ResponseDTO> checkLoadedItems(String serialNumber){
        ResponseDTO responseDTO = new ResponseDTO();

        log.info("serial number "+serialNumber);
        Optional<Drone> optionalDrone = droneRepository.findBySerialNumber(serialNumber);

        try {
            optionalDrone.ifPresentOrElse(
                    drone -> {


                        responseDTO.setStatusCode(HttpStatus.OK.value());
                        responseDTO.setDrones(Collections.singletonList(drone));
                        responseDTO.setMessage("Drone Loads found");
                    },
                    () -> {
                        responseDTO.setMessage("Drone with serial number " + serialNumber + " not found");
                        responseDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
                    }
            );
        } catch (IllegalStateException | IllegalArgumentException e) {
            log.error("error loading drone "+e);
            responseDTO.setMessage("Drone loads could not be found! " + e.getMessage());
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }

        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
    }

    public ResponseEntity<ResponseDTO> checkAvailableDrones(){
        ResponseDTO responseDTO = new ResponseDTO();

        List<Drone> availableDrones = droneRepository.findAllByState(State.IDLE);
        try {
              if (availableDrones.isEmpty()){
                  responseDTO.setStatusCode(HttpStatus.OK.value());
                  responseDTO.setMessage("No available drones at the moment. Try again later");
              } else {
                  responseDTO.setStatusCode(HttpStatus.OK.value());
                  responseDTO.setDrones(availableDrones);
                  responseDTO.setMessage("Here are the available drones");
              }
        } catch (IllegalStateException | IllegalArgumentException e) {
            log.error("error loading drone "+e);
            responseDTO.setMessage("Could not check for drones due to an error. Try again later" );
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }
        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
    }

    public ResponseEntity<ResponseDTO> checkBatteryLevel(String serialNumber) {

        ResponseDTO responseDTO = new ResponseDTO();

        log.info("serial number "+serialNumber);
        Optional<Drone> optionalDrone = droneRepository.findBySerialNumber(serialNumber);

        try {
            optionalDrone.ifPresentOrElse(
                    drone -> {


                        responseDTO.setStatusCode(HttpStatus.OK.value());
                        responseDTO.setMessage("Drone battery at "+drone.getBatteryCapacity()+"%");
                    },
                    () -> {
                        responseDTO.setMessage("Drone with serial number " + serialNumber + " not found");
                        responseDTO.setStatusCode(HttpStatus.NOT_FOUND.value());
                    }
            );
        } catch (IllegalStateException | IllegalArgumentException e) {
            log.error("error loading drone "+e);
            responseDTO.setMessage("Drone battery could not be read! " + e.getMessage());
            responseDTO.setStatusCode(HttpStatus.BAD_REQUEST.value());
        }

        return ResponseEntity.status(responseDTO.getStatusCode()).body(responseDTO);
    }

    @Scheduled(fixedRate = 3600000) // Runs every hour
    public void checkBatteryLevelsPeriodically() {
        List<Drone> drones = droneRepository.findAll();

        for (Drone drone : drones) {
            // Check battery level and perform actions
            log.info("Checking battery level for drone with serial number: " + drone.getSerialNumber());

            if (drone.getBatteryCapacity() < 10.0) {
                log.warn("Drone with serial number " + drone.getSerialNumber() + " has low battery.");
            }
        }
    }

    public static String extractMessage(String violationMessage) {
        // Find the start of the actual message
        int messageStartIndex = violationMessage.indexOf("interpolatedMessage='") + "interpolatedMessage='".length();
        // Find the end of the actual message
        int messageEndIndex = violationMessage.indexOf("',", messageStartIndex);
        // Extract and return the message
        return violationMessage.substring(messageStartIndex, messageEndIndex);
    }

}
