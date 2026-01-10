package com.epitomehub.carverse.carlisting.dto;

import lombok.Data;

@Data
public class CarSearchRequest {
    private String keyword;
    private String brand;
    private String model;
    private String variant;
    private String fuelType;
    private String transmission;
    private String city;
    private String state;

    private Long ownerId;
    private String status;

    private Integer minYear;
    private Integer maxYear;
    private Double minPrice;
    private Double maxPrice;
    private Integer minKmDriven;
    private Integer maxKmDriven;

    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "createdAt";
    private String sortDir = "desc";
}
