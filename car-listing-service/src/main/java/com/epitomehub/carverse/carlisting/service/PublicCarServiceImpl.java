package com.epitomehub.carverse.carlisting.service;

import com.epitomehub.carverse.carlisting.dto.CarSearchRequest;
import com.epitomehub.carverse.carlisting.dto.PublicCarImageResponse;
import com.epitomehub.carverse.carlisting.dto.PublicCarResponse;
import com.epitomehub.carverse.carlisting.entity.Car;
import com.epitomehub.carverse.carlisting.entity.CarImageType;
import com.epitomehub.carverse.carlisting.repository.CarImageRepository;
import com.epitomehub.carverse.carlisting.repository.CarRepository;
import com.epitomehub.carverse.carlisting.specification.CarSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicCarServiceImpl implements PublicCarService {

    private final CarRepository carRepository;
    private final CarImageRepository carImageRepository;

    @Override
    public Page<PublicCarResponse> searchPublic(
            String brand, String model, String variant, String fuelType, String transmission, String city,
            Integer minYear, Integer maxYear, Double minPrice, Double maxPrice,
            int page, int size, String sort
    ) {
        CarSearchRequest req = new CarSearchRequest();
        req.setBrand(brand);
        req.setModel(model);
        req.setModel(variant);
        req.setFuelType(fuelType);
        req.setTransmission(transmission);
        req.setCity(city);
        req.setMinYear(minYear);
        req.setMaxYear(maxYear);
        req.setMinPrice(minPrice);
        req.setMaxPrice(maxPrice);
        req.setPage(page);
        req.setSize(size);

        Specification<Car> spec = CarSpecification.withFilters(req);
        Pageable pageable = PageRequest.of(page, size, parseSort(sort));

        return carRepository.findAll(spec, pageable).map(this::toPublicResponse);
    }

    @Override
    public PublicCarResponse getPublicCar(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found: " + carId));
        return toPublicResponse(car);
    }

    private PublicCarResponse toPublicResponse(Car car) {
        // IMPORTANT: use the repository method that exists (see repository section below)
        List<PublicCarImageResponse> exteriorImages =
                carImageRepository.findByCarIdAndImageTypeOrderByIdAsc(car.getId(), CarImageType.EXTERIOR)
                        .stream()
                        .map(img -> new PublicCarImageResponse(img.getId(), img.getUrl()))
                        .toList();

        return new PublicCarResponse(
                car.getId(),
                car.getBrand(),
                car.getModel(),
                car.getVariant(),
                car.getFuelType(),
                car.getTransmission(),
                car.getYear(),
                car.getPrice(),
                car.getKmsDriven(),
                car.getCity(),
                car.getState(),
                car.getTitle(),
                exteriorImages
        );
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        String[] parts = sort.split(",");
        String field = parts[0].trim();
        Sort.Direction dir = (parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim()))
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(dir, field);
    }
}
