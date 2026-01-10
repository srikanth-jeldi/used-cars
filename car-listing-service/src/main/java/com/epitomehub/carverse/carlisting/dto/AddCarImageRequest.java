package com.epitomehub.carverse.carlisting.dto;

import com.epitomehub.carverse.carlisting.entity.CarImageCategory;
import com.epitomehub.carverse.carlisting.entity.CarImageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddCarImageRequest(

        @NotBlank String url,
        @NotNull CarImageCategory category,
        Integer sortOrder,
        Boolean primary

) {}
