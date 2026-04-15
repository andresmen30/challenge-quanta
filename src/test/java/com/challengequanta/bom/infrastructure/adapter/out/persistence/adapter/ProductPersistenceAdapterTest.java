package com.challengequanta.bom.infrastructure.adapter.out.persistence.adapter;

import com.challengequanta.bom.domain.model.Product;
import com.challengequanta.bom.infrastructure.adapter.out.persistence.entity.ProductEntity;
import com.challengequanta.bom.infrastructure.adapter.out.persistence.mapper.ProductPersistenceMapper;
import com.challengequanta.bom.infrastructure.adapter.out.persistence.repository.ProductR2dbcRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductPersistenceAdapterTest {

    @Mock
    private ProductR2dbcRepository productR2dbcRepository;

    @Mock
    private ProductPersistenceMapper productPersistenceMapper;

    @InjectMocks
    private ProductPersistenceAdapter adapter;

    @Test
    void shouldSaveProductAndReturnDomain() {
        Product domain = new Product(null, "Zapato");
        ProductEntity entity = new ProductEntity(null, "Zapato");
        ProductEntity savedEntity = new ProductEntity(1L, "Zapato");
        Product savedDomain = new Product(1L, "Zapato");

        when(productPersistenceMapper.toEntity(domain)).thenReturn(entity);
        when(productR2dbcRepository.save(entity)).thenReturn(Mono.just(savedEntity));
        when(productPersistenceMapper.toDomain(savedEntity)).thenReturn(savedDomain);

        StepVerifier.create(adapter.save(domain))
                .expectNext(savedDomain)
                .verifyComplete();

        verify(productR2dbcRepository).save(entity);
    }

    @Test
    void shouldFindByIdAndMapToDomain() {
        ProductEntity entity = new ProductEntity(1L, "Zapato");
        Product domain = new Product(1L, "Zapato");

        when(productR2dbcRepository.findById(1L)).thenReturn(Mono.just(entity));
        when(productPersistenceMapper.toDomain(entity)).thenReturn(domain);

        StepVerifier.create(adapter.findById(1L))
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenProductNotFoundById() {
        when(productR2dbcRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById(99L))
                .verifyComplete();

        verify(productPersistenceMapper, org.mockito.Mockito.never()).toDomain(any(ProductEntity.class));
    }

    @Test
    void shouldFindByNameIgnoreCaseAndMapToDomain() {
        ProductEntity entity = new ProductEntity(1L, "Zapato");
        Product domain = new Product(1L, "Zapato");

        when(productR2dbcRepository.findByNameIgnoreCase("zapato")).thenReturn(Mono.just(entity));
        when(productPersistenceMapper.toDomain(entity)).thenReturn(domain);

        StepVerifier.create(adapter.findByNameIgnoreCase("zapato"))
                .expectNext(domain)
                .verifyComplete();
    }

    @Test
    void shouldDeleteById() {
        when(productR2dbcRepository.deleteById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteById(1L))
                .verifyComplete();

        verify(productR2dbcRepository).deleteById(1L);
    }
}
