package com.epitomehub.carverse.carlisting.controller;

import com.epitomehub.carverse.carlisting.dto.PublicCarResponse;
import com.epitomehub.carverse.carlisting.service.PublicCarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/cars")
public class PublicCarController {

    private final PublicCarService publicCarService;

    @GetMapping("/{carId}")
    public PublicCarResponse getPublic(@PathVariable Long carId) {
        return publicCarService.getPublicCar(carId);
    }

    @GetMapping
    public Page<PublicCarResponse> listPublic(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String variant,
            @RequestParam(required = false) String fuelType,
            @RequestParam(required = false) String transmission,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer minYear,
            @RequestParam(required = false) Integer maxYear,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        return publicCarService.searchPublic(
                brand, model, variant, fuelType, transmission, city,
                minYear, maxYear, minPrice, maxPrice,
                page, size, sort
        );
    }
}