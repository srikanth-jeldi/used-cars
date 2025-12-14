package com.epitomehub.carverse.carlisting.repository;

import com.epitomehub.carverse.carlisting.entity.Car;
import org.springframework.data.jpa.domain.Specification;

public final class CarSpecifications {

    private CarSpecifications() {}

    public static Specification<Car> withFilters(
            String brand,
            String model,
            String fuelType,
            String transmission,
            String city,
            Integer minYear,
            Integer maxYear,
            Double minPrice,
            Double maxPrice
    ) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (brand != null && !brand.isBlank()) {
                predicates = cb.and(predicates, cb.equal(cb.lower(root.get("brand")), brand.toLowerCase()));
            }
            if (model != null && !model.isBlank()) {
                predicates = cb.and(predicates, cb.equal(cb.lower(root.get("model")), model.toLowerCase()));
            }
            if (fuelType != null && !fuelType.isBlank()) {
                predicates = cb.and(predicates, cb.equal(cb.lower(root.get("fuelType")), fuelType.toLowerCase()));
            }
            if (transmission != null && !transmission.isBlank()) {
                predicates = cb.and(predicates, cb.equal(cb.lower(root.get("transmission")), transmission.toLowerCase()));
            }
            if (city != null && !city.isBlank()) {
                predicates = cb.and(predicates, cb.equal(cb.lower(root.get("city")), city.toLowerCase()));
            }
            if (minYear != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("year"), minYear));
            }
            if (maxYear != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("year"), maxYear));
            }
            if (minPrice != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            return predicates;
        };
    }
}
