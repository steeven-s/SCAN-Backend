
package com.carrental.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.carrental.dto.CarResponse;
import com.carrental.dto.CreateCarRequest;
import com.carrental.service.CarService;

@RestController
@RequestMapping("/api/admin/cars")
public class AdminCarController {

    private final CarService carService;

    public AdminCarController(CarService carService) {
        this.carService = carService;
    }

    // CREATE CAR
    @PostMapping
    public ResponseEntity<CarResponse> createCar(
            @Valid @RequestBody CreateCarRequest request) {

        CarResponse response = carService.createCar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // GET ALL CARS
    @GetMapping
    public ResponseEntity<List<CarResponse>> getAllCars() {

        List<CarResponse> cars = carService.getAllCars();

        return ResponseEntity.ok(cars);
    }

    // GET CAR BY ID
    @GetMapping("/{id}")
    public ResponseEntity<CarResponse> getCarById(
            @PathVariable Long id) {

        CarResponse response = carService.getCarById(id);

        return ResponseEntity.ok(response);
    }

    // UPDATE CAR
    @PutMapping("/{id}")
    public ResponseEntity<CarResponse> updateCar(
            @PathVariable Long id,
            @Valid @RequestBody CreateCarRequest request) {

        CarResponse response = carService.updateCar(id, request);

        return ResponseEntity.ok(response);
    }

    // DELETE CAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(
            @PathVariable Long id) {

        carService.deleteCar(id);

        return ResponseEntity.noContent().build();
    }
}
