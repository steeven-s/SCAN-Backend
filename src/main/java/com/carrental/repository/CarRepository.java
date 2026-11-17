
package com.carrental.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carrental.entity.Car;
import com.carrental.entity.CarStatus;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    // Check whether registration number already exists
    boolean existsByRegistrationNumber(String registrationNumber);

    // Find car using registration number
    Optional<Car> findByRegistrationNumber(String registrationNumber);

    // Find cars by availability status
    List<Car> findByStatus(CarStatus status);

    // Find available cars
    List<Car> findByStatusOrderByCreatedAtDesc(CarStatus status);

    // Filter cars by category
    List<Car> findByCategoryIgnoreCase(String category);

    // Filter cars by fuel type
    List<Car> findByFuelTypeIgnoreCase(String fuelType);

    // Filter cars by transmission
    List<Car> findByTransmissionIgnoreCase(String transmission);

    // Search by brand
    List<Car> findByBrandContainingIgnoreCase(String brand);

    // Search by brand or model
    List<Car> findByBrandContainingIgnoreCaseOrModelContainingIgnoreCase(
            String brand,
            String model
    );

    // Find cars by category and availability
    List<Car> findByCategoryIgnoreCaseAndStatus(
            String category,
            CarStatus status
    );
    
    boolean existsByRegistrationNumberAndIdNot(
            String registrationNumber,
            Long id
    );
}
