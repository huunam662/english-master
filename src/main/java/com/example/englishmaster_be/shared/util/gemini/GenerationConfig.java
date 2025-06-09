package com.example.englishmaster_be.shared.util.gemini;

public class GenerationConfig {
    private Double temperature;
    private Integer topK;
    private Double topP;
    private Integer maxOutputTokens;
    private String responseMimeType;
    private ResponseSchema responseSchema;

    public GenerationConfig() {
    }

    private GenerationConfig(Builder builder) {
        this.temperature = builder.temperature;
        this.topK = builder.topK;
        this.topP = builder.topP;
        this.maxOutputTokens = builder.maxOutputTokens;
        this.responseMimeType = builder.responseMimeType;
        this.responseSchema = builder.responseSchema;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getTopK() {
        return topK;
    }

    public void setTopK(Integer topK) {
        this.topK = topK;
    }

    public Double getTopP() {
        return topP;
    }

    public void setTopP(Double topP) {
        this.topP = topP;
    }

    public Integer getMaxOutputTokens() {
        return maxOutputTokens;
    }

    public void setMaxOutputTokens(Integer maxOutputTokens) {
        this.maxOutputTokens = maxOutputTokens;
    }

    public String getResponseMimeType() {
        return responseMimeType;
    }

    public void setResponseMimeType(String responseMimeType) {
        this.responseMimeType = responseMimeType;
    }

    public ResponseSchema getResponseSchema() {
        return responseSchema;
    }

    public void setResponseSchema(ResponseSchema responseSchema) {
        this.responseSchema = responseSchema;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Double temperature;
        private Integer topK;
        private Double topP;
        private Integer maxOutputTokens;
        private String responseMimeType;
        private ResponseSchema responseSchema;

        public Builder setTemperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder setTopK(Integer topK) {
            this.topK = topK;
            return this;
        }

        public Builder setTopP(Double topP) {
            this.topP = topP;
            return this;
        }

        public Builder setMaxOutputTokens(Integer maxOutputTokens) {
            this.maxOutputTokens = maxOutputTokens;
            return this;
        }

        public Builder setResponseMimeType(String responseMimeType) {
            this.responseMimeType = responseMimeType;
            return this;
        }

        public Builder setResponseSchema(ResponseSchema responseSchema) {
            this.responseSchema = responseSchema;
            return this;
        }

        public GenerationConfig build() {
            return new GenerationConfig(this);
        }
    }
}
