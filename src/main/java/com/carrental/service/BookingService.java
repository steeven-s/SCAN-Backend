package com.carrental.service;

import java.util.List;
import java.util.Map;

import com.carrental.dto.AdminBookingResponse;
import com.carrental.dto.CreateBookingRequest;
import com.carrental.dto.CustomerBookingResponse;
import com.carrental.entity.Booking;

public interface BookingService {

    Booking createBooking(CreateBookingRequest request);

    List<CustomerBookingResponse> getMyBookings();

    Map<String, Object> cancelBooking(Long bookingId);

    // Admin: retrieve all customer bookings
    List<AdminBookingResponse> getAllBookings();
}