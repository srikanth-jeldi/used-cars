package com.epitomehub.carverse.carlisting.service;

import com.epitomehub.carverse.carlisting.dto.PublicCarResponse;
import org.springframework.data.domain.Page;

public interface PublicCarService {

    Page<PublicCarResponse> searchPublic(
            String brand, String model,String variant, String fuelType, String transmission, String city,
            Integer minYear, Integer maxYear, Double minPrice, Double maxPrice,
            int page, int size, String sort
    );

    PublicCarResponse getPublicCar(Long carId);
}
