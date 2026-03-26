package com.challengequanta.bom.infrastructure.adapter.in.router;

import com.challengequanta.bom.infrastructure.adapter.in.dto.ProductionResultResponse;
import com.challengequanta.bom.infrastructure.adapter.in.handler.ProductionHandler;
import com.challengequanta.bom.shared.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ProductionRouter {

    @Bean
    @RouterOperation(
            path = "/production/calculate",
            method = RequestMethod.GET,
            beanClass = ProductionHandler.class,
            beanMethod = "calculate",
            operation = @Operation(
                    operationId = "calculateProduction",
                    summary = "Calculate required materials",
                    description = "Calculates the required materials for a product and production quantity.",
                    tags = {"Production"},
                    parameters = {
                            @Parameter(
                                    in = ParameterIn.QUERY,
                                    name = "productId",
                                    required = true,
                                    description = "Product identifier",
                                    schema = @Schema(type = "integer", format = "int64")
                            ),
                            @Parameter(
                                    in = ParameterIn.QUERY,
                                    name = "quantity",
                                    required = true,
                                    description = "Production quantity",
                                    schema = @Schema(type = "integer", format = "int32")
                            )
                    },
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "Calculation successful",
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = ProductionResultResponse.class)
                                    )
                            ),
                            @ApiResponse(
                                    responseCode = "400",
                                    description = "Invalid request",
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = ErrorResponse.class)
                                    )
                            ),
                            @ApiResponse(
                                    responseCode = "404",
                                    description = "Product not found",
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = ErrorResponse.class)
                                    )
                            )
                    }
            )
    )
    public RouterFunction<ServerResponse> productionRoutes(ProductionHandler productionHandler) {
        return RouterFunctions.route()
                .GET("/production/calculate", productionHandler::calculate)
                .build();
    }
}
