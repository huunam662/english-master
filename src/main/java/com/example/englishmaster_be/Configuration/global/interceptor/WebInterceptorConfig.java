package com.example.englishmaster_be.Configuration.global.interceptor;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebInterceptorConfig implements WebMvcConfigurer {

    GlobalInterceptorHandler globalInterceptorHandler;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(globalInterceptorHandler)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                );
    }

}
