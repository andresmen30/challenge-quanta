package com.challengequanta.bom.domain.model;

import java.util.List;

public record ProductionResult(String product, Integer quantity, List<RequiredMaterial> materials) {
}
