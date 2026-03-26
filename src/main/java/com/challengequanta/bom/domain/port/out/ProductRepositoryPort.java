package com.challengequanta.bom.domain.port.out;

import com.challengequanta.bom.domain.model.Product;
import reactor.core.publisher.Mono;

public interface ProductRepositoryPort {

    Mono<Product> save(Product product);

    Mono<Product> findById(Long id);
}
