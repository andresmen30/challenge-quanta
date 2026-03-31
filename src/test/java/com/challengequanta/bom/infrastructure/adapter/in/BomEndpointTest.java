package com.challengequanta.bom.infrastructure.adapter.in;

import com.challengequanta.bom.infrastructure.adapter.out.persistence.entity.ProductEntity;
import com.challengequanta.bom.infrastructure.adapter.out.persistence.entity.ProductMaterialEntity;
import com.challengequanta.bom.infrastructure.adapter.out.persistence.repository.ProductMaterialR2dbcRepository;
import com.challengequanta.bom.infrastructure.adapter.out.persistence.repository.ProductR2dbcRepository;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class BomEndpointTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductR2dbcRepository productRepository;

    @Autowired
    private ProductMaterialR2dbcRepository materialRepository;

    @BeforeEach
    void setUp() {
        materialRepository.deleteAll()
                .then(productRepository.deleteAll())
                .block();
    }

    @Test
    void shouldCreateProductSuccessfully() {
        webTestClient.post()
                .uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "name": "Zapato"
                        }
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNumber()
                .jsonPath("$.name").isEqualTo("Zapato");
    }

    @Test
    void shouldReturnBadRequestWhenCreatingProductWithoutName() {
        webTestClient.post()
                .uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "name": ""
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("Product name is required")
                .jsonPath("$.timestamp").exists();
    }

    @Test
    void shouldReturnConflictWhenCreatingDuplicatedProductName() {
        webTestClient.post()
                .uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "name": "Zapato"
                        }
                        """)
                .exchange()
                .expectStatus().isCreated();

        webTestClient.post()
                .uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "name": "Zapato"
                        }
                        """)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.message").isEqualTo("Product with name 'Zapato' already exists")
                .jsonPath("$.timestamp").exists();
    }

    @Test
    void shouldAddMaterialSuccessfully() {
        Long productId = createProduct("Zapato");

        webTestClient.post()
                .uri("/products/{productId}/materials", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "material": "Cuero",
                          "quantity": 2
                        }
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isNumber()
                .jsonPath("$.productId").isEqualTo(productId.intValue())
                .jsonPath("$.material").isEqualTo("Cuero")
                .jsonPath("$.quantity").isEqualTo(2);
    }

    @Test
    void shouldReturnNotFoundWhenAddingMaterialToMissingProduct() {
        webTestClient.post()
                .uri("/products/{productId}/materials", 999)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "material": "Cuero",
                          "quantity": 2
                        }
                        """)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("Product with id 999 was not found")
                .jsonPath("$.timestamp").exists();
    }

    @Test
    void shouldReturnConflictWhenAddingDuplicatedMaterialToProduct() {
        Long productId = createProduct("Zapato");

        webTestClient.post()
                .uri("/products/{productId}/materials", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "material": "Cuero",
                          "quantity": 2
                        }
                        """)
                .exchange()
                .expectStatus().isCreated();

        webTestClient.post()
                .uri("/products/{productId}/materials", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "material": "Cuero",
                          "quantity": 3
                        }
                        """)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.message").isEqualTo("Material 'Cuero' already exists for product with id " + productId)
                .jsonPath("$.timestamp").exists();
    }

    @Test
    void shouldCalculateProductionSuccessfully() {
        Long productId = createProduct("Zapato");
        materialRepository.saveAll(Flux.just(
                new ProductMaterialEntity(null, productId, "Cuero", 2),
                new ProductMaterialEntity(null, productId, "Suela", 1),
                new ProductMaterialEntity(null, productId, "Cordones", 1)
        )).collectList().block();

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/production/calculate")
                        .queryParam("productId", productId)
                        .queryParam("quantity", 100)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.product").isEqualTo("Zapato")
                .jsonPath("$.quantity").isEqualTo(100)
                .jsonPath("$.materials.length()").isEqualTo(3)
                .jsonPath("$.materials[0].material").isEqualTo("Cuero")
                .jsonPath("$.materials[0].required").isEqualTo(200)
                .jsonPath("$.materials[1].material").isEqualTo("Suela")
                .jsonPath("$.materials[1].required").isEqualTo(100)
                .jsonPath("$.materials[2].material").isEqualTo("Cordones")
                .jsonPath("$.materials[2].required").isEqualTo(100);
    }

    @Test
    void shouldReturnBadRequestWhenCalculatingWithInvalidQuantity() {
        Long productId = createProduct("Zapato");

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/production/calculate")
                        .queryParam("productId", productId)
                        .queryParam("quantity", 0)
                        .build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("quantity must be greater than 0")
                .jsonPath("$.timestamp").exists();
    }

    @Test
    void shouldReturnNotFoundWhenProductDoesNotExistForCalculation() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/production/calculate")
                        .queryParam("productId", 404)
                        .queryParam("quantity", 10)
                        .build())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("Product with id 404 was not found")
                .jsonPath("$.timestamp").exists();
    }

    @Test
    void shouldDeleteProductSuccessfully() {
        Long productId = createProduct("Zapato");

        webTestClient.delete()
                .uri("/products/{productId}", productId)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldDeleteProductAndItsMaterials() {
        Long productId = createProduct("Zapato");
        materialRepository.saveAll(Flux.just(
                new ProductMaterialEntity(null, productId, "Cuero", 2),
                new ProductMaterialEntity(null, productId, "Suela", 1)
        )).collectList().block();

        webTestClient.delete()
                .uri("/products/{productId}", productId)
                .exchange()
                .expectStatus().isNoContent();

        Long materialsCount = materialRepository.findByProductIdOrderByIdAsc(productId).count().block();
        assertThat(materialsCount).isZero();
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingProduct() {
        webTestClient.delete()
                .uri("/products/{productId}", 999)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("Product with id 999 was not found")
                .jsonPath("$.timestamp").exists();
    }

    @Test
    void shouldReturnBadRequestWhenDeletingWithInvalidProductId() {
        webTestClient.delete()
                .uri("/products/{productId}", 0)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("productId must be greater than 0")
                .jsonPath("$.timestamp").exists();
    }

    @Test
    void shouldExposeOpenApiDocumentation() {
        webTestClient.get()
                .uri("/v3/api-docs")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.openapi").exists()
                .jsonPath("$.paths['/products']").exists()
                .jsonPath("$.paths['/products/{productId}']").exists()
                .jsonPath("$.paths['/products/{productId}/materials']").exists()
                .jsonPath("$.paths['/production/calculate']").exists();
    }

    private Long createProduct(String name) {
        return productRepository.save(new ProductEntity(null, name))
                .map(ProductEntity::getId)
                .block();
    }
}
