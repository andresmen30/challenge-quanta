package com.challengequanta.bom.application.service;

import com.challengequanta.bom.domain.model.Product;
import com.challengequanta.bom.domain.model.ProductMaterial;
import com.challengequanta.bom.domain.port.in.command.AddMaterialCommand;
import com.challengequanta.bom.domain.port.in.command.CreateProductCommand;
import com.challengequanta.bom.domain.port.out.ProductMaterialRepositoryPort;
import com.challengequanta.bom.domain.port.out.ProductRepositoryPort;
import com.challengequanta.bom.shared.exception.BadRequestException;
import com.challengequanta.bom.shared.exception.ConflictException;
import com.challengequanta.bom.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BomServiceTest {

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @Mock
    private ProductMaterialRepositoryPort productMaterialRepositoryPort;

    @InjectMocks
    private BomService bomService;

    @Test
    void shouldCreateProductSuccessfully() {
        when(productRepositoryPort.findByNameIgnoreCase("Zapato")).thenReturn(Mono.empty());
        when(productRepositoryPort.save(any(Product.class)))
                .thenReturn(Mono.just(new Product(1L, "Zapato")));

        StepVerifier.create(bomService.createProduct(new CreateProductCommand("Zapato")))
                .expectNextMatches(product -> product.id().equals(1L) && product.name().equals("Zapato"))
                .verifyComplete();

        verify(productRepositoryPort).save(any(Product.class));
    }

    @Test
    void shouldFailWhenCreatingDuplicateProductName() {
        when(productRepositoryPort.findByNameIgnoreCase("Zapato"))
                .thenReturn(Mono.just(new Product(99L, "Zapato")));

        StepVerifier.create(bomService.createProduct(new CreateProductCommand("  Zapato  ")))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(ConflictException.class);
                    assertThat(error.getMessage()).isEqualTo("Product with name 'Zapato' already exists");
                })
                .verify();
    }

    @Test
    void shouldFailWhenCreatingProductWithoutName() {
        StepVerifier.create(Mono.defer(() -> bomService.createProduct(new CreateProductCommand("   "))))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(BadRequestException.class);
                    assertThat(error.getMessage()).isEqualTo("Product name is required");
                })
                .verify();
    }

    @Test
    void shouldAddMaterialSuccessfully() {
        Long productId = 10L;
        AddMaterialCommand command = new AddMaterialCommand("Cuero", 2);

        when(productRepositoryPort.findById(productId)).thenReturn(Mono.just(new Product(productId, "Zapato")));
        when(productMaterialRepositoryPort.findByProductIdAndMaterialIgnoreCase(productId, "Cuero"))
                .thenReturn(Mono.empty());
        when(productMaterialRepositoryPort.save(any(ProductMaterial.class)))
                .thenReturn(Mono.just(new ProductMaterial(100L, productId, "Cuero", 2)));

        StepVerifier.create(bomService.addMaterial(productId, command))
                .expectNextMatches(material -> material.id().equals(100L)
                        && material.productId().equals(productId)
                        && material.material().equals("Cuero")
                        && material.quantity().equals(2))
                .verifyComplete();

        verify(productRepositoryPort).findById(productId);
        verify(productMaterialRepositoryPort).save(any(ProductMaterial.class));
    }

    @Test
    void shouldFailWhenAddingMaterialToMissingProduct() {
        Long productId = 99L;
        AddMaterialCommand command = new AddMaterialCommand("Cuero", 2);

        when(productRepositoryPort.findById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(bomService.addMaterial(productId, command))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(NotFoundException.class);
                    assertThat(error.getMessage()).isEqualTo("Product with id 99 was not found");
                })
                .verify();
    }

    @Test
    void shouldFailWhenAddingDuplicateMaterialToProduct() {
        Long productId = 10L;
        AddMaterialCommand command = new AddMaterialCommand("Cuero", 2);

        when(productRepositoryPort.findById(productId)).thenReturn(Mono.just(new Product(productId, "Zapato")));
        when(productMaterialRepositoryPort.findByProductIdAndMaterialIgnoreCase(productId, "Cuero"))
                .thenReturn(Mono.just(new ProductMaterial(501L, productId, "Cuero", 1)));

        StepVerifier.create(bomService.addMaterial(productId, command))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(ConflictException.class);
                    assertThat(error.getMessage()).isEqualTo("Material 'Cuero' already exists for product with id 10");
                })
                .verify();
    }

    @Test
    void shouldCalculateProductionSuccessfully() {
        Long productId = 1L;

        when(productRepositoryPort.findById(productId)).thenReturn(Mono.just(new Product(productId, "Zapato")));
        when(productMaterialRepositoryPort.findByProductId(productId)).thenReturn(Flux.just(
                new ProductMaterial(1L, productId, "Cuero", 2),
                new ProductMaterial(2L, productId, "Suela", 1),
                new ProductMaterial(3L, productId, "Cordones", 1)
        ));

        StepVerifier.create(bomService.calculateProduction(productId, 100))
                .expectNextMatches(result -> result.product().equals("Zapato")
                        && result.quantity().equals(100)
                        && result.materials().size() == 3
                        && result.materials().get(0).material().equals("Cuero")
                        && result.materials().get(0).required().equals(200)
                        && result.materials().get(1).material().equals("Suela")
                        && result.materials().get(1).required().equals(100)
                        && result.materials().get(2).material().equals("Cordones")
                        && result.materials().get(2).required().equals(100))
                .verifyComplete();
    }

    @Test
    void shouldMergeDuplicatedMaterialsWhenCalculatingProduction() {
        Long productId = 1L;

        when(productRepositoryPort.findById(productId)).thenReturn(Mono.just(new Product(productId, "Zapato")));
        when(productMaterialRepositoryPort.findByProductId(productId)).thenReturn(Flux.just(
                new ProductMaterial(1L, productId, "Cuero", 2),
                new ProductMaterial(2L, productId, "cuero", 1),
                new ProductMaterial(3L, productId, "Suela", 1)
        ));

        StepVerifier.create(bomService.calculateProduction(productId, 100))
                .expectNextMatches(result -> result.product().equals("Zapato")
                        && result.quantity().equals(100)
                        && result.materials().size() == 2
                        && result.materials().get(0).material().equals("Cuero")
                        && result.materials().get(0).required().equals(300)
                        && result.materials().get(1).material().equals("Suela")
                        && result.materials().get(1).required().equals(100))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenCalculatingWithInvalidQuantity() {
        StepVerifier.create(Mono.defer(() -> bomService.calculateProduction(1L, 0)))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(BadRequestException.class);
                    assertThat(error.getMessage()).isEqualTo("quantity must be greater than 0");
                })
                .verify();
    }

    @Test
    void shouldFailWhenCalculatingForMissingProduct() {
        when(productRepositoryPort.findById(404L)).thenReturn(Mono.empty());

        StepVerifier.create(bomService.calculateProduction(404L, 10))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(NotFoundException.class);
                    assertThat(error.getMessage()).isEqualTo("Product with id 404 was not found");
                })
                .verify();
    }

    @Test
    void shouldDeleteProductSuccessfully() {
        Long productId = 1L;

        when(productRepositoryPort.findById(productId)).thenReturn(Mono.just(new Product(productId, "Zapato")));
        when(productMaterialRepositoryPort.deleteByProductId(productId)).thenReturn(Mono.empty());
        when(productRepositoryPort.deleteById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(bomService.deleteProduct(productId))
                .verifyComplete();

        verify(productMaterialRepositoryPort).deleteByProductId(productId);
        verify(productRepositoryPort).deleteById(productId);
    }

    @Test
    void shouldFailWhenDeletingMissingProduct() {
        when(productRepositoryPort.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(bomService.deleteProduct(99L))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(NotFoundException.class);
                    assertThat(error.getMessage()).isEqualTo("Product with id 99 was not found");
                })
                .verify();
    }

    @Test
    void shouldFailWhenDeletingWithInvalidProductId() {
        StepVerifier.create(Mono.defer(() -> bomService.deleteProduct(0L)))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(BadRequestException.class);
                    assertThat(error.getMessage()).isEqualTo("productId must be greater than 0");
                })
                .verify();
    }
}
