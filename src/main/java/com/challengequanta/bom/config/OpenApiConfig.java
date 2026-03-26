package com.challengequanta.bom.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI challengeQuantaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Challenge Quanta BOM API")
                        .description("Reactive BOM microservice for products, materials and production calculations.")
                        .version("v1")
                        .contact(new Contact().name("Challenge Quanta Team")));
    }
}
