
package com.carrental.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.carrental.dto.AdminBookingResponse;
import com.carrental.dto.AssignBookingRequest;
import com.carrental.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/bookings")
public class AdminBookingController {

    private final BookingService bookingService;

    public AdminBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Admin: retrieve all bookings
    @GetMapping
    public ResponseEntity<List<AdminBookingResponse>> getAllBookings() {

        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    // Admin: approve booking
    @PutMapping("/{bookingId}/approve")
    public ResponseEntity<Map<String, String>> approveBooking(
            @PathVariable Long bookingId) {

        bookingService.approveBooking(bookingId);

        return ResponseEntity.ok(
                Map.of("message", "Booking approved successfully")
        );
    }

    // Admin: reject booking
    @PutMapping("/{bookingId}/reject")
    public ResponseEntity<Map<String, String>> rejectBooking(
            @PathVariable Long bookingId) {

        bookingService.rejectBooking(bookingId);

        return ResponseEntity.ok(
                Map.of("message", "Booking rejected successfully")
        );
    }

    // Admin: assign booking to employee
    @PutMapping("/{bookingId}/assign")
    public ResponseEntity<Map<String, String>> assignBooking(
            @PathVariable Long bookingId,
            @Valid @RequestBody AssignBookingRequest request) {

        bookingService.assignBooking(
                bookingId,
                request.assignedTo()
        );

        return ResponseEntity.ok(
                Map.of("message", "Booking assigned successfully")
        );
    }
}
