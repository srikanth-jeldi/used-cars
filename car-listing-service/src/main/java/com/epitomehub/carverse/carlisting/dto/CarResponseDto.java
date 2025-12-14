package com.epitomehub.carverse.carlisting.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CarResponseDto {
    private Long id;

    private String title;
    private String description;

    private String brand;
    private String model;
    private String fuelType;
    private String transmission;

    private Integer year;
    private Double price;
    private Integer kmsDriven;

    private String city;
    private String state;

    private Long ownerId;

    private List<String> imageUrls;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
