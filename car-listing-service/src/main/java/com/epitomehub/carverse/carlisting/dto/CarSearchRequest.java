package com.epitomehub.carverse.carlisting.dto;

import lombok.Data;

@Data
public class CarSearchRequest {
    private String keyword;
    private String brand;
    private String model;
    private String fuelType;
    // Not present in entity currently; keep only if you plan to add later
    private String transmission;

    private String city;
    private String state;

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
