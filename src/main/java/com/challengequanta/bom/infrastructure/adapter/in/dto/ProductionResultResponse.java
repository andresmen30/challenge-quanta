package com.challengequanta.bom.infrastructure.adapter.in.dto;

import java.util.List;

public record ProductionResultResponse(String product, Integer quantity, List<RequiredMaterialResponse> materials) {
}
