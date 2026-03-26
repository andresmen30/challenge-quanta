package com.challengequanta.bom.infrastructure.adapter.in.handler;

import com.challengequanta.bom.domain.port.in.ProductionUseCase;
import com.challengequanta.bom.infrastructure.adapter.in.mapper.ResponseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductionHandler {

    private final ProductionUseCase productionUseCase;
    private final RequestParamExtractor requestParamExtractor;
    private final ResponseMapper responseMapper;

    public Mono<ServerResponse> calculate(ServerRequest request) {
        Long productId = requestParamExtractor.positiveLongQueryParam(request, "productId");
        Integer quantity = requestParamExtractor.positiveIntegerQueryParam(request, "quantity");

        return productionUseCase.calculateProduction(productId, quantity)
                .map(responseMapper::toProductionResultResponse)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }
}
