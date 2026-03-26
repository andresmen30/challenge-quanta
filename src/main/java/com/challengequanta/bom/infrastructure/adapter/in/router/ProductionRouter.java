package com.challengequanta.bom.infrastructure.adapter.in.router;

import com.challengequanta.bom.infrastructure.adapter.in.handler.ProductionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ProductionRouter {

    @Bean
    public RouterFunction<ServerResponse> productionRoutes(ProductionHandler productionHandler) {
        return RouterFunctions.route()
                .GET("/production/calculate", productionHandler::calculate)
                .build();
    }
}
