
package com.carrental.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carrental.dto.AdminBookingResponse;
import com.carrental.service.BookingService;

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

        List<AdminBookingResponse> bookings =
                bookingService.getAllBookings();

        return ResponseEntity.ok(bookings);
    }
}
