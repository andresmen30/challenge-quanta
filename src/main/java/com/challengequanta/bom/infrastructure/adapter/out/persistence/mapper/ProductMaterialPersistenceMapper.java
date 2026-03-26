package com.challengequanta.bom.infrastructure.adapter.out.persistence.mapper;

import com.challengequanta.bom.domain.model.ProductMaterial;
import com.challengequanta.bom.infrastructure.adapter.out.persistence.entity.ProductMaterialEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMaterialPersistenceMapper {

    ProductMaterialEntity toEntity(ProductMaterial productMaterial);

    ProductMaterial toDomain(ProductMaterialEntity productMaterialEntity);
}
