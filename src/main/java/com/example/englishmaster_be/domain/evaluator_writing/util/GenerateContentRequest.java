package com.example.englishmaster_be.domain.evaluator_writing.util;

import java.util.ArrayList;
import java.util.List;

public class GenerateContentRequest {
    private List<Content> contents;
    private GenerationConfig generationConfig;
    private SystemInstruction systemInstruction;

    public GenerateContentRequest() {
        this.contents = new ArrayList<>();
    }

    public GenerateContentRequest(List<Content> contents) {
        this.contents = contents;
    }

    public GenerateContentRequest(List<Content> contents, GenerationConfig generationConfig) {
        this.contents = contents;
        this.generationConfig = generationConfig;
    }

    public GenerateContentRequest(List<Content> contents, GenerationConfig generationConfig, SystemInstruction systemInstruction) {
        this.contents = contents;
        this.generationConfig = generationConfig;
        this.systemInstruction = systemInstruction;
    }

    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    public GenerationConfig getGenerationConfig() {
        return generationConfig;
    }

    public void setGenerationConfig(GenerationConfig generationConfig) {
        this.generationConfig = generationConfig;
    }

    public SystemInstruction getSystemInstruction() {
        return systemInstruction;
    }

    public void setSystemInstruction(SystemInstruction systemInstruction) {
        this.systemInstruction = systemInstruction;
    }
}
