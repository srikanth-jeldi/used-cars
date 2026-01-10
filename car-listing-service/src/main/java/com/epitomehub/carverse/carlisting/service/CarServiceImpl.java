package com.epitomehub.carverse.carlisting.service;

import com.epitomehub.carverse.carlisting.dto.CarRequestDto;
import com.epitomehub.carverse.carlisting.dto.CarResponseDto;
import com.epitomehub.carverse.carlisting.dto.CarSearchRequest;
import com.epitomehub.carverse.carlisting.entity.Car;
import com.epitomehub.carverse.carlisting.entity.CarStatus;
import com.epitomehub.carverse.carlisting.exception.ResourceNotFoundException;
import com.epitomehub.carverse.carlisting.mapper.CarMapper;
import com.epitomehub.carverse.carlisting.repository.CarRepository;
import com.epitomehub.carverse.carlisting.specification.CarSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final CarPublishValidator carPublishValidator;

    @Override
    @Transactional
    public CarResponseDto createCar(CarRequestDto dto) {
        Car car = CarMapper.toEntity(dto);
        car = carRepository.save(car);
        return CarMapper.toDto(car);
    }

    @Override
    public CarResponseDto getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found: " + id));
        return CarMapper.toDto(car);
    }

    @Override
    public Page<CarResponseDto> getAllCars(int page, int size, String sort) {
        // All nulls - public listing; CarSpecification.withFilters(req) should force PUBLISHED.
        CarSearchRequest req = new CarSearchRequest();
        Specification<Car> spec = CarSpecification.withFilters(req);

        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        return carRepository.findAll(spec, pageable).map(CarMapper::toDto);
    }

    @Override
    public Page<CarResponseDto> searchCars(
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
    ) {
        CarSearchRequest req = buildSearchRequest(
                brand, model, variant, fuelType, transmission, city,
                minYear, maxYear, minPrice, maxPrice,
                null // status null for public search; spec should force PUBLISHED
        );

        Specification<Car> spec = CarSpecification.withFilters(req);
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        return carRepository.findAll(spec, pageable).map(CarMapper::toDto);
    }

    @Override
    public Page<CarResponseDto> searchMyCars(
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
            String status,
            int page,
            int size,
            String sort
    ) {
        CarSearchRequest req = buildSearchRequest(
                brand, model, variant, fuelType, transmission, city,
                minYear, maxYear, minPrice, maxPrice,
                status // allow DRAFT/PUBLISHED/SOLD etc for owner/admin search
        );

        Specification<Car> spec = CarSpecification.withMyFilters(req, ownerId);
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        return carRepository.findAll(spec, pageable).map(CarMapper::toDto);
    }

    @Override
    @Transactional
    public CarResponseDto updateCar(Long id, CarRequestDto dto) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found: " + id));

        CarMapper.updateEntity(car, dto);
        car = carRepository.save(car);

        return CarMapper.toDto(car);
    }

    @Override
    @Transactional
    public void deleteCar(Long id) {
        if (!carRepository.existsById(id)) {
            throw new ResourceNotFoundException("Car not found: " + id);
        }
        carRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void publishCar(Long ownerId, Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found: " + carId));

        if (!ownerId.equals(car.getOwnerId())) {
            throw new AccessDeniedException("Forbidden");
        }

        carPublishValidator.validateOrThrow(carId);

        car.setStatus(CarStatus.PUBLISHED);
        car.setPublishedAt(LocalDateTime.now());

        carRepository.save(car);
    }

    private CarSearchRequest buildSearchRequest(
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
            String status
    ) {
        CarSearchRequest req = new CarSearchRequest();
        req.setBrand(brand);
        req.setModel(model);
        req.setVariant(variant);
        req.setFuelType(fuelType);
        req.setTransmission(transmission);
        req.setCity(city);
        req.setMinYear(minYear);
        req.setMaxYear(maxYear);
        req.setMinPrice(minPrice);
        req.setMaxPrice(maxPrice);

        // If your CarSearchRequest has these fields, keep them null.
        // req.setMinKm(null);
        // req.setMaxKm(null);

        // status is only used for /me search; for public search it stays null
        // and CarSpecification.withFilters(req) should force PUBLISHED.
        req.setStatus(status);

        return req;
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) return Sort.by(Sort.Direction.DESC, "createdAt");

        String[] parts = sort.split(",");
        String field = parts[0].trim();
        Sort.Direction dir = (parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim()))
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return Sort.by(dir, field);
    }
}
