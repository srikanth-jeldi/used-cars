package com.epitomehub.carverse.carservice.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class CarRequestDto {

    private Long ownerId; // later JWT nundi set cheddam

    @NotBlank
    private String title;

    private String description;

    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    @Min(1900)
    private int year;

    @Positive
    private double price;

    @PositiveOrZero
    private int kmDriven;

    @NotBlank
    private String fuelType;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    // For now: list of image URLs (later file-service integrate avvachu)
    private List<String> imageUrls;

}
