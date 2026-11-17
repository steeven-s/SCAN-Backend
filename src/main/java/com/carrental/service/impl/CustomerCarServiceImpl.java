
package com.carrental.service.impl;

import com.carrental.dto.CarPricingResponse;
import com.carrental.dto.CustomerCarResponse;
import com.carrental.entity.Car;
import com.carrental.entity.CarImage;
import com.carrental.entity.CarStatus;
import com.carrental.repository.CarImageRepository;
import com.carrental.repository.CarRepository;
import com.carrental.service.CustomerCarService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CustomerCarServiceImpl implements CustomerCarService {

    private final CarRepository carRepository;
    private final CarImageRepository carImageRepository;

    public CustomerCarServiceImpl(
            CarRepository carRepository,
            CarImageRepository carImageRepository) {

        this.carRepository = carRepository;
        this.carImageRepository = carImageRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerCarResponse> getAvailableCars() {

        List<Car> cars = carRepository
                .findByStatusOrderByCreatedAtDesc(CarStatus.AVAILABLE);

        return cars.stream()
                .map(this::mapToCustomerResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerCarResponse getAvailableCarById(Long id) {

        Car car = carRepository.findById(id)
                .filter(existingCar ->
                        existingCar.getStatus() == CarStatus.AVAILABLE)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Available car not found with ID: " + id
                        )
                );

        return mapToCustomerResponse(car);
    }

    private CustomerCarResponse mapToCustomerResponse(Car car) {

        List<CustomerCarResponse.CustomerCarImageResponse> images =
                carImageRepository
                        .findByCarIdOrderByIdAsc(car.getId())
                        .stream()
                        .map(this::mapImage)
                        .toList();

        CarPricingResponse pricing = null;

        if (car.getPricing() != null) {
        	pricing = new CarPricingResponse(
        	        car.getPricing().getHourlyPrice(),
        	        car.getPricing().getHalfDayPrice(),
        	        car.getPricing().getDailyPrice(),
        	        car.getPricing().getWeeklyPrice(),
        	        car.getPricing().getMonthlyPrice()
        	);
        }

        return new CustomerCarResponse(
                car.getId(),
                car.getBrand(),
                car.getModel(),
                car.getManufacturingYear(),
                car.getCategory(),
                car.getFuelType(),
                car.getTransmission(),
                car.getSeatingCapacity(),
                car.getDescription(),
                car.getStatus(),
                pricing,
                images
        );
    }

    private CustomerCarResponse.CustomerCarImageResponse mapImage(
            CarImage image) {

        return new CustomerCarResponse.CustomerCarImageResponse(
                image.getId(),
                image.getImageUrl(),
                image.getIsPrimary()
        );
    }
}
