
package com.carrental.controller;

import com.carrental.dto.CreateBookingRequest;
import com.carrental.dto.CustomerBookingResponse;
import com.carrental.entity.Booking;
import com.carrental.service.BookingService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customer/bookings")
public class CustomerBookingController {

    private final BookingService bookingService;

    public CustomerBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Create a new booking
    @PostMapping
    public ResponseEntity<?> createBooking(
            @Valid @RequestBody CreateBookingRequest request) {

        Booking booking = bookingService.createBooking(request);

        Map<String, Object> response = Map.of(
                "message", "Booking created successfully",
                "bookingId", booking.getId(),
                "carId", booking.getCar().getId(),
                "pricingType", booking.getPricingType(),
                "pickupDateTime", booking.getPickupDateTime(),
                "returnDateTime", booking.getReturnDateTime(),
                "unitPrice", booking.getUnitPrice(),
                "totalAmount", booking.getTotalAmount(),
                "status", booking.getStatus()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    // Get logged-in customer's booking history
    @GetMapping
    public ResponseEntity<List<CustomerBookingResponse>> getMyBookings() {

        List<CustomerBookingResponse> bookings =
                bookingService.getMyBookings();

        return ResponseEntity.ok(bookings);
    }
    
    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelBooking(
            @PathVariable Long bookingId) {

        Map<String, Object> response =
                bookingService.cancelBooking(bookingId);

        return ResponseEntity.ok(response);
    }
}
