
package com.carrental.dto;

import com.carrental.entity.PricingType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record CreateBookingRequest(

        @NotNull(message = "Car ID is required")
        @Positive(message = "Invalid car ID")
        Long carId,

        @NotNull(message = "Pricing type is required")
        PricingType pricingType,

        @NotNull(message = "Pickup date and time are required")
        @Future(message = "Pickup date and time must be in the future")
        LocalDateTime pickupDateTime,

        @NotNull(message = "Return date and time are required")
        @Future(message = "Return date and time must be in the future")
        LocalDateTime returnDateTime

) {
}
