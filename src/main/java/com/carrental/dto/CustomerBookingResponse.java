
package com.carrental.dto;

import com.carrental.entity.BookingStatus;
import com.carrental.entity.PricingType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CustomerBookingResponse(

        Long bookingId,

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

) {
}
