
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

    // Admin: approve a pending booking
    void approveBooking(Long bookingId);

    // Admin: reject a pending booking
    void rejectBooking(Long bookingId);

    // Admin: assign booking to an employee
    void assignBooking(Long bookingId, String assignedTo);
}
