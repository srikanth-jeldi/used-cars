package com.epitomehub.carverse.carlisting.service;

import com.epitomehub.carverse.carlisting.dto.CarRequestDto;
import com.epitomehub.carverse.carlisting.dto.CarResponseDto;
import com.epitomehub.carverse.carlisting.entity.Car;
import com.epitomehub.carverse.carlisting.exception.ResourceNotFoundException;
import com.epitomehub.carverse.carlisting.mapper.CarMapper;
import com.epitomehub.carverse.carlisting.repository.CarRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import static com.epitomehub.carverse.carlisting.repository.CarSpecifications.withFilters;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    @Override
    public CarResponseDto createCar(CarRequestDto dto) {
        if (dto.getOwnerId() == null) {
            throw new IllegalArgumentException("ownerId missing in request (set from JWT)");
        }
        Car car = CarMapper.toEntity(dto);
        Car saved = carRepository.save(car);
        return CarMapper.toDto(saved);
    }

    @Override
    public CarResponseDto getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found: " + id));
        return CarMapper.toDto(car);
    }

    @Override
    public Page<CarResponseDto> getAllCars(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        return carRepository.findAll(pageable).map(CarMapper::toDto);
    }

    @Override
    public Page<CarResponseDto> searchCars(String brand, String model, String fuelType, String transmission, String city,
                                          Integer minYear, Integer maxYear, Double minPrice, Double maxPrice,
                                          int page, int size, String sort) {

        Specification<Car> spec = withFilters(brand, model, fuelType, transmission, city, minYear, maxYear, minPrice, maxPrice);
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        return carRepository.findAll(spec, pageable).map(CarMapper::toDto);
    }

    @Override
    public CarResponseDto updateCar(Long id, CarRequestDto dto) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found: " + id));

        // Optional ownership check (if you want strict auth):
        // if (dto.getOwnerId() != null && !dto.getOwnerId().equals(car.getOwnerId())) throw new AccessDeniedException("Not owner");

        CarMapper.updateEntity(car, dto);
        Car saved = carRepository.save(car);
        return CarMapper.toDto(saved);
    }

    @Override
    public void deleteCar(Long id) {
        if (!carRepository.existsById(id)) {
            throw new ResourceNotFoundException("Car not found: " + id);
        }
        carRepository.deleteById(id);
    }

    private Sort parseSort(String sort) {
        // sort example: "createdAt,desc" or "price,asc"
        if (sort == null || sort.isBlank()) return Sort.by(Sort.Direction.DESC, "createdAt");
        String[] parts = sort.split(",");
        String field = parts[0].trim();
        Sort.Direction dir = (parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim())) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(dir, field);
    }
}
