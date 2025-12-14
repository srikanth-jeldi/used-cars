package com.epitomehub.carverse.carlisting.service;

import com.epitomehub.carverse.carlisting.dto.CarResponseDto;
import com.epitomehub.carverse.carlisting.dto.CarSearchRequest;
import com.epitomehub.carverse.carlisting.dto.PageResponse;

public interface CarSearchService {
    PageResponse<CarResponseDto> search(CarSearchRequest request);
}