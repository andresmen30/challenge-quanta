package com.challengequanta.bom.infrastructure.adapter.out.persistence.repository;

import com.challengequanta.bom.infrastructure.adapter.out.persistence.entity.ProductEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ProductR2dbcRepository extends ReactiveCrudRepository<ProductEntity, Long> {
}
