
package com.carrental.service;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.carrental.dto.CarImageResponse;
import com.carrental.entity.Car;
import com.carrental.entity.CarImage;
import com.carrental.repository.CarImageRepository;
import com.carrental.repository.CarRepository;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
public class CarImageService {
	private static final Logger log =
	        LoggerFactory.getLogger(CarImageService.class);
	private final CarImageRepository carImageRepository;
	private final CarRepository carRepository;
	private final CloudinaryService cloudinaryService;

	public CarImageService(
			CarImageRepository carImageRepository,
			CarRepository carRepository,
			CloudinaryService cloudinaryService) {

		this.carImageRepository = carImageRepository;
		this.carRepository = carRepository;
		this.cloudinaryService = cloudinaryService;
	}

	@Transactional
	public CarImageResponse uploadImage(
			Long carId,
			MultipartFile file) {

		// 1. Verify car exists
		Car car = carRepository.findById(carId)
		.orElseThrow(() -> new ResponseStatusException(
				HttpStatus.NOT_FOUND,
				"Car not found with ID: " + carId
				));

		// 2. Upload image to Cloudinary
		Map<String, Object> uploadResult =
				cloudinaryService.uploadImage(file);

		String imageUrl = (String) uploadResult.get("imageUrl");
		String publicId = (String) uploadResult.get("publicId");

		try {

			// 3. Create CarImage entity
			CarImage carImage = new CarImage();

			carImage.setCar(car);
			carImage.setImageUrl(imageUrl);
			carImage.setPublicId(publicId);

			// First image becomes primary
			boolean firstImage =
					carImageRepository.countByCarId(carId) == 0;

			carImage.setIsPrimary(firstImage);

			// 4. Save in database and flush
			CarImage savedImage =
					carImageRepository.saveAndFlush(carImage);

			// 5. Return response
			return mapToResponse(savedImage);

		} catch (RuntimeException exception) {

			// Compensate if database persistence fails
			try {
				cloudinaryService.deleteImage(publicId);
			} catch (RuntimeException cleanupException) {
				exception.addSuppressed(cleanupException);
			}

			throw exception;
		}
	}

	private CarImageResponse mapToResponse(CarImage image) {

		return new CarImageResponse(
				image.getId(),
				image.getCar().getId(),
				image.getImageUrl(),
				image.getPublicId(),
				image.getIsPrimary(),
				image.getCreatedAt()
				);
	}


	@Transactional(readOnly = true)
	public java.util.List<CarImageResponse> getCarImages(Long carId) {

		if (!carRepository.existsById(carId)) {
			throw new ResponseStatusException(
					HttpStatus.NOT_FOUND,
					"Car not found with ID: " + carId
					);
		}

		return carImageRepository
				.findByCarIdOrderByIdAsc(carId)
				.stream()
				.map(this::mapToResponse)
				.toList();
	}
	
	@Transactional
	public CarImageResponse setPrimaryImage(Long carId, Long imageId) {

	    if (!carRepository.existsById(carId)) {
	        throw new ResponseStatusException(
	                HttpStatus.NOT_FOUND,
	                "Car not found with ID: " + carId
	        );
	    }

	    CarImage selectedImage = carImageRepository
	            .findByIdAndCarId(imageId, carId)
	            .orElseThrow(() -> new ResponseStatusException(
	                    HttpStatus.NOT_FOUND,
	                    "Image not found for this car"
	            ));

	    List<CarImage> images =
	            carImageRepository.findByCarIdOrderByIdAsc(carId);

	    // Remove primary status from all images
	    for (CarImage image : images) {
	        image.setIsPrimary(false);
	    }

	    // Assign primary status to selected image
	    selectedImage.setIsPrimary(true);

	    carImageRepository.saveAll(images);

	    return mapToResponse(selectedImage);
	}
	
	@Transactional
	public void deleteImage(Long carId, Long imageId) {

	    CarImage image = carImageRepository
	            .findByIdAndCarId(imageId, carId)
	            .orElseThrow(() -> new ResponseStatusException(
	                    HttpStatus.NOT_FOUND,
	                    "Image not found for this car"
	            ));

	    String publicId = image.getPublicId();

	    // Delete database record first
	    carImageRepository.delete(image);
	    carImageRepository.flush();

	    // Delete Cloudinary image only after DB transaction commits
	    TransactionSynchronizationManager.registerSynchronization(
	            new TransactionSynchronization() {

	                @Override
	                public void afterCommit() {
	                    try {
	                        cloudinaryService.deleteImage(publicId);
	                    } catch (RuntimeException exception) {
	                        log.error(
	                                "Failed to delete Cloudinary image: {}",
	                                publicId,
	                                exception
	                        );
	                    }
	                }
	            }
	    );
	}
	
	

}
