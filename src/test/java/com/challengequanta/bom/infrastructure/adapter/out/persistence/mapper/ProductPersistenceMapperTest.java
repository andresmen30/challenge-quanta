package com.challengequanta.bom.infrastructure.adapter.out.persistence.mapper;

import com.challengequanta.bom.domain.model.Product;
import com.challengequanta.bom.infrastructure.adapter.out.persistence.entity.ProductEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductPersistenceMapperTest {

    private ProductPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductPersistenceMapperImpl();
    }

    @Test
    void shouldMapDomainToEntity() {
        Product product = new Product(1L, "Zapato");

        ProductEntity entity = mapper.toEntity(product);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getName()).isEqualTo("Zapato");
    }

    @Test
    void shouldMapDomainWithNullIdToEntity() {
        Product product = new Product(null, "Zapato");

        ProductEntity entity = mapper.toEntity(product);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo("Zapato");
    }

    @Test
    void shouldReturnNullWhenMappingNullDomain() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void shouldMapEntityToDomain() {
        ProductEntity entity = new ProductEntity(1L, "Zapato");

        Product product = mapper.toDomain(entity);

        assertThat(product).isEqualTo(new Product(1L, "Zapato"));
    }

    @Test
    void shouldReturnNullWhenMappingNullEntity() {
        assertThat(mapper.toDomain(null)).isNull();
    }
}
