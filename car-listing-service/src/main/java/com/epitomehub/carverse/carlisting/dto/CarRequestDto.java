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
    private String fuelType; // PETROL / DIESEL / EV etc

    @NotBlank
    private String transmission; // MANUAL / AUTOMATIC etc

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

    private String title;

    private String description;

    // populated from JWT (ownerId from token). Not required in request.
    private Long ownerId;
}
