package com.challengequanta.bom.domain.port.in;

import com.challengequanta.bom.domain.model.ProductionResult;
import reactor.core.publisher.Mono;

public interface ProductionUseCase {

    Mono<ProductionResult> calculateProduction(Long productId, Integer quantity);
}
