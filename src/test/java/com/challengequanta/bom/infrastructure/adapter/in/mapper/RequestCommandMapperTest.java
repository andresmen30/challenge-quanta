package com.challengequanta.bom.infrastructure.adapter.in.mapper;

import com.challengequanta.bom.domain.port.in.command.AddMaterialCommand;
import com.challengequanta.bom.domain.port.in.command.CreateProductCommand;
import com.challengequanta.bom.infrastructure.adapter.in.dto.AddMaterialRequest;
import com.challengequanta.bom.infrastructure.adapter.in.dto.CreateProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestCommandMapperTest {

    private RequestCommandMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RequestCommandMapperImpl();
    }

    @Test
    void shouldMapCreateProductRequestToCommand() {
        CreateProductRequest request = new CreateProductRequest("Zapato");

        CreateProductCommand command = mapper.toCreateProductCommand(request);

        assertThat(command).isEqualTo(new CreateProductCommand("Zapato"));
    }

    @Test
    void shouldReturnNullWhenMappingNullCreateProductRequest() {
        assertThat(mapper.toCreateProductCommand(null)).isNull();
    }

    @Test
    void shouldMapAddMaterialRequestToCommand() {
        AddMaterialRequest request = new AddMaterialRequest("Cuero", 3);

        AddMaterialCommand command = mapper.toAddMaterialCommand(request);

        assertThat(command).isEqualTo(new AddMaterialCommand("Cuero", 3));
    }

    @Test
    void shouldReturnNullWhenMappingNullAddMaterialRequest() {
        assertThat(mapper.toAddMaterialCommand(null)).isNull();
    }
}
