
package com.carrental.service.impl;

import com.carrental.dto.AdminBookingResponse;
import com.carrental.dto.CreateBookingRequest;
import com.carrental.dto.CustomerBookingResponse;
import com.carrental.entity.Booking;
import com.carrental.entity.BookingStatus;
import com.carrental.entity.Car;
import com.carrental.entity.CarPricing;
import com.carrental.entity.CarStatus;
import com.carrental.entity.PricingType;
import com.carrental.entity.User;
import com.carrental.repository.BookingRepository;
import com.carrental.repository.CarRepository;
import com.carrental.repository.UserRepository;
import com.carrental.service.BookingService;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class BookingServiceImpl implements BookingService {

	private final BookingRepository bookingRepository;
	private final CarRepository carRepository;
	private final UserRepository userRepository;

	public BookingServiceImpl(
			BookingRepository bookingRepository,
			CarRepository carRepository,
			UserRepository userRepository) {

		this.bookingRepository = bookingRepository;
		this.carRepository = carRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public Booking createBooking(CreateBookingRequest request) {

		// 1. Get authenticated customer
		Authentication authentication =
				SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new ResponseStatusException(
					HttpStatus.UNAUTHORIZED,
					"Customer authentication required"
					);
		}

		String email = authentication.getName();

		User customer = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"Customer not found"
						));

		// 2. Validate pickup and return dates
		LocalDateTime pickup = request.pickupDateTime();
		LocalDateTime returnDate = request.returnDateTime();

		if (!pickup.isAfter(LocalDateTime.now())) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Pickup date and time must be in the future"
					);
		}

		if (!returnDate.isAfter(pickup)) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Return date must be after pickup date"
					);
		}

		// 3. Validate rental type
		if (request.pricingType() == null) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Rental pricing type is required"
					);
		}

		// 4. Half-day must be exactly 12 hours
		if (request.pricingType() == PricingType.HALF_DAY) {

			Duration duration = Duration.between(pickup, returnDate);

			if (!duration.equals(Duration.ofHours(12))) {
				throw new ResponseStatusException(
						HttpStatus.BAD_REQUEST,
						"Half-day booking must be exactly 12 hours"
						);
			}
		}

		// 5. Find car
		Car car = carRepository.findById(request.carId())
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"Car not found"
						));

		if (car.getStatus() != CarStatus.AVAILABLE) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Car is currently unavailable"
					);
		}

		// 6. Check overlapping bookings
		List<BookingStatus> blockingStatuses = List.of(
				BookingStatus.PENDING,
				BookingStatus.CONFIRMED
				);

		boolean alreadyBooked =
				bookingRepository.existsOverlappingBooking(
						car.getId(),
						pickup,
						returnDate,
						blockingStatuses
						);

		if (alreadyBooked) {
			throw new ResponseStatusException(
					HttpStatus.CONFLICT,
					"Car is already booked for the selected period"
					);
		}

		// 7. Get car pricing
		CarPricing pricing = car.getPricing();

		if (pricing == null) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Pricing information is not configured for this car"
					);
		}

		// 8. Calculate rental price
		BigDecimal unitPrice = getUnitPrice(
				pricing,
				request.pricingType()
				);

		BigDecimal totalAmount = calculateTotalAmount(
				pricing,
				request.pricingType(),
				pickup,
				returnDate
				);

		// 9. Create booking
		Booking booking = new Booking();

		booking.setCustomer(customer);
		booking.setCar(car);
		booking.setRentalType("SELF_DRIVE");
		booking.setPricingType(request.pricingType());
		booking.setPickupDateTime(pickup);
		booking.setReturnDateTime(returnDate);
		booking.setUnitPrice(unitPrice);
		booking.setTotalAmount(totalAmount);
		booking.setStatus(BookingStatus.PENDING);

		// 10. Save booking
		return bookingRepository.save(booking);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CustomerBookingResponse> getMyBookings() {

		// Get authenticated customer
		Authentication authentication =
				SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new ResponseStatusException(
					HttpStatus.UNAUTHORIZED,
					"Customer authentication required"
					);
		}

		String email = authentication.getName();

		// Find customer
		User customer = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"Customer not found"
						));

		// Fetch only this customer's bookings
		List<Booking> bookings =
				bookingRepository.findByCustomerIdOrderByCreatedAtDesc(
						customer.getId()
						);

		// Convert entities into response DTOs
		return bookings.stream()
				.map(booking -> new CustomerBookingResponse(
						booking.getId(),
						booking.getCar().getId(),
						booking.getCar().getBrand(),
						booking.getCar().getModel(),
						booking.getCar().getManufacturingYear(),
						booking.getRentalType(),
						booking.getPricingType(),
						booking.getPickupDateTime(),
						booking.getReturnDateTime(),
						booking.getUnitPrice(),
						booking.getTotalAmount(),
						booking.getStatus(),
						booking.getCreatedAt()
						))
				.toList();
	}

	// Cancel a booking belonging to the authenticated customer
	@Override
	@Transactional
	public Map<String, Object> cancelBooking(Long bookingId) {

		// 1. Get authenticated customer
		Authentication authentication =
				SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new ResponseStatusException(
					HttpStatus.UNAUTHORIZED,
					"Customer authentication required"
					);
		}

		String email = authentication.getName();

		// 2. Find customer
		User customer = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"Customer not found"
						));

		// 3. Find booking belonging to this customer only
		Booking booking = bookingRepository
				.findByIdAndCustomerId(bookingId, customer.getId())
				.orElseThrow(() -> new ResponseStatusException(
						HttpStatus.NOT_FOUND,
						"Booking not found"
						));

		// 4. Allow cancellation only for PENDING or CONFIRMED bookings
		if (booking.getStatus() != BookingStatus.PENDING &&
				booking.getStatus() != BookingStatus.CONFIRMED) {

			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Only pending or confirmed bookings can be cancelled"
					);
		}

		// 5. Do not allow cancellation after rental pickup time
		if (!booking.getPickupDateTime().isAfter(LocalDateTime.now())) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"This booking cannot be cancelled because the rental has already started"
					);
		}

		// 6. Update booking status
		booking.setStatus(BookingStatus.CANCELLED);

		Booking cancelledBooking = bookingRepository.save(booking);

		// 7. Return cancellation response
		return Map.of(
				"message", "Booking cancelled successfully.",
				"bookingId", cancelledBooking.getId(),
				"status", cancelledBooking.getStatus().name()
				);
	}


	// Admin: retrieve all customer bookings

@Override
@Transactional(readOnly = true)
public List<AdminBookingResponse> getAllBookings() {

    return bookingRepository.findAll()
            .stream()
            .sorted((b1, b2) ->
                    b2.getCreatedAt().compareTo(b1.getCreatedAt()))
            .map(booking -> new AdminBookingResponse(
                    booking.getId(),
                    booking.getCustomer().getId(),
                    booking.getCustomer().getFullName(),
                    booking.getCustomer().getPhone(),
                    booking.getCar().getId(),
                    booking.getCar().getBrand(),
                    booking.getCar().getModel(),
                    booking.getCar().getManufacturingYear(),
                    booking.getCar().getOwnerName(),
                    booking.getCar().getOwnerMobileNumber(),
                    booking.getAssignedTo(),
                    booking.getRentalType(),
                    booking.getPricingType(),
                    booking.getPickupDateTime(),
                    booking.getReturnDateTime(),
                    booking.getUnitPrice(),
                    booking.getTotalAmount(),
                    booking.getStatus(),
                    booking.getCreatedAt()
            ))
            .toList();
}


// Admin: approve a pending booking
@Override
@Transactional
public void approveBooking(Long bookingId) {

    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Booking not found"
            ));

    if (booking.getStatus() != BookingStatus.PENDING) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Only pending bookings can be approved"
        );
    }

    booking.setStatus(BookingStatus.CONFIRMED);

    bookingRepository.save(booking);
}

// Admin: reject a pending booking
@Override
@Transactional
public void rejectBooking(Long bookingId) {

    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Booking not found"
            ));

    if (booking.getStatus() != BookingStatus.PENDING) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Only pending bookings can be rejected"
        );
    }

    booking.setStatus(BookingStatus.CANCELLED);

    bookingRepository.save(booking);
}

// Admin: assign booking to an employee
@Override
@Transactional
public void assignBooking(Long bookingId, String assignedTo) {

    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Booking not found"
            ));

    if (assignedTo == null || assignedTo.isBlank()) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Please select an employee"
        );
    }

    String employee = assignedTo.trim();

    List<String> allowedEmployees = List.of(
            "Naveen",
            "Charan",
            "Ashok",
            "Steeven"
    );

    if (!allowedEmployees.contains(employee)) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid employee. Allowed employees: Naveen, Charan, Ashok, Steeven"
        );
    }

    booking.setAssignedTo(employee);

    bookingRepository.save(booking);
}




	// Get base price for selected rental type
	private BigDecimal getUnitPrice(
			CarPricing pricing,
			PricingType pricingType) {

		BigDecimal price = switch (pricingType) {

		case HALF_DAY -> pricing.getHalfDayPrice();

		case DAILY -> pricing.getDailyPrice();

		case WEEKLY -> pricing.getWeeklyPrice();

		case MONTHLY -> pricing.getMonthlyPrice();

		case HOURLY -> pricing.getHourlyPrice();
		};

		if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					"Selected rental price is not available"
					);
		}

		return price;
	}

	// Calculate final rental amount
	private BigDecimal calculateTotalAmount(
			CarPricing pricing,
			PricingType pricingType,
			LocalDateTime pickup,
			LocalDateTime returnDate) {

		Duration duration = Duration.between(pickup, returnDate);

		return switch (pricingType) {

		case HALF_DAY -> pricing.getHalfDayPrice();

		case DAILY -> {
			long days = calculateBillableDays(duration);

			yield pricing.getDailyPrice()
			.multiply(BigDecimal.valueOf(days));
		}

		case WEEKLY -> calculateWeeklyAmount(
				pricing,
				pickup,
				returnDate
				);

		case MONTHLY -> calculateMonthlyAmount(
				pricing,
				pickup,
				returnDate
				);

		case HOURLY -> {

			long hours = duration.toHours();

			if (!duration.minusHours(hours).isZero()) {
				hours++;
			}

			if (hours <= 0) {
				hours = 1;
			}

			yield pricing.getHourlyPrice()
			.multiply(BigDecimal.valueOf(hours));
		}
		};
	}

	// Daily rental: charge every started 24-hour period
	private long calculateBillableDays(Duration duration) {

		if (duration.isZero()) {
			return 0;
		}

		long completeDays = duration.toDays();

		if (!duration.minusDays(completeDays).isZero()) {
			completeDays++;
		}

		return Math.max(completeDays, 1);
	}

	// Weekly rental: complete 7-day blocks + remaining daily charges
	private BigDecimal calculateWeeklyAmount(
			CarPricing pricing,
			LocalDateTime pickup,
			LocalDateTime returnDate) {

		long weeks = 0;

		LocalDateTime cursor = pickup;

		while (!cursor.plusDays(7).isAfter(returnDate)) {
			cursor = cursor.plusDays(7);
			weeks++;
		}

		long remainingDays = calculateBillableDays(
				Duration.between(cursor, returnDate)
				);

		BigDecimal weeklyAmount = pricing.getWeeklyPrice()
				.multiply(BigDecimal.valueOf(weeks));

		BigDecimal dailyAmount = pricing.getDailyPrice()
				.multiply(BigDecimal.valueOf(remainingDays));

		return weeklyAmount.add(dailyAmount);
	}

	// Monthly rental: complete calendar months + remaining daily charges
	private BigDecimal calculateMonthlyAmount(
			CarPricing pricing,
			LocalDateTime pickup,
			LocalDateTime returnDate) {

		long months = 0;

		LocalDateTime cursor = pickup;

		while (!cursor.plusMonths(1).isAfter(returnDate)) {
			cursor = cursor.plusMonths(1);
			months++;
		}

		long remainingDays = calculateBillableDays(
				Duration.between(cursor, returnDate)
				);

		BigDecimal monthlyAmount = pricing.getMonthlyPrice()
				.multiply(BigDecimal.valueOf(months));

		BigDecimal dailyAmount = pricing.getDailyPrice()
				.multiply(BigDecimal.valueOf(remainingDays));

		return monthlyAmount.add(dailyAmount);
	}
}
