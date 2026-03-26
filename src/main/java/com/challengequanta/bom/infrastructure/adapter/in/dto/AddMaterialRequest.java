package com.challengequanta.bom.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddMaterialRequest(
        @NotBlank(message = "Material is required")
        String material,

        @NotNull(message = "Quantity is required")
        @Positive(message = "quantity must be greater than 0")
        Integer quantity
) {
}
