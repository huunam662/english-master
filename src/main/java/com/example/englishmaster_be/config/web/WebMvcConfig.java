package com.example.englishmaster_be.config.web;

import com.example.englishmaster_be.config.global.interceptor.GlobalInterceptorConfig;
import com.example.englishmaster_be.value.AppValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebMvcConfig implements WebMvcConfigurer {

    AppValue appValue;

    GlobalInterceptorConfig globalInterceptorHandler;


    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {

        configurer.addPathPrefix(appValue.getEndpoint_prefix(), clazz -> clazz.isAnnotationPresent(RestController.class));
    }

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
