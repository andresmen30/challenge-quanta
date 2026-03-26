package com.challengequanta.bom.infrastructure.adapter.out.persistence.repository;

import com.challengequanta.bom.infrastructure.adapter.out.persistence.entity.ProductMaterialEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ProductMaterialR2dbcRepository extends ReactiveCrudRepository<ProductMaterialEntity, Long> {

    Flux<ProductMaterialEntity> findByProductIdOrderByIdAsc(Long productId);
}
