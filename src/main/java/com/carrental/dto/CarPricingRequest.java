
package com.carrental.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public record CarPricingRequest(

    @NotNull(message = "Hourly price is required")
    @DecimalMin(value = "0.01",
            message = "Hourly price must be greater than zero")
    @Digits(integer = 8, fraction = 2)
    BigDecimal hourlyPrice,

    @NotNull(message = "Half-day price is required")
    @DecimalMin(value = "0.01",
            message = "Half-day price must be greater than zero")
    @Digits(integer = 8, fraction = 2)
    BigDecimal halfDayPrice,

    @NotNull(message = "Daily price is required")
    @DecimalMin(value = "0.01",
            message = "Daily price must be greater than zero")
    @Digits(integer = 8, fraction = 2)
    BigDecimal dailyPrice,

    @NotNull(message = "Weekly price is required")
    @DecimalMin(value = "0.01",
            message = "Weekly price must be greater than zero")
    @Digits(integer = 8, fraction = 2)
    BigDecimal weeklyPrice,

    @NotNull(message = "Monthly price is required")
    @DecimalMin(value = "0.01",
            message = "Monthly price must be greater than zero")
    @Digits(integer = 8, fraction = 2)
    BigDecimal monthlyPrice

) {
}
