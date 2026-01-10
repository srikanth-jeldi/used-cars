package com.epitomehub.carverse.carlisting.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CarRequestDto {

    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    @NotBlank
    private String variant;
    @NotBlank
    private String fuelType;

    @NotBlank
    private String transmission;

    @NotNull
    @Min(1950)
    @Max(2100)
    private Integer year;

    @NotNull
    @Positive
    private Double price;

    @NotNull
    @Positive
    private Integer kmsDriven;

    @NotBlank
    private String city;

    private String state;

    private String area;
    private String pincode;
    private Double lat;
    private Double lng;

    private String title;
    private String description;

    // Set only from auth/gateway (do not trust client input)
    private Long ownerId;
}
