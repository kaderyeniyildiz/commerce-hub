package com.commercehub.user.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI commerceHubOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CommerceHub API")
                        .description("REST API for the CommerceHub application")
                        .version("1.0.0"));
    }
}
