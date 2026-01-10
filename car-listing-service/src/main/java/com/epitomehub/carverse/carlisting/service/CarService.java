package com.epitomehub.carverse.carlisting.service;

import com.epitomehub.carverse.carlisting.dto.CarRequestDto;
import com.epitomehub.carverse.carlisting.dto.CarResponseDto;
import org.springframework.data.domain.Page;

public interface CarService {

    CarResponseDto createCar(CarRequestDto dto);

    CarResponseDto getCarById(Long id);

    Page<CarResponseDto> getAllCars(int page, int size, String sort);

    /**
     * Public search (guest allowed) - ONLY PUBLISHED cars.
     */
    Page<CarResponseDto> searchCars(
            String brand,
            String model,
            String variant,
            String fuelType,
            String transmission,
            String city,
            Integer minYear,
            Integer maxYear,
            Double minPrice,
            Double maxPrice,
            int page,
            int size,
            String sort
    );

    /**
     * Owner/Admin search (My listings) - can include DRAFT, PUBLISHED, SOLD
     * status is optional (null => all statuses).
     */
    Page<CarResponseDto> searchMyCars(
            Long ownerId,
            String brand,
            String model,
            String variant,
            String fuelType,
            String transmission,
            String city,
            Integer minYear,
            Integer maxYear,
            Double minPrice,
            Double maxPrice,
            String status, // DRAFT/PUBLISHED/SOLD (optional)
            int page,
            int size,
            String sort
    );

    CarResponseDto updateCar(Long id, CarRequestDto dto);

    void deleteCar(Long id);

    void publishCar(Long ownerId, Long carId);
}