package com.epitomehub.carverse.carservice.mapper;

import com.epitomehub.carverse.carservice.dto.CarRequestDto;
import com.epitomehub.carverse.carservice.dto.CarResponseDto;
import com.epitomehub.carverse.carservice.entity.Car;
import com.epitomehub.carverse.carservice.entity.CarImage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CarMapper {

    public static Car toEntity(CarRequestDto dto) {
        if (dto == null) return null;

        Car car = new Car();
        car.setOwnerId(dto.getOwnerId());
        car.setTitle(dto.getTitle());
        car.setDescription(dto.getDescription());
        car.setBrand(dto.getBrand());
        car.setModel(dto.getModel());
        car.setYear(dto.getYear());
        car.setPrice(dto.getPrice());
        car.setKmDriven(dto.getKmDriven());
        car.setFuelType(dto.getFuelType());
        car.setCity(dto.getCity());
        car.setState(dto.getState());

        // Images: create CarImage entities
        if (dto.getImageUrls() != null && !dto.getImageUrls().isEmpty()) {
            List<CarImage> images = dto.getImageUrls().stream()
                    .map(url -> {
                        CarImage img = new CarImage();
                        img.setImageUrl(url);
                        img.setCar(car);
                        return img;
                    })
                    .collect(Collectors.toList());
            car.setImages(images);
        }

        return car;
    }

    public static void updateEntityFromDto(Car car, CarRequestDto dto) {
        if (car == null || dto == null) return;

        car.setOwnerId(dto.getOwnerId());
        car.setTitle(dto.getTitle());
        car.setDescription(dto.getDescription());
        car.setBrand(dto.getBrand());
        car.setModel(dto.getModel());
        car.setYear(dto.getYear());
        car.setPrice(dto.getPrice());
        car.setKmDriven(dto.getKmDriven());
        car.setFuelType(dto.getFuelType());
        car.setCity(dto.getCity());
        car.setState(dto.getState());
        // images update we will handle later (file-service phase)
    }

    public static CarResponseDto toDto(Car car) {
        if (car == null) return null;

        List<String> imageUrls = (car.getImages() == null)
                ? Collections.emptyList()
                : car.getImages().stream()
                .map(CarImage::getImageUrl)
                .collect(Collectors.toList());

        return CarResponseDto.builder()
                .id(car.getId())
                .ownerId(car.getOwnerId())
                .title(car.getTitle())
                .description(car.getDescription())
                .brand(car.getBrand())
                .model(car.getModel())
                .year(car.getYear())
                .price(car.getPrice())
                .kmDriven(car.getKmDriven())
                .fuelType(car.getFuelType())
                .city(car.getCity())
                .state(car.getState())
                .imageUrls(imageUrls)
                .createdAt(car.getCreatedAt())
                .updatedAt(car.getUpdatedAt())
                .build();
    }
}
