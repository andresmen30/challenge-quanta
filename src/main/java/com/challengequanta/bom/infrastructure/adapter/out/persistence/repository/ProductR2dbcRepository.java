package com.challengequanta.bom.infrastructure.adapter.out.persistence.repository;

import com.challengequanta.bom.infrastructure.adapter.out.persistence.entity.ProductEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ProductR2dbcRepository extends ReactiveCrudRepository<ProductEntity, Long> {

    Mono<ProductEntity> findByNameIgnoreCase(String name);
}
