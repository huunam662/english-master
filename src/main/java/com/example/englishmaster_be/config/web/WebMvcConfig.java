package com.example.englishmaster_be.config.web;

import com.example.englishmaster_be.common.constant.sort.PackTypeSortBy;
import com.example.englishmaster_be.common.constant.sort.TopicSortBy;
import com.example.englishmaster_be.config.interceptor.InterceptorConfig;
import com.example.englishmaster_be.value.AppValue;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
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

    InterceptorConfig globalInterceptorHandler;


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

    @Override
    public void addFormatters(FormatterRegistry registry) {

        registry.addConverter(new Converter<String, PackTypeSortBy>() {

            @Override
            @NonNull
            public PackTypeSortBy convert(@NonNull String source) {

                return PackTypeSortBy.fromValue(source);
            }

        });

        registry.addConverter(new Converter<String, TopicSortBy>() {

            @Override
            @NonNull
            public TopicSortBy convert(@NonNull String source) {

                return TopicSortBy.fromCode(source);
            }
        });
    }
}
