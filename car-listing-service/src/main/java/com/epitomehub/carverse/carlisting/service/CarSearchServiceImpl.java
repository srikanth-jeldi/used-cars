package com.epitomehub.carverse.carlisting.service;
import com.epitomehub.carverse.carlisting.dto.CarSearchRequest;
import com.epitomehub.carverse.carlisting.dto.PageResponse;
import com.epitomehub.carverse.carlisting.entity.Car;
import com.epitomehub.carverse.carlisting.repository.CarRepository;
import com.epitomehub.carverse.carlisting.specification.CarSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import com.epitomehub.carverse.carlisting.dto.CarResponseDto;
import com.epitomehub.carverse.carlisting.mapper.CarMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarSearchServiceImpl implements CarSearchService {

    private final CarRepository carRepository;

    @Override
    public PageResponse<CarResponseDto> search(CarSearchRequest request) {

        int page = request.getPage() != null && request.getPage() >= 0 ? request.getPage() : 0;
        int size = request.getSize() != null && request.getSize() > 0 ? request.getSize() : 12;

        String sortBy = (request.getSortBy() == null || request.getSortBy().isBlank()) ? "createdAt" : request.getSortBy();
        Sort.Direction direction = "asc".equalsIgnoreCase(request.getSortDir()) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Car> result = carRepository.findAll(CarSpecification.build(request), pageable);

        List<CarResponseDto> content = result.getContent()
                .stream()
                .map(CarMapper::toDto)
                .toList();

        return PageResponse.<CarResponseDto>builder()
                .content(content)
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }

}