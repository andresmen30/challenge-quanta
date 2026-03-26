package com.challengequanta.bom.infrastructure.adapter.out.persistence.mapper;

import com.challengequanta.bom.domain.model.Product;
import com.challengequanta.bom.infrastructure.adapter.out.persistence.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductPersistenceMapper {

    ProductEntity toEntity(Product product);

    Product toDomain(ProductEntity productEntity);
}
