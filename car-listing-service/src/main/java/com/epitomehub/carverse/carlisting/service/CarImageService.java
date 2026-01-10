package com.epitomehub.carverse.carlisting.service;

import com.epitomehub.carverse.carlisting.dto.AddCarImageRequest;
import com.epitomehub.carverse.carlisting.dto.CarImageResponse;

import java.util.List;

public interface CarImageService {

    CarImageResponse addImage(Long ownerId, Long carId, AddCarImageRequest req);

    void deleteImage(Long ownerId, Long carId, Long imageId);

    List<CarImageResponse> listImages(Long carId);
}
