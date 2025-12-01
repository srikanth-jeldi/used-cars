package com.epitomehub.carverse.carservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cars")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ownerId; // Auth user ID

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

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CarImage> images;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
