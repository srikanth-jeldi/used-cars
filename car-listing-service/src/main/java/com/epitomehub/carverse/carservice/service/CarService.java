package com.epitomehub.carverse.carservice.service;

import com.epitomehub.carverse.carservice.entity.Car;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CarService {

    Car createCar(Car car);

    Car getCar(Long id);

    List<Car> getAllCars();

    Car updateCar(Long id, Car updatedCar);

    void deleteCar(Long id);

    Page<Car> searchCars(
            String brand,
            String model,
            String city,
            String state,
            String fuelType,
            Double minPrice,
            Double maxPrice,
            Integer minYear,
            Integer maxYear,
            Integer minKm,
            Integer maxKm,
            int page,
            int size,
            String sortBy,
            String sortDir
    );
}
