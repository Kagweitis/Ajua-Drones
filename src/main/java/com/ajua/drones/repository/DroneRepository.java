package com.ajua.drones.repository;

import com.ajua.drones.models.Drone;
import com.ajua.drones.models.State;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface DroneRepository extends JpaRepository<Drone, Long> {

    Optional<Drone> findBySerialNumber(String serialNumber);


    List<Drone> findAllByState(@NotNull State state);
}
