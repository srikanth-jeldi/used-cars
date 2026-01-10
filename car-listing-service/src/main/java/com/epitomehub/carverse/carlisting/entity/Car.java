package com.epitomehub.carverse.carlisting.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(length = 100)
    private String variant;

    // (Optional for later price-insight) keep future ready:
    // private String variant;

    @Column(name = "fuel_type", nullable = false)
    private String fuelType;

    @Column(nullable = false)
    private String transmission;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Double price;

    @Column(name = "km_driven", nullable = false)
    private Integer kmsDriven;

    // Location
    @Column(nullable = false)
    private String city;

    private String state;

    private String area;     // locality
    private String pincode;  // optional but useful

    private Double lat;      // optional for "near me"
    private Double lng;      // optional for "near me"

    private String title;

    @Column(length = 2000)
    private String description;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarStatus status;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "sold_at")
    private LocalDateTime soldAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CarImage> images = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = CarStatus.DRAFT;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void replaceImages(List<CarImage> newImages) {
        this.images.clear();
        if (newImages != null) {
            for (CarImage img : newImages) {
                addImage(img);
            }
        }
    }

    public void addImage(CarImage image) {
        if (image == null) return;
        image.setCar(this);
        this.images.add(image);
    }
}
