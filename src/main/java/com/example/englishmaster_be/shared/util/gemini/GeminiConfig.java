package com.example.englishmaster_be.shared.util.gemini;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiConfig {
    @Bean
    @ConditionalOnProperty(
            name = {"gemini.api.key", "gemini.api.endpoint", "gemini.api.model"},
            matchIfMissing = false
    )
    public GeminiClient geminiClient(
            @Value("${gemini.api.key}") String apiKey,
            @Value("${gemini.api.endpoint}") String apiEndpoint,
            @Value("${gemini.api.model}") String modelName
    ) {
        return new GeminiClient(apiKey, apiEndpoint, modelName);
    }
}
