
package com.carrental.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCarRequest(

    @NotBlank(message = "Brand is required")
    @Size(max = 100, message = "Brand cannot exceed 100 characters")
    String brand,

    @NotBlank(message = "Model is required")
    @Size(max = 100, message = "Model cannot exceed 100 characters")
    String model,

    @NotNull(message = "Manufacturing year is required")
    @Min(value = 1900, message = "Invalid manufacturing year")
    @Max(value = 2100, message = "Invalid manufacturing year")
    Integer manufacturingYear,

    @NotBlank(message = "Registration number is required")
    @Size(max = 30, message = "Registration number cannot exceed 30 characters")
    String registrationNumber,

    @Size(max = 150, message = "Owner name cannot exceed 150 characters")
    String ownerName,

    @Pattern(
        regexp = "^\\+?[0-9]{10,15}$",
        message = "Enter a valid owner mobile number (10 to 15 digits)"
    )
    String ownerMobileNumber,

    @NotBlank(message = "Category is required")
    @Size(max = 50)
    String category,

    @NotBlank(message = "Fuel type is required")
    @Size(max = 30)
    String fuelType,

    @NotBlank(message = "Transmission is required")
    @Size(max = 30)
    String transmission,

    @NotNull(message = "Seating capacity is required")
    @Min(value = 1, message = "Seating capacity must be at least 1")
    @Max(value = 20, message = "Seating capacity cannot exceed 20")
    Integer seatingCapacity,

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    String description,

    @NotNull(message = "Pricing details are required")
    @Valid
    CarPricingRequest pricing

) {
}
