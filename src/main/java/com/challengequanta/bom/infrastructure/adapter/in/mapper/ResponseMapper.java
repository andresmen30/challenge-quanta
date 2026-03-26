package com.challengequanta.bom.infrastructure.adapter.in.mapper;

import com.challengequanta.bom.domain.model.Product;
import com.challengequanta.bom.domain.model.ProductMaterial;
import com.challengequanta.bom.domain.model.ProductionResult;
import com.challengequanta.bom.domain.model.RequiredMaterial;
import com.challengequanta.bom.infrastructure.adapter.in.dto.MaterialResponse;
import com.challengequanta.bom.infrastructure.adapter.in.dto.ProductResponse;
import com.challengequanta.bom.infrastructure.adapter.in.dto.ProductionResultResponse;
import com.challengequanta.bom.infrastructure.adapter.in.dto.RequiredMaterialResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ResponseMapper {

    ProductResponse toProductResponse(Product product);

    MaterialResponse toMaterialResponse(ProductMaterial productMaterial);

    ProductionResultResponse toProductionResultResponse(ProductionResult productionResult);

    RequiredMaterialResponse toRequiredMaterialResponse(RequiredMaterial requiredMaterial);
}
