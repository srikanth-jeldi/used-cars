package com.epitomehub.carverse.carlisting.dto;

import java.util.List;

public record PublicCarResponse(
        Long id,
        String brand,
        String model,
        String variant,
        String fuelType,
        String transmission,
        Integer year,
        Double price,
        Integer kmsDriven,
        String city,
        String state,
        String title,
        List<PublicCarImageResponse> exteriorImages
) {}
