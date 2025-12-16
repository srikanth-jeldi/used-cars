package com.epitomehub.carverse.carservice.controller;

import com.epitomehub.carverse.carservice.dto.CarRequestDto;
import com.epitomehub.carverse.carservice.dto.CarResponseDto;
import com.epitomehub.carverse.carservice.entity.Car;
import com.epitomehub.carverse.carservice.mapper.CarMapper;
import com.epitomehub.carverse.carservice.security.JwtAuthenticationFilter;
import com.epitomehub.carverse.carservice.service.CarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    // ---------------------- CREATE CAR ----------------------
    @PostMapping
    public ResponseEntity<CarResponseDto> createCar(
            @Valid @RequestBody CarRequestDto request,
            Authentication authentication
    ) {
        Long ownerId = null;

        if (authentication != null &&
                authentication.getPrincipal() instanceof JwtAuthenticationFilter.UserPrincipal principal) {
            ownerId = principal.getUserId();
        }

        // Dev fallback: body lo ownerId unte use chey (future lo remove cheyochu)
        if (ownerId == null) {
            ownerId = request.getOwnerId();
        }

        Car car = CarMapper.toEntity(request);   // static usage
        car.setOwnerId(ownerId);

        Car saved = carService.createCar(car);
        CarResponseDto responseDto = CarMapper.toDto(saved); // static usage

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // ---------------------- GET ONE CAR ----------------------
    @GetMapping("/{id}")
    public ResponseEntity<CarResponseDto> getCar(@PathVariable Long id) {
        Car car = carService.getCar(id);
        return ResponseEntity.ok(CarMapper.toDto(car));
    }

    // ---------------------- GET ALL CARS (NO FILTERS) ----------------------
    @GetMapping
    public ResponseEntity<List<CarResponseDto>> getAllCars() {
        List<Car> cars = carService.getAllCars();

        List<CarResponseDto> response = cars.stream()
                .map(CarMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // ---------------------- UPDATE CAR ----------------------
    @PutMapping("/{id}")
    public ResponseEntity<CarResponseDto> updateCar(
            @PathVariable Long id,
            @Valid @RequestBody CarRequestDto request,
            Authentication authentication
    ) {
        Long ownerId = null;

        if (authentication != null &&
                authentication.getPrincipal() instanceof JwtAuthenticationFilter.UserPrincipal principal) {
            ownerId = principal.getUserId();
        }

        Car updatedData = CarMapper.toEntity(request);
        if (ownerId != null) {
            updatedData.setOwnerId(ownerId);
        }

        Car updated = carService.updateCar(id, updatedData);
        return ResponseEntity.ok(CarMapper.toDto(updated));
    }

    // ---------------------- DELETE CAR ----------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------------- SEARCH + FILTERS + PAGINATION ----------------------
    @GetMapping("/search")
    public ResponseEntity<Page<CarResponseDto>> searchCars(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String fuelType,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer minYear,
            @RequestParam(required = false) Integer maxYear,
            @RequestParam(required = false) Integer minKm,
            @RequestParam(required = false) Integer maxKm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Page<Car> result = carService.searchCars(
                brand, model, city, state, fuelType,
                minPrice, maxPrice,
                minYear, maxYear,
                minKm, maxKm,
                page, size,
                sortBy, sortDir
        );

        Page<CarResponseDto> dtoPage = result.map(CarMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }
}
