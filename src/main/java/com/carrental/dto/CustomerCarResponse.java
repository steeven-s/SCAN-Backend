package com.carrental.dto;

import com.carrental.entity.CarStatus;

import java.util.List;

public record CustomerCarResponse(

        Long id,
        String brand,
        String model,
        Integer manufacturingYear,
        String category,
        String fuelType,
        String transmission,
        Integer seatingCapacity,
        String description,
        CarStatus status,
        CarPricingResponse pricing,
        List<CustomerCarImageResponse> images

) {

    public record CustomerCarImageResponse(
            Long id,
            String imageUrl,
            Boolean isPrimary
    ) {
    }
}