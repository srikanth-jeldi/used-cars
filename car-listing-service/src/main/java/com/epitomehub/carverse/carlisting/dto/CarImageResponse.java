package com.epitomehub.carverse.carlisting.dto;

import com.epitomehub.carverse.carlisting.entity.CarImageCategory;

public record CarImageResponse(
        Long id,
        String url,
        CarImageCategory category,
        Integer sortOrder,
        boolean primary
) {}
