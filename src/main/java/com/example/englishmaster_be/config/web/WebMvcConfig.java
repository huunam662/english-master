package com.example.englishmaster_be.config.web;

<<<<<<< HEAD
import com.example.englishmaster_be.advice.interceptor.GlobalAppInterceptor;
=======
import com.example.englishmaster_be.common.constant.speaking_test.LevelSpeakerType;
import com.example.englishmaster_be.common.constant.TopicType;
import com.example.englishmaster_be.common.constant.sort.FlashCardSortBy;
import com.example.englishmaster_be.common.constant.sort.PackTypeSortBy;
import com.example.englishmaster_be.common.constant.sort.TopicSortBy;
import com.example.englishmaster_be.config.interceptor.InterceptorConfig;
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c
import com.example.englishmaster_be.value.AppValue;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.util.List;
import java.util.stream.Stream;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AppValue appValue;

    private final GlobalAppInterceptor globalInterceptorHandler;

    public WebMvcConfig(AppValue appValue, GlobalAppInterceptor globalInterceptorHandler) {
        this.appValue = appValue;
        this.globalInterceptorHandler = globalInterceptorHandler;
    }

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
<<<<<<< HEAD
=======

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

        registry.addConverter(new Converter<String, TopicType>() {
            @Override
            @NonNull
            public TopicType convert(@NonNull String source) {
                return TopicType.fromType(source);
            }
        });

        registry.addConverter(new Converter<String, FlashCardSortBy>() {
            @Override
            @NonNull
            public FlashCardSortBy convert(@NonNull String source) {
                return FlashCardSortBy.fromValue(source);
            }
        });
        registry.addConverter(new Converter<String, LevelSpeakerType>() {
            @Override
            @NonNull
            public LevelSpeakerType convert(@NonNull String source) {
                return LevelSpeakerType.fromLevel(source);
            }
        });
    }
>>>>>>> 197ed81940903ab14a285d25c6aed6f94b8e649c
}
