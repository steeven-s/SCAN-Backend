
package com.carrental.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carrental.entity.CarPricing;

@Repository
public interface CarPricingRepository
        extends JpaRepository<CarPricing, Long> {

    // Find pricing details using car ID
    Optional<CarPricing> findByCarId(Long carId);

    // Check whether pricing already exists for a car
    boolean existsByCarId(Long carId);

    // Delete pricing details using car ID
    void deleteByCarId(Long carId);
}
