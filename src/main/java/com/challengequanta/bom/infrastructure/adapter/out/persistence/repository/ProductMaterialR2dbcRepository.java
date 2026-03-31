package com.challengequanta.bom.infrastructure.adapter.out.persistence.repository;

import com.challengequanta.bom.infrastructure.adapter.out.persistence.entity.ProductMaterialEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductMaterialR2dbcRepository extends ReactiveCrudRepository<ProductMaterialEntity, Long> {

    Flux<ProductMaterialEntity> findByProductIdOrderByIdAsc(Long productId);

    Mono<ProductMaterialEntity> findByProductIdAndMaterialIgnoreCase(Long productId, String material);

    Mono<Void> deleteByProductId(Long productId);
}
