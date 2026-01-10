package com.epitomehub.carverse.carlisting.controller;

import com.epitomehub.carverse.carlisting.dto.CarRequestDto;
import com.epitomehub.carverse.carlisting.dto.CarResponseDto;
import com.epitomehub.carverse.carlisting.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;

    @PostMapping
    public ResponseEntity<CarResponseDto> create(@Valid @RequestBody CarRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.createCar(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @GetMapping
    public ResponseEntity<Page<CarResponseDto>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        return ResponseEntity.ok(carService.getAllCars(page, size, sort));
    }

    /**
     * PUBLIC SEARCH (guest allowed) - ONLY PUBLISHED cars.
     * Existing endpoint remains same.
     */
    @GetMapping("/search")
    public ResponseEntity<Page<CarResponseDto>> searchCars(
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
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        return ResponseEntity.ok(
                carService.searchCars(
                        brand, model, variant, fuelType, transmission, city,
                        minYear, maxYear, minPrice, maxPrice,
                        page, size, sort
                )
        );
    }

    /**
     * MY CARS SEARCH (authenticated) - ALL statuses for owner.
     */
    @GetMapping("/me")
    public ResponseEntity<Page<CarResponseDto>> getMyCars(
            Authentication authentication,
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
            @RequestParam(required = false) String status, // DRAFT/PUBLISHED/SOLD (optional)
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        Long ownerId = requireUserId(authentication);

        return ResponseEntity.ok(
                carService.searchMyCars(
                        ownerId,
                        brand, model, variant, fuelType, transmission, city,
                        minYear, maxYear, minPrice, maxPrice,
                        status,
                        page, size, sort
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarResponseDto> update(@PathVariable Long id, @Valid @RequestBody CarRequestDto dto) {
        return ResponseEntity.ok(carService.updateCar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{carId}/publish")
    public ResponseEntity<Void> publish(@PathVariable Long carId, Authentication authentication) {
        Long ownerId = requireUserId(authentication);
        carService.publishCar(ownerId, carId);
        return ResponseEntity.ok().build();
    }

    private Long requireUserId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Long l) return l;
        if (principal instanceof Integer i) return i.longValue();

        if (principal instanceof String s) {
            return Long.parseLong(s);
        }

        // If your JWT stores userId somewhere else, adjust here.
        throw new org.springframework.security.access.AccessDeniedException("Unauthorized principal type: " + principal.getClass());
    }
}