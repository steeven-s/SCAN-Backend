
package com.carrental.service;

import java.util.List;

import com.carrental.dto.CarResponse;
import com.carrental.dto.CreateCarRequest;

public interface CarService {

    // Create a new car with pricing
    CarResponse createCar(CreateCarRequest request);

    // Retrieve all cars
    List<CarResponse> getAllCars();

    // Retrieve a car by ID
    CarResponse getCarById(Long id);

    // Update car details and pricing
    CarResponse updateCar(Long id, CreateCarRequest request);

    // Delete a car
    void deleteCar(Long id);
}
