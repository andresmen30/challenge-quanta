package com.challengequanta.bom.infrastructure.adapter.in.router;

import com.challengequanta.bom.infrastructure.adapter.in.dto.AddMaterialRequest;
import com.challengequanta.bom.infrastructure.adapter.in.dto.CreateProductRequest;
import com.challengequanta.bom.infrastructure.adapter.in.dto.MaterialResponse;
import com.challengequanta.bom.infrastructure.adapter.in.dto.ProductResponse;
import com.challengequanta.bom.infrastructure.adapter.in.handler.ProductHandler;
import com.challengequanta.bom.shared.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ProductRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/products",
                    method = RequestMethod.POST,
                    beanClass = ProductHandler.class,
                    beanMethod = "createProduct",
                    operation = @Operation(
                            operationId = "createProduct",
                            summary = "Create product",
                            description = "Creates a new product in the BOM catalog.",
                            tags = {"Products"},
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = CreateProductRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Product created",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ProductResponse.class)
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
                                            responseCode = "409",
                                            description = "Product already exists",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ErrorResponse.class)
                                            )
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/products/{productId}",
                    method = RequestMethod.DELETE,
                    beanClass = ProductHandler.class,
                    beanMethod = "deleteProduct",
                    operation = @Operation(
                            operationId = "deleteProduct",
                            summary = "Delete product",
                            description = "Deletes a product and all its associated materials from the BOM catalog.",
                            tags = {"Products"},
                            parameters = {
                                    @Parameter(
                                            in = ParameterIn.PATH,
                                            name = "productId",
                                            required = true,
                                            description = "Product identifier",
                                            schema = @Schema(type = "integer", format = "int64")
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "204",
                                            description = "Product deleted"
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Invalid productId",
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
            ),
            @RouterOperation(
                    path = "/products/{productId}/materials",
                    method = RequestMethod.POST,
                    beanClass = ProductHandler.class,
                    beanMethod = "addMaterial",
                    operation = @Operation(
                            operationId = "addMaterial",
                            summary = "Add material to a product",
                            description = "Associates a material and quantity to a product.",
                            tags = {"Products"},
                            parameters = {
                                    @Parameter(
                                            in = ParameterIn.PATH,
                                            name = "productId",
                                            required = true,
                                            description = "Product identifier",
                                            schema = @Schema(type = "integer", format = "int64")
                                    )
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = AddMaterialRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Material added",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = MaterialResponse.class)
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
                                    ),
                                    @ApiResponse(
                                            responseCode = "409",
                                            description = "Material already exists for product",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ErrorResponse.class)
                                            )
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> productRoutes(final ProductHandler productHandler) {
        return RouterFunctions.route()
                .POST("/products", productHandler::createProduct)
                .DELETE("/products/{productId}", productHandler::deleteProduct)
                .POST("/products/{productId}/materials", productHandler::addMaterial)
                .build();
    }
}
