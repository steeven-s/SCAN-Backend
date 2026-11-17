
package com.carrental.controller;

import com.carrental.dto.CustomerCarResponse;
import com.carrental.service.CustomerCarService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CustomerCarController {

    private final CustomerCarService customerCarService;

    public CustomerCarController(
            CustomerCarService customerCarService) {

        this.customerCarService = customerCarService;
    }

    // Get all available cars
    @GetMapping
    public ResponseEntity<List<CustomerCarResponse>> getAvailableCars() {

        List<CustomerCarResponse> cars =
                customerCarService.getAvailableCars();

        return ResponseEntity.ok(cars);
    }

    // Get available car by ID
    @GetMapping("/{id}")
    public ResponseEntity<CustomerCarResponse> getAvailableCarById(
            @PathVariable Long id) {

        CustomerCarResponse car =
                customerCarService.getAvailableCarById(id);

        return ResponseEntity.ok(car);
    }
}
