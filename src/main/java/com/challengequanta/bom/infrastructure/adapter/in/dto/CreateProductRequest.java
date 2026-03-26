package com.challengequanta.bom.infrastructure.adapter.in.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateProductRequest(
        @NotBlank(message = "Product name is required")
        String name
) {
}
