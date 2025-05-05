package com.example.englishmaster_be.config.web;

import com.example.englishmaster_be.config.middleware.interceptor.MiddlewareInterceptorConfig;
import com.example.englishmaster_be.value.AppValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.stream.Stream;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WebMvcConfig implements WebMvcConfigurer {

    AppValue appValue;

    MiddlewareInterceptorConfig globalInterceptorHandler;


    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {

        configurer.addPathPrefix(
                appValue.getEndpoint_prefix(),
                clazz -> clazz.isAnnotationPresent(RestController.class)
        );
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        List<String> pathLocal = List.of(
                appValue.getEndpoint_prefix() + "/v3/api-docs/**",
                appValue.getEndpoint_prefix() + "/swagger-resources/**",
                appValue.getEndpoint_prefix() + "/webjars/**"
        );

        List<String> pathProduction = pathLocal
                .stream()
                .map(path -> "/englishmaster" + path)
                .toList();

        List<String> pathMerge = Stream.concat(
                pathProduction.stream(),
                pathLocal.stream()
        ).toList();

        registry.addInterceptor(globalInterceptorHandler)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        pathMerge
                );
    }

}
