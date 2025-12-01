package com.epitomehub.carverse.carservice.repository;

import com.epitomehub.carverse.carservice.entity.Car;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class CarSpecifications {

    public static Specification<Car> withFilters(
            String brand,
            String model,
            String city,
            String state,
            String fuelType,
            Double minPrice,
            Double maxPrice,
            Integer minYear,
            Integer maxYear,
            Integer minKm,
            Integer maxKm
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (brand != null && !brand.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("brand")), brand.toLowerCase()));
            }

            if (model != null && !model.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("model")), model.toLowerCase()));
            }

            if (city != null && !city.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("city")), city.toLowerCase()));
            }

            if (state != null && !state.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("state")), state.toLowerCase()));
            }

            if (fuelType != null && !fuelType.isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("fuelType")), fuelType.toLowerCase()));
            }

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            if (minYear != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("year"), minYear));
            }

            if (maxYear != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("year"), maxYear));
            }

            if (minKm != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("kmDriven"), minKm));
            }

            if (maxKm != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("kmDriven"), maxKm));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
