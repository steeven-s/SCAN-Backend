
package com.carrental.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.carrental.entity.Booking;
import com.carrental.entity.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Check whether a car has an overlapping booking
    @Query("""
        SELECT COUNT(b) > 0
        FROM Booking b
        WHERE b.car.id = :carId
        AND b.status IN :statuses
        AND b.pickupDateTime < :returnDateTime
        AND b.returnDateTime > :pickupDateTime
    """)
    boolean existsOverlappingBooking(
            @Param("carId") Long carId,
            @Param("pickupDateTime") LocalDateTime pickupDateTime,
            @Param("returnDateTime") LocalDateTime returnDateTime,
            @Param("statuses") List<BookingStatus> statuses
    );

    // Get customer booking history
    List<Booking> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    // Get bookings for a particular car
    List<Booking> findByCarIdOrderByCreatedAtDesc(Long carId);

    // Get bookings by status
    List<Booking> findByStatusOrderByCreatedAtDesc(BookingStatus status);
    
    Optional<Booking> findByIdAndCustomerId(Long bookingId, Long customerId);
}
