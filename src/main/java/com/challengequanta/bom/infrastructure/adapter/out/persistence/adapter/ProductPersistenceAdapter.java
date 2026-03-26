package com.challengequanta.bom.infrastructure.adapter.out.persistence.adapter;

import com.challengequanta.bom.domain.model.Product;
import com.challengequanta.bom.domain.port.out.ProductRepositoryPort;
import com.challengequanta.bom.infrastructure.adapter.out.persistence.mapper.ProductPersistenceMapper;
import com.challengequanta.bom.infrastructure.adapter.out.persistence.repository.ProductR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductPersistenceAdapter implements ProductRepositoryPort {

    private final ProductR2dbcRepository productR2dbcRepository;
    private final ProductPersistenceMapper productPersistenceMapper;

    @Override
    public Mono<Product> save(Product product) {
        return productR2dbcRepository.save(productPersistenceMapper.toEntity(product))
                .map(productPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Product> findById(Long id) {
        return productR2dbcRepository.findById(id)
                .map(productPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Product> findByNameIgnoreCase(String name) {
        return productR2dbcRepository.findByNameIgnoreCase(name)
                .map(productPersistenceMapper::toDomain);
    }
}
