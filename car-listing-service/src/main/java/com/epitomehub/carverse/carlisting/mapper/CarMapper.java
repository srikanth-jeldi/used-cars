package com.epitomehub.carverse.carlisting.mapper;

import com.epitomehub.carverse.carlisting.dto.CarRequestDto;
import com.epitomehub.carverse.carlisting.dto.CarResponseDto;
import com.epitomehub.carverse.carlisting.entity.Car;
import com.epitomehub.carverse.carlisting.entity.CarImage;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class CarMapper {

    private CarMapper() {}

    public static Car toEntity(CarRequestDto dto) {
        if (dto == null) return null;

        return Car.builder()
                .brand(dto.getBrand())
                .model(dto.getModel())
                .fuelType(dto.getFuelType())
                .transmission(dto.getTransmission())
                .year(dto.getYear())
                .price(dto.getPrice())
                .kmsDriven(dto.getKmsDriven())
                .city(dto.getCity())
                .state(dto.getState())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .ownerId(dto.getOwnerId())
                .build();
    }

    public static void updateEntity(Car entity, CarRequestDto dto) {
        entity.setBrand(dto.getBrand());
        entity.setModel(dto.getModel());
        entity.setFuelType(dto.getFuelType());
        entity.setTransmission(dto.getTransmission());
        entity.setYear(dto.getYear());
        entity.setPrice(dto.getPrice());
        entity.setKmsDriven(dto.getKmsDriven());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        // ownerId should not be overwritten unless provided (from token)
        if (dto.getOwnerId() != null) {
            entity.setOwnerId(dto.getOwnerId());
        }
    }

    public static CarResponseDto toDto(Car car) {
        if (car == null) return null;

        List<String> imageUrls = car.getImages() == null ? Collections.emptyList() :
                car.getImages().stream()
                        .map(CarImage::getImageUrl)
                        .filter(u -> u != null && !u.isBlank())
                        .collect(Collectors.toList());

        return CarResponseDto.builder()
                .id(car.getId())
                .title(car.getTitle())
                .description(car.getDescription())
                .brand(car.getBrand())
                .model(car.getModel())
                .fuelType(car.getFuelType())
                .transmission(car.getTransmission())
                .year(car.getYear())
                .price(car.getPrice())
                .kmsDriven(car.getKmsDriven())
                .city(car.getCity())
                .state(car.getState())
                .ownerId(car.getOwnerId())
                .imageUrls(imageUrls)
                .createdAt(car.getCreatedAt())
                .updatedAt(car.getUpdatedAt())
                .build();
    }
}
