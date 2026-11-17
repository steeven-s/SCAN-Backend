
package com.carrental.dto;

import java.time.LocalDateTime;

import com.carrental.entity.CarStatus;

public record CarResponse(
    Long id,
    String brand,
    String model,
    Integer manufacturingYear,
    String registrationNumber,
    String category,
    String fuelType,
    String transmission,
    Integer seatingCapacity,
    String description,
    CarStatus status,
    CarPricingResponse pricing,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
