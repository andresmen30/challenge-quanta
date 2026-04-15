package com.challengequanta.bom.infrastructure.adapter.in.mapper;

import com.challengequanta.bom.domain.model.Product;
import com.challengequanta.bom.domain.model.ProductMaterial;
import com.challengequanta.bom.domain.model.ProductionResult;
import com.challengequanta.bom.domain.model.RequiredMaterial;
import com.challengequanta.bom.infrastructure.adapter.in.dto.MaterialResponse;
import com.challengequanta.bom.infrastructure.adapter.in.dto.ProductResponse;
import com.challengequanta.bom.infrastructure.adapter.in.dto.ProductionResultResponse;
import com.challengequanta.bom.infrastructure.adapter.in.dto.RequiredMaterialResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseMapperTest {

    private ResponseMapper responseMapper;

    @BeforeEach
    void setUp() {
        responseMapper = new ResponseMapperImpl();
    }

    @Test
    void shouldMapProductToProductResponse() {
        Product product = new Product(1L, "Zapato");

        ProductResponse response = responseMapper.toProductResponse(product);

        assertThat(response).isEqualTo(new ProductResponse(1L, "Zapato"));
    }

    @Test
    void shouldReturnNullWhenMappingNullProduct() {
        assertThat(responseMapper.toProductResponse(null)).isNull();
    }

    @Test
    void shouldMapProductMaterialToMaterialResponse() {
        ProductMaterial productMaterial = new ProductMaterial(10L, 1L, "Cuero", 2);

        MaterialResponse response = responseMapper.toMaterialResponse(productMaterial);

        assertThat(response).isEqualTo(new MaterialResponse(10L, 1L, "Cuero", 2));
    }

    @Test
    void shouldMapRequiredMaterialToRequiredMaterialResponse() {
        RequiredMaterial requiredMaterial = new RequiredMaterial("Cuero", 200);

        RequiredMaterialResponse response = responseMapper.toRequiredMaterialResponse(requiredMaterial);

        assertThat(response).isEqualTo(new RequiredMaterialResponse("Cuero", 200));
    }

    @Test
    void shouldMapProductionResultToProductionResultResponse() {
        ProductionResult productionResult = new ProductionResult(
                "Zapato",
                100,
                List.of(
                        new RequiredMaterial("Cuero", 200),
                        new RequiredMaterial("Suela", 100)
                )
        );

        ProductionResultResponse response = responseMapper.toProductionResultResponse(productionResult);

        assertThat(response.product()).isEqualTo("Zapato");
        assertThat(response.quantity()).isEqualTo(100);
        assertThat(response.materials()).containsExactly(
                new RequiredMaterialResponse("Cuero", 200),
                new RequiredMaterialResponse("Suela", 100)
        );
    }

    @Test
    void shouldMapProductionResultWithEmptyMaterials() {
        ProductionResult productionResult = new ProductionResult("Zapato", 100, List.of());

        ProductionResultResponse response = responseMapper.toProductionResultResponse(productionResult);

        assertThat(response.product()).isEqualTo("Zapato");
        assertThat(response.quantity()).isEqualTo(100);
        assertThat(response.materials()).isEmpty();
    }
}
