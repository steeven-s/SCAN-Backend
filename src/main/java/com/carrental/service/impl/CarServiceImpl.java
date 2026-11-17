
package com.carrental.service.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.carrental.dto.CarPricingRequest;
import com.carrental.dto.CarPricingResponse;
import com.carrental.dto.CarResponse;
import com.carrental.dto.CreateCarRequest;
import com.carrental.entity.Car;
import com.carrental.entity.CarPricing;
import com.carrental.entity.CarStatus;
import com.carrental.repository.CarRepository;
import com.carrental.service.CarService;

@Service
@Transactional
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    // CREATE CAR
    @Override
    public CarResponse createCar(CreateCarRequest request) {

        String registrationNumber =
                normalizeRegistrationNumber(request.registrationNumber());

        if (carRepository.existsByRegistrationNumber(registrationNumber)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A car with this registration number already exists"
            );
        }

        Car car = new Car();

        mapRequestToCar(request, car);

        // New cars are available by default
        car.setStatus(CarStatus.AVAILABLE);

        CarPricing pricing = new CarPricing();

        mapPricingRequest(request.pricing(), pricing);

        // Maintain both sides of the relationship
        car.setPricing(pricing);

        // Cascade saves the pricing record too
        Car savedCar = carRepository.save(car);

        return mapToResponse(savedCar);
    }

    // GET ALL CARS
    @Override
    @Transactional(readOnly = true)
    public List<CarResponse> getAllCars() {

        return carRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // GET CAR BY ID
    @Override
    @Transactional(readOnly = true)
    public CarResponse getCarById(Long id) {

        Car car = findCarById(id);

        return mapToResponse(car);
    }

    // UPDATE CAR
    @Override
    public CarResponse updateCar(Long id, CreateCarRequest request) {

        Car car = findCarById(id);

        String registrationNumber =
                normalizeRegistrationNumber(request.registrationNumber());

        if (carRepository.existsByRegistrationNumberAndIdNot(
                registrationNumber, id)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Another car already uses this registration number"
            );
        }

        // Update car details
        mapRequestToCar(request, car);

        // Preserve the existing availability status
        // Do not reset status to AVAILABLE during an update

        CarPricing pricing = car.getPricing();

        if (pricing == null) {
            pricing = new CarPricing();
            car.setPricing(pricing);
        }

        // Update pricing details
        mapPricingRequest(request.pricing(), pricing);

        Car updatedCar = carRepository.save(car);

        return mapToResponse(updatedCar);
    }

    // DELETE CAR
    @Override
    public void deleteCar(Long id) {

        Car car = findCarById(id);

        carRepository.delete(car);
    }

    // FIND CAR OR THROW 404
    private Car findCarById(Long id) {

        return carRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Car not found with ID: " + id
                ));
    }

    // MAP REQUEST TO CAR ENTITY
    private void mapRequestToCar(
            CreateCarRequest request,
            Car car) {

        car.setBrand(request.brand().trim());
        car.setModel(request.model().trim());
        car.setManufacturingYear(request.manufacturingYear());

        car.setRegistrationNumber(
                normalizeRegistrationNumber(request.registrationNumber())
        );

        car.setCategory(request.category().trim().toUpperCase(Locale.ROOT));
        car.setFuelType(request.fuelType().trim().toUpperCase(Locale.ROOT));
        car.setTransmission(
                request.transmission().trim().toUpperCase(Locale.ROOT)
        );

        car.setSeatingCapacity(request.seatingCapacity());

        car.setDescription(
                request.description() == null
                        ? null
                        : request.description().trim()
        );
    }

 // MAP PRICING REQUEST TO PRICING ENTITY
    private void mapPricingRequest(
            CarPricingRequest request,
            CarPricing pricing) {

        pricing.setHourlyPrice(request.hourlyPrice());
        pricing.setHalfDayPrice(request.halfDayPrice());
        pricing.setDailyPrice(request.dailyPrice());
        pricing.setWeeklyPrice(request.weeklyPrice());
        pricing.setMonthlyPrice(request.monthlyPrice());
    }

 // MAP ENTITY TO RESPONSE DTO
    private CarResponse mapToResponse(Car car) {

        CarPricing pricing = car.getPricing();

        CarPricingResponse pricingResponse = null;

        if (pricing != null) {
            pricingResponse = new CarPricingResponse(
                    pricing.getHourlyPrice(),
                    pricing.getHalfDayPrice(),
                    pricing.getDailyPrice(),
                    pricing.getWeeklyPrice(),
                    pricing.getMonthlyPrice()
            );
        }

        return new CarResponse(
                car.getId(),
                car.getBrand(),
                car.getModel(),
                car.getManufacturingYear(),
                car.getRegistrationNumber(),
                car.getCategory(),
                car.getFuelType(),
                car.getTransmission(),
                car.getSeatingCapacity(),
                car.getDescription(),
                car.getStatus(),
                pricingResponse,
                car.getCreatedAt(),
                car.getUpdatedAt()
        );
    }

    // NORMALIZE REGISTRATION NUMBER
    private String normalizeRegistrationNumber(String registrationNumber) {

        return registrationNumber.trim().toUpperCase(Locale.ROOT);
    }
}
