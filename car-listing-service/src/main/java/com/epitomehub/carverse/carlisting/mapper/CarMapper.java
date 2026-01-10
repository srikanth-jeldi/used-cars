package com.epitomehub.carverse.carlisting.mapper;

import com.epitomehub.carverse.carlisting.dto.CarRequestDto;
import com.epitomehub.carverse.carlisting.dto.CarResponseDto;
import com.epitomehub.carverse.carlisting.entity.Car;
import com.epitomehub.carverse.carlisting.entity.CarImage;
import com.epitomehub.carverse.carlisting.entity.CarStatus;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class CarMapper {

    private CarMapper() {}

    public static Car toEntity(CarRequestDto dto) {
        if (dto == null) return null;

        return Car.builder()
                .brand(dto.getBrand())
                .model(dto.getModel())
                .variant(dto.getVariant())
                .fuelType(dto.getFuelType())
                .transmission(dto.getTransmission())
                .year(dto.getYear())
                .price(dto.getPrice())
                .kmsDriven(dto.getKmsDriven())
                .city(dto.getCity())
                .state(dto.getState())
                .area(dto.getArea())
                .pincode(dto.getPincode())
                .lat(dto.getLat())
                .lng(dto.getLng())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .ownerId(dto.getOwnerId())
                .status(CarStatus.DRAFT) // default draft on create
                .build();
    }

    public static void updateEntity(Car entity, CarRequestDto dto) {
        entity.setBrand(dto.getBrand());
        entity.setModel(dto.getModel());
        entity.setVariant(dto.getVariant());
        entity.setFuelType(dto.getFuelType());
        entity.setTransmission(dto.getTransmission());
        entity.setYear(dto.getYear());
        entity.setPrice(dto.getPrice());
        entity.setKmsDriven(dto.getKmsDriven());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setArea(dto.getArea());
        entity.setPincode(dto.getPincode());
        entity.setLat(dto.getLat());
        entity.setLng(dto.getLng());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());

        // ownerId should only be set from auth context (controller)
        if (dto.getOwnerId() != null) {
            entity.setOwnerId(dto.getOwnerId());
        }
    }

    public static CarResponseDto toDto(Car car) {
        if (car == null) return null;

        List<String> imageUrls = car.getImages() == null
                ? Collections.emptyList()
                : car.getImages().stream()
                .map(CarImage::getUrl)          // <-- FIX 1
                .filter(Objects::nonNull)       // <-- FIX 2
                .filter(u -> !u.isBlank())       // <-- FIX 3 (now String)
                .toList();

        return CarResponseDto.builder()
                .id(car.getId())
                .title(car.getTitle())
                .description(car.getDescription())
                .brand(car.getBrand())
                .model(car.getModel())
                .variant(car.getVariant())
                .fuelType(car.getFuelType())
                .transmission(car.getTransmission())
                .year(car.getYear())
                .price(car.getPrice())
                .kmsDriven(car.getKmsDriven())
                .city(car.getCity())
                .state(car.getState())
                .area(car.getArea())
                .pincode(car.getPincode())
                .lat(car.getLat())
                .lng(car.getLng())
                .status(car.getStatus() == null ? null : car.getStatus().name())
                .ownerId(car.getOwnerId())
                .imageUrls(imageUrls)
                .createdAt(car.getCreatedAt())
                .updatedAt(car.getUpdatedAt())
                .publishedAt(car.getPublishedAt())
                .soldAt(car.getSoldAt())
                .build();
    }
}
