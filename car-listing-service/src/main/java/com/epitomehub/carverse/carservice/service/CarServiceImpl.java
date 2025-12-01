package com.epitomehub.carverse.carservice.service;

import com.epitomehub.carverse.carservice.entity.Car;
import com.epitomehub.carverse.carservice.repository.CarRepository;
import com.epitomehub.carverse.carservice.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import static com.epitomehub.carverse.carservice.repository.CarSpecifications.withFilters;
@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    @Override
    public Car createCar(Car car) {
        car.setCreatedAt(LocalDateTime.now());
        car.setUpdatedAt(LocalDateTime.now());
        return carRepository.save(car);
    }

    @Override
    public Car getCar(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Car not found"));
    }

    @Override
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    @Override
    public Car updateCar(Long id, Car updatedCar) {
        Car car = getCar(id);

        car.setTitle(updatedCar.getTitle());
        car.setDescription(updatedCar.getDescription());
        car.setBrand(updatedCar.getBrand());
        car.setModel(updatedCar.getModel());
        car.setYear(updatedCar.getYear());
        car.setPrice(updatedCar.getPrice());
        car.setKmDriven(updatedCar.getKmDriven());
        car.setFuelType(updatedCar.getFuelType());
        car.setCity(updatedCar.getCity());
        car.setState(updatedCar.getState());
        car.setUpdatedAt(LocalDateTime.now());

        return carRepository.save(car);
    }

    @Override
    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }
    @Override
    public Page<Car> searchCars(
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
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return carRepository.findAll(
                withFilters(
                        brand,
                        model,
                        city,
                        state,
                        fuelType,
                        minPrice,
                        maxPrice,
                        minYear,
                        maxYear,
                        minKm,
                        maxKm
                ),
                pageable
        );
    }
}
