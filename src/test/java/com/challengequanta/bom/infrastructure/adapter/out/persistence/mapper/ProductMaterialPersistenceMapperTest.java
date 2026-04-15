package com.challengequanta.bom.infrastructure.adapter.out.persistence.mapper;

import com.challengequanta.bom.domain.model.ProductMaterial;
import com.challengequanta.bom.infrastructure.adapter.out.persistence.entity.ProductMaterialEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMaterialPersistenceMapperTest {

    private ProductMaterialPersistenceMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductMaterialPersistenceMapperImpl();
    }

    @Test
    void shouldMapDomainToEntity() {
        ProductMaterial productMaterial = new ProductMaterial(10L, 1L, "Cuero", 2);

        ProductMaterialEntity entity = mapper.toEntity(productMaterial);

        assertThat(entity.getId()).isEqualTo(10L);
        assertThat(entity.getProductId()).isEqualTo(1L);
        assertThat(entity.getMaterial()).isEqualTo("Cuero");
        assertThat(entity.getQuantity()).isEqualTo(2);
    }

    @Test
    void shouldMapDomainWithNullIdToEntity() {
        ProductMaterial productMaterial = new ProductMaterial(null, 1L, "Cuero", 2);

        ProductMaterialEntity entity = mapper.toEntity(productMaterial);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getProductId()).isEqualTo(1L);
    }

    @Test
    void shouldReturnNullWhenMappingNullDomain() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void shouldMapEntityToDomain() {
        ProductMaterialEntity entity = new ProductMaterialEntity(10L, 1L, "Cuero", 2);

        ProductMaterial productMaterial = mapper.toDomain(entity);

        assertThat(productMaterial).isEqualTo(new ProductMaterial(10L, 1L, "Cuero", 2));
    }

    @Test
    void shouldReturnNullWhenMappingNullEntity() {
        assertThat(mapper.toDomain(null)).isNull();
    }
}
