package com.challengequanta.bom.infrastructure.adapter.out.persistence.adapter;

import com.challengequanta.bom.domain.model.ProductMaterial;
import com.challengequanta.bom.infrastructure.adapter.out.persistence.entity.ProductMaterialEntity;
import com.challengequanta.bom.infrastructure.adapter.out.persistence.mapper.ProductMaterialPersistenceMapper;
import com.challengequanta.bom.infrastructure.adapter.out.persistence.repository.ProductMaterialR2dbcRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductMaterialPersistenceAdapterTest {

    @Mock
    private ProductMaterialR2dbcRepository productMaterialR2dbcRepository;

    @Mock
    private ProductMaterialPersistenceMapper productMaterialPersistenceMapper;

    @InjectMocks
    private ProductMaterialPersistenceAdapter adapter;

    @Test
    void shouldSaveProductMaterialAndReturnDomain() {
        ProductMaterial domain = new ProductMaterial(null, 1L, "Cuero", 2);
        ProductMaterialEntity entity = new ProductMaterialEntity(null, 1L, "Cuero", 2);
        ProductMaterialEntity savedEntity = new ProductMaterialEntity(10L, 1L, "Cuero", 2);
        ProductMaterial savedDomain = new ProductMaterial(10L, 1L, "Cuero", 2);

        when(productMaterialPersistenceMapper.toEntity(domain)).thenReturn(entity);
        when(productMaterialR2dbcRepository.save(entity)).thenReturn(Mono.just(savedEntity));
        when(productMaterialPersistenceMapper.toDomain(savedEntity)).thenReturn(savedDomain);

        StepVerifier.create(adapter.save(domain))
                .expectNext(savedDomain)
                .verifyComplete();

        verify(productMaterialR2dbcRepository).save(entity);
    }

    @Test
    void shouldFindByProductIdPreservingOrderAndMapEachToDomain() {
        ProductMaterialEntity entity1 = new ProductMaterialEntity(1L, 1L, "Cuero", 2);
        ProductMaterialEntity entity2 = new ProductMaterialEntity(2L, 1L, "Suela", 1);
        ProductMaterial domain1 = new ProductMaterial(1L, 1L, "Cuero", 2);
        ProductMaterial domain2 = new ProductMaterial(2L, 1L, "Suela", 1);

        when(productMaterialR2dbcRepository.findByProductIdOrderByIdAsc(1L))
                .thenReturn(Flux.just(entity1, entity2));
        when(productMaterialPersistenceMapper.toDomain(entity1)).thenReturn(domain1);
        when(productMaterialPersistenceMapper.toDomain(entity2)).thenReturn(domain2);

        StepVerifier.create(adapter.findByProductId(1L))
                .expectNext(domain1)
                .expectNext(domain2)
                .verifyComplete();
    }

    @Test
    void shouldFindByProductIdAndMaterialIgnoreCase() {
        ProductMaterialEntity entity = new ProductMaterialEntity(10L, 1L, "Cuero", 2);
        ProductMaterial domain = new ProductMaterial(10L, 1L, "Cuero", 2);

        when(productMaterialR2dbcRepository.findByProductIdAndMaterialIgnoreCase(1L, "cuero"))
                .thenReturn(Mono.just(entity));
        when(productMaterialPersistenceMapper.toDomain(entity)).thenReturn(domain);

        StepVerifier.create(adapter.findByProductIdAndMaterialIgnoreCase(1L, "cuero"))
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenMaterialNotFoundForProduct() {
        when(productMaterialR2dbcRepository.findByProductIdAndMaterialIgnoreCase(1L, "missing"))
                .thenReturn(Mono.empty());

        StepVerifier.create(adapter.findByProductIdAndMaterialIgnoreCase(1L, "missing"))
                .verifyComplete();
    }

    @Test
    void shouldDeleteByProductId() {
        when(productMaterialR2dbcRepository.deleteByProductId(1L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteByProductId(1L))
                .verifyComplete();

        verify(productMaterialR2dbcRepository).deleteByProductId(1L);
    }
}
