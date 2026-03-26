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
import com.challengequanta.bom.shared.exception.ConflictException;
import com.challengequanta.bom.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BomService implements ProductUseCase, ProductionUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final ProductMaterialRepositoryPort productMaterialRepositoryPort;

    @Override
    public Mono<Product> createProduct(final CreateProductCommand command) {
        validateProductName(command.name());
        String normalizedName = command.name().trim();

        return productRepositoryPort.findByNameIgnoreCase(normalizedName)
                .flatMap(existing -> Mono.<Product>error(new ConflictException(
                        "Product with name '" + normalizedName + "' already exists")))
                .switchIfEmpty(Mono.defer(() -> productRepositoryPort.save(new Product(null, normalizedName))));
    }

    @Override
    public Mono<ProductMaterial> addMaterial(final Long productId, final AddMaterialCommand command) {
        validateProductId(productId);
        validateMaterial(command.material());
        validatePositiveQuantity(command.quantity());
        String normalizedMaterial = command.material().trim();

        return productRepositoryPort.findById(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("Product with id " + productId + " was not found")))
                .flatMap(product -> productMaterialRepositoryPort.findByProductIdAndMaterialIgnoreCase(
                                productId, normalizedMaterial)
                        .flatMap(existing -> Mono.<ProductMaterial>error(new ConflictException(
                                "Material '" + normalizedMaterial + "' already exists for product with id " + productId)))
                        .switchIfEmpty(Mono.defer(() -> productMaterialRepositoryPort.save(
                                new ProductMaterial(null, productId, normalizedMaterial, command.quantity())
                        ))));
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
                        .map(this::mergeRequiredMaterials)
                        .map(materials -> new ProductionResult(product.name(), quantity, List.copyOf(materials))));
    }

    private Integer multiply(final Integer left, final Integer right) {
        try {
            return Math.multiplyExact(left, right);
        } catch (ArithmeticException ex) {
            throw new BadRequestException("Quantity multiplication overflow");
        }
    }

    private Integer addExact(final Integer left, final Integer right) {
        try {
            return Math.addExact(left, right);
        } catch (ArithmeticException ex) {
            throw new BadRequestException("Quantity aggregation overflow");
        }
    }

    private List<RequiredMaterial> mergeRequiredMaterials(final List<RequiredMaterial> materials) {
        Map<String, RequiredMaterial> mergedByMaterial = new LinkedHashMap<>();

        for (RequiredMaterial material : materials) {
            String normalizedName = material.material().trim().toLowerCase(Locale.ROOT);
            RequiredMaterial existing = mergedByMaterial.computeIfAbsent(
                    normalizedName,
                    ignored -> new RequiredMaterial(material.material().trim(), 0)
            );

            mergedByMaterial.put(normalizedName, new RequiredMaterial(
                    existing.material(),
                    addExact(existing.required(), material.required())
            ));
        }

        return List.copyOf(mergedByMaterial.values());
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
