package com.epitomehub.carverse.carlisting.service;

import com.epitomehub.carverse.carlisting.entity.CarImageCategory;
import com.epitomehub.carverse.carlisting.repository.CarImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@RequiredArgsConstructor
public class CarPublishValidator {

    private final CarImageRepository carImageRepository;

    public void validateOrThrow(Long carId) {

        long total = carImageRepository.countByCar_Id(carId);
        if (total < 5) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Publish failed: At least 5 images are required. Current=" + total
            );
        }

        if (carImageRepository.countByCar_IdAndCategory(carId, CarImageCategory.FRONT) < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Publish failed: At least 1 FRONT image is required");
        }

        if (carImageRepository.countByCar_IdAndCategory(carId, CarImageCategory.REAR) < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Publish failed: At least 1 REAR image is required");
        }

        if (carImageRepository.countByCar_IdAndCategory(carId, CarImageCategory.INTERIOR) < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Publish failed: At least 1 INTERIOR image is required");
        }

        long primaryCount = carImageRepository.countPrimaryByCarId(carId);
        if (primaryCount != 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Publish failed: Exactly 1 primary image is required. Current=" + primaryCount
            );
        }
    }
}
