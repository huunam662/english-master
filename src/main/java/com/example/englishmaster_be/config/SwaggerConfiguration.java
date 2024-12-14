package com.example.englishmaster_be.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Order(1)
@Configuration
public class SwaggerConfiguration implements WebMvcConfigurer {

    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info().title("Master English API")
                        .version("1.0.0")
                        .description("Some custom description of API."))
                .addServersItem(new Server().url("http://localhost:8080").description("Local server"))
                .addServersItem(new Server().url("https://gateway.dev.meu-solutions.com/englishmaster").description("Production server"));

    }

    private SecurityScheme createAPIKeyScheme() {

        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }
}