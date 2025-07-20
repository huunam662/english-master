package com.example.englishmaster_be.domain.mock_test.evaluator_writing.util;

public class GeminiResponse {
    private final Object content;

    public GeminiResponse(Object content) {
        this.content = content;
    }

    @SuppressWarnings("unchecked")
    public <T> T content() {
        return (T) content;
    }
}
