package com.challengequanta.bom.infrastructure.adapter.in.router;

import com.challengequanta.bom.infrastructure.adapter.in.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ProductRouter {

    @Bean
    public RouterFunction<ServerResponse> productRoutes(final ProductHandler productHandler) {
        return RouterFunctions.route()
                .POST("/products", productHandler::createProduct)
                .POST("/products/{productId}/materials", productHandler::addMaterial)
                .build();
    }
}
