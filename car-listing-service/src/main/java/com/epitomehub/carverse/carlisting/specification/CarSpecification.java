package com.epitomehub.carverse.carlisting.specification;

import com.epitomehub.carverse.carlisting.dto.CarSearchRequest;
import com.epitomehub.carverse.carlisting.entity.Car;
import com.epitomehub.carverse.carlisting.entity.CarStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CarSpecification {

    private CarSpecification() {}

    // Backward-compatible alias
    public static Specification<Car> build(CarSearchRequest req) {
        return withFilters(req);
    }

    /**
     * PUBLIC SEARCH => ONLY PUBLISHED
     */
    public static Specification<Car> withFilters(CarSearchRequest req) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("status"), CarStatus.PUBLISHED));

            addCommonPredicates(req, root, cb, predicates);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * MY LISTINGS SEARCH => ownerId enforced, status optional (null => all)
     */
    public static Specification<Car> withMyFilters(CarSearchRequest req, Long ownerId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("ownerId"), ownerId));

            if (StringUtils.hasText(req.getStatus())) {
                CarStatus st = CarStatus.valueOf(req.getStatus().trim().toUpperCase());
                predicates.add(cb.equal(root.get("status"), st));
            }

            addCommonPredicates(req, root, cb, predicates);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addCommonPredicates(
            CarSearchRequest req,
            jakarta.persistence.criteria.Root<Car> root,
            jakarta.persistence.criteria.CriteriaBuilder cb,
            List<Predicate> predicates
    ) {
        if (StringUtils.hasText(req.getBrand())) {
            predicates.add(cb.equal(cb.lower(root.get("brand")), req.getBrand().trim().toLowerCase()));
        }

        if (StringUtils.hasText(req.getModel())) {
            predicates.add(cb.equal(cb.lower(root.get("model")), req.getModel().trim().toLowerCase()));
        }

        if (StringUtils.hasText(req.getVariant())) {
            predicates.add(cb.equal(cb.lower(root.get("variant")), req.getVariant().trim().toLowerCase()));
        }

        if (StringUtils.hasText(req.getFuelType())) {
            predicates.add(cb.equal(cb.lower(root.get("fuelType")), req.getFuelType().trim().toLowerCase()));
        }

        if (StringUtils.hasText(req.getTransmission())) {
            predicates.add(cb.equal(cb.lower(root.get("transmission")), req.getTransmission().trim().toLowerCase()));
        }

        if (StringUtils.hasText(req.getCity())) {
            predicates.add(cb.equal(cb.lower(root.get("city")), req.getCity().trim().toLowerCase()));
        }

        if (req.getMinYear() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("year"), req.getMinYear()));
        }
        if (req.getMaxYear() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("year"), req.getMaxYear()));
        }

        if (req.getMinPrice() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("price"), req.getMinPrice()));
        }
        if (req.getMaxPrice() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("price"), req.getMaxPrice()));
        }

        if (req.getMinKmDriven() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("kmsDriven"), req.getMinKmDriven()));
        }
        if (req.getMaxKmDriven() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("kmsDriven"), req.getMaxKmDriven()));
        }
    }
}
