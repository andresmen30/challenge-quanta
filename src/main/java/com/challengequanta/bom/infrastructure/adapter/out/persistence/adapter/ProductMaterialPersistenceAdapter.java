package com.challengequanta.bom.infrastructure.adapter.out.persistence.adapter;

import com.challengequanta.bom.domain.model.ProductMaterial;
import com.challengequanta.bom.domain.port.out.ProductMaterialRepositoryPort;
import com.challengequanta.bom.infrastructure.adapter.out.persistence.mapper.ProductMaterialPersistenceMapper;
import com.challengequanta.bom.infrastructure.adapter.out.persistence.repository.ProductMaterialR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductMaterialPersistenceAdapter implements ProductMaterialRepositoryPort {

    private final ProductMaterialR2dbcRepository productMaterialR2dbcRepository;
    private final ProductMaterialPersistenceMapper productMaterialPersistenceMapper;

    @Override
    public Mono<ProductMaterial> save(ProductMaterial productMaterial) {
        return productMaterialR2dbcRepository.save(productMaterialPersistenceMapper.toEntity(productMaterial))
                .map(productMaterialPersistenceMapper::toDomain);
    }

    @Override
    public Flux<ProductMaterial> findByProductId(Long productId) {
        return productMaterialR2dbcRepository.findByProductIdOrderByIdAsc(productId)
                .map(productMaterialPersistenceMapper::toDomain);
    }

    @Override
    public Mono<ProductMaterial> findByProductIdAndMaterialIgnoreCase(Long productId, String material) {
        return productMaterialR2dbcRepository.findByProductIdAndMaterialIgnoreCase(productId, material)
                .map(productMaterialPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteByProductId(Long productId) {
        return productMaterialR2dbcRepository.deleteByProductId(productId);
    }
}
