
package com.carrental.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carrental.entity.CarImage;

public interface CarImageRepository
        extends JpaRepository<CarImage, Long> {

    List<CarImage> findByCarIdOrderByIdAsc(Long carId);

    Optional<CarImage> findByIdAndCarId(Long id, Long carId);

    boolean existsByCarIdAndIsPrimaryTrue(Long carId);

    long countByCarId(Long carId);
    

}
