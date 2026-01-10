package com.epitomehub.carverse.carlisting.controller;

import com.epitomehub.carverse.carlisting.dto.AddCarImageRequest;
import com.epitomehub.carverse.carlisting.dto.CarImageResponse;
import com.epitomehub.carverse.carlisting.service.CarImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cars/{carId}/images")
public class CarImageController {

    private final CarImageService carImageService;

    @PostMapping
    public CarImageResponse add(@PathVariable Long carId,
                                @Valid @RequestBody AddCarImageRequest req,
                                Authentication auth) {
        Long ownerId = (Long) auth.getPrincipal(); // keep as-is in your project
        return carImageService.addImage(ownerId, carId, req);
    }

    @GetMapping
    public List<CarImageResponse> list(@PathVariable Long carId) {
        return carImageService.listImages(carId);
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> delete(@PathVariable Long carId,
                                       @PathVariable Long imageId,
                                       Authentication auth) {
        Long ownerId = (Long) auth.getPrincipal(); // keep as-is in your project
        carImageService.deleteImage(ownerId, carId, imageId);
        return ResponseEntity.noContent().build();
    }
}
