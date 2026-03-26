package com.challengequanta.bom.application.service;

import com.challengequanta.bom.domain.model.Product;
import com.challengequanta.bom.domain.model.ProductMaterial;
import com.challengequanta.bom.domain.model.ProductionResult;
import com.challengequanta.bom.domain.model.RequiredMaterial;
import com.challengequanta.bom.domain.port.in.ProductUseCase;
import com.challengequanta.bom.domain.port.in.ProductionUseCase;
import com.challengequanta.bom.domain.port.in.command.AddMaterialCommand;
import com.challengequanta.bom.domain.port.in.command.CreateProductCommand;
import com.challengequanta.bom.domain.port.out.ProductMaterialRepositoryPort;
import com.challengequanta.bom.domain.port.out.ProductRepositoryPort;
import com.challengequanta.bom.shared.exception.BadRequestException;
import com.challengequanta.bom.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BomService implements ProductUseCase, ProductionUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final ProductMaterialRepositoryPort productMaterialRepositoryPort;

    @Override
    public Mono<Product> createProduct(final CreateProductCommand command) {
        validateProductName(command.name());
        Product product = new Product(null, command.name().trim());
        return productRepositoryPort.save(product);
    }

    @Override
    public Mono<ProductMaterial> addMaterial(final Long productId, final AddMaterialCommand command) {
        validateProductId(productId);
        validateMaterial(command.material());
        validatePositiveQuantity(command.quantity());

        return productRepositoryPort.findById(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("Product with id " + productId + " was not found")))
                .flatMap(product -> productMaterialRepositoryPort.save(
                        new ProductMaterial(null, productId, command.material().trim(), command.quantity())
                ));
    }

    @Override
    public Mono<ProductionResult> calculateProduction(final Long productId, final Integer quantity) {
        validateProductId(productId);
        validatePositiveQuantity(quantity);

        return productRepositoryPort.findById(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("Product with id " + productId + " was not found")))
                .flatMap(product -> productMaterialRepositoryPort.findByProductId(productId)
                        .map(productMaterial -> new RequiredMaterial(
                                productMaterial.material(),
                                multiply(productMaterial.quantity(), quantity)
                        ))
                        .collectList()
                        .map(materials -> new ProductionResult(product.name(), quantity, List.copyOf(materials))));
    }

    private Integer multiply(final Integer left, final Integer right) {
        try {
            return Math.multiplyExact(left, right);
        } catch (ArithmeticException ex) {
            throw new BadRequestException("Quantity multiplication overflow");
        }
    }

    private void validateProductName(final String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Product name is required");
        }
    }

    private void validateMaterial(final String material) {
        if (material == null || material.isBlank()) {
            throw new BadRequestException("Material is required");
        }
    }

    private void validateProductId(final Long productId) {
        if (productId == null || productId <= 0) {
            throw new BadRequestException("productId must be greater than 0");
        }
    }

    private void validatePositiveQuantity(final Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("quantity must be greater than 0");
        }
    }
}
