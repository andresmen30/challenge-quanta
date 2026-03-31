package com.challengequanta.bom.infrastructure.adapter.in.handler;

import com.challengequanta.bom.domain.port.in.ProductUseCase;
import com.challengequanta.bom.infrastructure.adapter.in.dto.AddMaterialRequest;
import com.challengequanta.bom.infrastructure.adapter.in.dto.CreateProductRequest;
import com.challengequanta.bom.infrastructure.adapter.in.mapper.RequestCommandMapper;
import com.challengequanta.bom.infrastructure.adapter.in.mapper.ResponseMapper;
import com.challengequanta.bom.shared.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductHandler {

    private final ProductUseCase productUseCase;
    private final RequestValidator requestValidator;
    private final RequestParamExtractor requestParamExtractor;
    private final RequestCommandMapper requestCommandMapper;
    private final ResponseMapper responseMapper;

    public Mono<ServerResponse> createProduct(final ServerRequest request) {
        return request.bodyToMono(CreateProductRequest.class)
                .switchIfEmpty(Mono.error(new BadRequestException("Request body is required")))
                .flatMap(requestValidator::validate)
                .map(requestCommandMapper::toCreateProductCommand)
                .flatMap(productUseCase::createProduct)
                .map(responseMapper::toProductResponse)
                .flatMap(productResponse -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(productResponse));
    }

    public Mono<ServerResponse> deleteProduct(final ServerRequest request) {
        Long productId = requestParamExtractor.positiveLongPathVariable(request, "productId");

        return productUseCase.deleteProduct(productId)
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> addMaterial(final ServerRequest request) {
        Long productId = requestParamExtractor.positiveLongPathVariable(request, "productId");

        return request.bodyToMono(AddMaterialRequest.class)
                .switchIfEmpty(Mono.error(new BadRequestException("Request body is required")))
                .flatMap(requestValidator::validate)
                .map(requestCommandMapper::toAddMaterialCommand)
                .flatMap(command -> productUseCase.addMaterial(productId, command))
                .map(responseMapper::toMaterialResponse)
                .flatMap(materialResponse -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(materialResponse));
    }
}
