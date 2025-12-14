package com.epitomehub.carverse.carlisting.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CarResponse {
    private Long id;
    private Long ownerId;

    private String title;
    private String brand;
    private String model;
    private Integer year;

    private Double price;
    private Integer kms;

    private String fuelType;
    private String transmission;

    private String city;
    private String state;

    private Boolean active;

    private Instant createdAt;
}
