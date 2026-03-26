package com.challengequanta.bom.infrastructure.adapter.in.mapper;

import com.challengequanta.bom.domain.port.in.command.AddMaterialCommand;
import com.challengequanta.bom.domain.port.in.command.CreateProductCommand;
import com.challengequanta.bom.infrastructure.adapter.in.dto.AddMaterialRequest;
import com.challengequanta.bom.infrastructure.adapter.in.dto.CreateProductRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RequestCommandMapper {

    CreateProductCommand toCreateProductCommand(CreateProductRequest request);

    AddMaterialCommand toAddMaterialCommand(AddMaterialRequest request);
}
