package com.example.englishmaster_be.shared.util.gemini;

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
