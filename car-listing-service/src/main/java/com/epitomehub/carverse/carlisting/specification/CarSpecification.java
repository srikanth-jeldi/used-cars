package com.epitomehub.carverse.carlisting.specification;

import com.epitomehub.carverse.carlisting.dto.CarSearchRequest;
import com.epitomehub.carverse.carlisting.entity.Car;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CarSpecification {

    private CarSpecification() {}

    public static Specification<Car> build(CarSearchRequest req) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // keyword (title/brand/model/city/state)
            if (StringUtils.hasText(req.getKeyword())) {
                String like = "%" + req.getKeyword().trim().toLowerCase() + "%";
                Predicate p1 = cb.like(cb.lower(root.get("title")), like);
                Predicate p2 = cb.like(cb.lower(root.get("brand")), like);
                Predicate p3 = cb.like(cb.lower(root.get("model")), like);
                Predicate p4 = cb.like(cb.lower(root.get("city")), like);
                Predicate p5 = cb.like(cb.lower(root.get("state")), like);
                predicates.add(cb.or(p1, p2, p3, p4, p5));
            }

            // brand
            if (StringUtils.hasText(req.getBrand())) {
                predicates.add(cb.like(cb.lower(root.get("brand")), "%" + req.getBrand().trim().toLowerCase() + "%"));
            }

            // model
            if (StringUtils.hasText(req.getModel())) {
                predicates.add(cb.like(cb.lower(root.get("model")), "%" + req.getModel().trim().toLowerCase() + "%"));
            }

            // fuelType (exact match is usually better)
            if (StringUtils.hasText(req.getFuelType())) {
                predicates.add(cb.equal(cb.lower(root.get("fuelType")), req.getFuelType().trim().toLowerCase()));
            }

            // city/state
            if (StringUtils.hasText(req.getCity())) {
                predicates.add(cb.like(cb.lower(root.get("city")), "%" + req.getCity().trim().toLowerCase() + "%"));
            }
            if (StringUtils.hasText(req.getState())) {
                predicates.add(cb.like(cb.lower(root.get("state")), "%" + req.getState().trim().toLowerCase() + "%"));
            }

            // year range
            if (req.getMinYear() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("year"), req.getMinYear()));
            }
            if (req.getMaxYear() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("year"), req.getMaxYear()));
            }

            // price range
            if (req.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), req.getMinPrice()));
            }
            if (req.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), req.getMaxPrice()));
            }

            // kmDriven range
            if (req.getMinKmDriven() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("kmDriven"), req.getMinKmDriven()));
            }
            if (req.getMaxKmDriven() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("kmDriven"), req.getMaxKmDriven()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}