package com.challengequanta.bom.domain.port.in;

import com.challengequanta.bom.domain.model.Product;
import com.challengequanta.bom.domain.model.ProductMaterial;
import com.challengequanta.bom.domain.port.in.command.AddMaterialCommand;
import com.challengequanta.bom.domain.port.in.command.CreateProductCommand;
import reactor.core.publisher.Mono;

public interface ProductUseCase {

    Mono<Product> createProduct(CreateProductCommand command);

    Mono<ProductMaterial> addMaterial(Long productId, AddMaterialCommand command);
}
