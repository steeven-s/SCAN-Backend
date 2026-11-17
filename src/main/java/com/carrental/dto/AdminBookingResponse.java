
package com.carrental.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.carrental.entity.BookingStatus;
import com.carrental.entity.PricingType;

public record AdminBookingResponse(
        Long bookingId,
        Long customerId,
        String customerName,
        String customerPhone,
        Long carId,
        String carBrand,
        String carModel,
        Integer manufacturingYear,
        String rentalType,
        PricingType pricingType,
        LocalDateTime pickupDateTime,
        LocalDateTime returnDateTime,
        BigDecimal unitPrice,
        BigDecimal totalAmount,
        BookingStatus status,
        LocalDateTime createdAt
) {}
