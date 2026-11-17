
package com.carrental.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.carrental.dto.CarImageResponse;
import com.carrental.service.CarImageService;

@RestController
@RequestMapping("/api/admin/cars/{carId}/images")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCarImageController {

    private final CarImageService carImageService;

    public AdminCarImageController(
            CarImageService carImageService) {
        this.carImageService = carImageService;
    }

    // Upload car image
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CarImageResponse> uploadImage(
            @PathVariable Long carId,
            @RequestParam("file") MultipartFile file) {

        CarImageResponse response =
                carImageService.uploadImage(carId, file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get all images for a car
    @GetMapping
    public ResponseEntity<List<CarImageResponse>> getCarImages(
            @PathVariable Long carId) {

        return ResponseEntity.ok(
                carImageService.getCarImages(carId)
        );
    }
    
    @PutMapping("/{imageId}/primary")
    public ResponseEntity<CarImageResponse> setPrimaryImage(
            @PathVariable Long carId,
            @PathVariable Long imageId) {

        return ResponseEntity.ok(
                carImageService.setPrimaryImage(carId, imageId)
        );
    }
    
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long carId,
            @PathVariable Long imageId) {

        carImageService.deleteImage(carId, imageId);

        return ResponseEntity.noContent().build();
    }
    
    
}
