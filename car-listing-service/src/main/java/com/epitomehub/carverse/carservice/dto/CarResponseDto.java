package com.epitomehub.carverse.carservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarResponseDto {

    private Long id;
    private Long ownerId;

    private String title;
    private String description;

    private String brand;
    private String model;
    private int year;

    private double price;
    private int kmDriven;
    private String fuelType;

    private String city;
    private String state;

    private List<String> imageUrls;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
