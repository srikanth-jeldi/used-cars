package com.epitomehub.carverse.carlisting.service;

import com.epitomehub.carverse.carlisting.dto.AddCarImageRequest;
import com.epitomehub.carverse.carlisting.dto.CarImageResponse;
import com.epitomehub.carverse.carlisting.entity.Car;
import com.epitomehub.carverse.carlisting.entity.CarImage;
import com.epitomehub.carverse.carlisting.repository.CarImageRepository;
import com.epitomehub.carverse.carlisting.repository.CarRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarImageServiceImpl implements CarImageService {

    private static final int MAX_IMAGES = 30;

    private final CarRepository carRepository;
    private final CarImageRepository carImageRepository;

    @Override
    @Transactional
    public CarImageResponse addImage(Long ownerId, Long carId, AddCarImageRequest req) {

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found: " + carId));

        if (!ownerId.equals(car.getOwnerId())) {
            throw new AccessDeniedException("Forbidden");
        }

        long count = carImageRepository.countByCar_Id(carId);
        if (count >= MAX_IMAGES) {
            throw new IllegalStateException("Max images reached (" + MAX_IMAGES + ")");
        }

        boolean requestedPrimary = Boolean.TRUE.equals(req.primary());

        // RULE: if primary=false and there is no primary yet => auto set to primary
        if (!requestedPrimary) {
            boolean hasPrimary = carImageRepository.existsByCar_IdAndPrimaryTrue(carId);
            if (!hasPrimary) {
                requestedPrimary = true;
            }
        }

        // RULE: if saving primary=true => clear existing primary first
        if (requestedPrimary) {
            carImageRepository.clearPrimaryForCar(carId);
        }

        CarImage img = new CarImage();
        img.setCar(car);
        img.setUrl(req.url() == null ? null : req.url().trim());
        img.setCategory(req.category());
        img.setSortOrder(req.sortOrder() == null ? 0 : req.sortOrder());
        img.setPrimary(requestedPrimary);

        CarImage saved = carImageRepository.save(img);

        return new CarImageResponse(
                saved.getId(),
                saved.getUrl(),
                saved.getCategory(),
                saved.getSortOrder(),
                saved.isPrimary()
        );
    }

    @Override
    @Transactional
    public void deleteImage(Long ownerId, Long carId, Long imageId) {

        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found: " + carId));

        if (!ownerId.equals(car.getOwnerId())) {
            throw new AccessDeniedException("Forbidden");
        }

        // Enforce belongs-to-car in one query
        CarImage img = carImageRepository.findByIdAndCar_Id(imageId, carId)
                .orElseThrow(() -> new IllegalArgumentException("Image not found: " + imageId));

        boolean wasPrimary = img.isPrimary();

        carImageRepository.delete(img);

        // If primary deleted, ensure another primary exists
        if (wasPrimary) {
            ensureSomePrimaryExists(carId);
        }
    }

    @Override
    public List<CarImageResponse> listImages(Long carId) {
        return carImageRepository.findByCar_IdOrderByPrimaryDescSortOrderAscIdAsc(carId)
                .stream()
                .map(i -> new CarImageResponse(i.getId(), i.getUrl(), i.getCategory(), i.getSortOrder(), i.isPrimary()))
                .toList();
    }

    private void ensureSomePrimaryExists(Long carId) {
        if (carImageRepository.existsByCar_IdAndPrimaryTrue(carId)) return;

        carImageRepository.findTopByCar_IdOrderByPrimaryDescSortOrderAscIdAsc(carId)
                .ifPresent(img -> {
                    carImageRepository.clearPrimaryForCar(carId);
                    img.setPrimary(true);
                    carImageRepository.save(img);
                });
    }
}
