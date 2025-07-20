package com.example.englishmaster_be.config.app.swagger;

import com.example.englishmaster_be.value.AppValue;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

    private final AppValue appValue;

    public SwaggerConfig(AppValue appValue) {
        this.appValue = appValue;
    }

    @Bean
    public OpenAPI openAPI() {

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info().title("Master English API")
                        .version("2.0.0")
                        .description("Some custom description of API."))
                .addServersItem(new Server().url("http://localhost:8080" + appValue.getEndpoint_prefix())
                        .description("Local server"))
                .addServersItem(new Server().url("https://gateway.dev.meu-solutions.com/englishmaster" + appValue.getEndpoint_prefix())
                        .description("Production server"));

    }

    private SecurityScheme createAPIKeyScheme() {

        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }


    @Bean
    public OpenApiCustomizer removeApiPrefixCustomizer(){

        return openApi -> {

            Paths paths = new Paths();

            openApi.getPaths().keySet().forEach(
                    path -> {

                        if(path.startsWith(appValue.getEndpoint_prefix())){

                            String pathWithoutPrefix = path.substring(appValue.getEndpoint_prefix().length());

                            paths.put(pathWithoutPrefix, openApi.getPaths().get(path));

                        }
                        else paths.put(path, openApi.getPaths().get(path));
                    }
            );

            openApi.setPaths(paths);
        };
    }

}