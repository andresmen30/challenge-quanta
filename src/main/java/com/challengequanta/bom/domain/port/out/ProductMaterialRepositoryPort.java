package com.challengequanta.bom.domain.port.out;

import com.challengequanta.bom.domain.model.ProductMaterial;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductMaterialRepositoryPort {

    Mono<ProductMaterial> save(ProductMaterial productMaterial);

    Flux<ProductMaterial> findByProductId(Long productId);

    Mono<ProductMaterial> findByProductIdAndMaterialIgnoreCase(Long productId, String material);
}
