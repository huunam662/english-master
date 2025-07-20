package com.example.englishmaster_be.domain.mock_test.evaluator_writing.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GeminiClient {
    private final String apiKey;
    private final String geminiApiEndpoint;
    private final String modelName;
    private final HttpClient httpClient;
    private static final Logger logger = LoggerFactory.getLogger(GeminiClient.class);

    public GeminiClient(String apiKey,
                        String geminiApiEndpoint,
                        String modelName) {
        Assert.hasText(apiKey, "Gemini API key must be provided in application.properties as 'gemini.api.key'");
        Assert.hasText(geminiApiEndpoint, "Gemini API endpoint must be provided in application.properties as 'gemini.api.endpoint'");
        Assert.hasText(modelName, "Gemini model name must be provided in application.properties as 'gemini.api.model'");
        this.apiKey = apiKey;
        this.geminiApiEndpoint = geminiApiEndpoint;
        this.modelName = modelName;
        this.httpClient = HttpClient.newBuilder().build();
//        logger.info("{}, {}, {}", apiKey, geminiApiEndpoint, modelName);
    }

    public static GeminiRequestBuilder prompt(String prompt) {
        return new GeminiRequestBuilder().prompt(prompt);
    }

    public static String extractContentFromModelResponse(String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonString);

        JsonNode candidates = rootNode.get("candidates");
        if (candidates == null || !candidates.isArray() || candidates.isEmpty()) {
            return null; // Or throw an exception, depending on your error handling
        }

        // Get the first candidate.
        JsonNode firstCandidate = candidates.get(0);
        if (firstCandidate == null) {
            return null;
        }

        JsonNode content = firstCandidate.get("content");
        if (content == null || !content.isObject()) {
            return null;
        }

        JsonNode parts = content.get("parts");
        if (parts == null || !parts.isArray() || parts.isEmpty()) {
            return null;
        }

        JsonNode firstPart = parts.get(0);
        if (firstPart == null) {
            return null;
        }

        JsonNode textNode = firstPart.get("text");
        if (textNode == null || !textNode.isTextual()) {
            return null;
        }

        return firstPart.get("text").asText();
    }

    //    public <T> T content()  {
//        try {
//            ObjectMapper ow = new ObjectMapper();
////            String requestBodyJson = ow.writeValueAsString(generateContentRequest);
//            String requestBodyJson = "";
//            URI endpointUri = URI.create(geminiApiEndpoint + "/" + modelName + ":generateContent?key=" + apiKey);
//
//            HttpRequest httpRequest = HttpRequest.newBuilder()
//                    .uri(endpointUri)
//                    .header("Content-Type", "application/json")
//                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
//                    .build();
//            logger.debug("Sending HTTP request to URI: {}", httpRequest.uri());
//            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
//;
//            int statusCode = response.statusCode();
//            String responseBody = response.body();
//            logger.debug(responseBody);
//            if (statusCode >= 200 && statusCode < 300) {
////                return ow.readValue(extractContentFromModelResponse(responseBody), String.class);
//            } else {
//                System.out.println("gemini error");
//            }
//
//        } catch (IOException | InterruptedException e) {
//            System.out.println("gemini error");
//        }
//        return null;
//    }
    public static class GeminiRequestBuilder {
        private final GenerateContentRequest request;
        private Class<?> responseSchema;

        public GeminiRequestBuilder() {
            this.request = new GenerateContentRequest();
        }

        public GeminiRequestBuilder prompt(String prompt) {
            Content content = new Content();
            content.setRole("user");
            content.getParts().add(new Part(prompt));
            request.getContents().add(content);
            return this;
        }

        public GeminiRequestBuilder responseSchema(Class<?> responseSchema) {
            this.responseSchema = responseSchema;
            return this;
        }

        public GeminiRequestBuilder addContent(String role, String text) {
            Content content = new Content();
            content.setRole(role);
            content.getParts().add(new Part(text));
            request.getContents().add(content);
            return this;
        }

        public GeminiRequestBuilder generationConfig(GenerationConfig config) {
            request.setGenerationConfig(config);
            return this;
        }

        public GeminiRequestBuilder systemInstruction(String role, String text) {
            SystemInstruction instruction = new SystemInstruction();
            instruction.setRole(role);
            instruction.getParts().add(new Part(text));
            request.setSystemInstruction(instruction);
            return this;
        }

        public GeminiResponse call() {
            GeminiClient client = SpringApplicationContext.getBean(GeminiClient.class);
            URI uri = URI.create(client.geminiApiEndpoint + "/models/" + client.modelName + ":generateContent"
                    + "?key=" + client.apiKey);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", "text/plain")
                    .POST(HttpRequest.BodyPublishers.ofString(buildRequestBody()))
                    .build();

            logger.info("Building Request: Method={}, URI={}", httpRequest.method(), httpRequest.uri());
            logger.debug("Request Headers: {}", httpRequest.headers().map().entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining(", ")));
            logger.debug("Request Body: {}", buildRequestBody());

            logger.info("Request: {}", httpRequest);
            try {
                HttpResponse<String> response =
                        client.httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                ObjectMapper Mapper = new ObjectMapper();
                Object result;
                if (responseSchema != null) {
                    result = Mapper.readValue(extractContentFromModelResponse(response.body()), responseSchema);
                }
                result = extractContentFromModelResponse(response.body());
                logger.debug("Response Body: {}", result);
//                logger.debug("Response after format: {}", result);
                return new GeminiResponse(result);
            } catch (Exception e) {
                throw new RuntimeException("Failed to call Gemini API", e);
            }
        }

        private String buildRequestBody() {
            ObjectMapper Mapper = new ObjectMapper();
            Mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Exclude null fields
            try {
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("contents", request.getContents());
                if (request.getGenerationConfig() != null) {
                    requestMap.put("generationConfig", request.getGenerationConfig());
                }
                if (request.getSystemInstruction() != null) {
                    requestMap.put("systemInstruction", request.getSystemInstruction());
                }

                String jsonBody = Mapper.writeValueAsString(requestMap);
                logger.info("Built request body: {}", jsonBody);
                return jsonBody;
            } catch (Exception e) {
                logger.error("Failed to serialize request body", e);
                throw new RuntimeException("Failed to serialize request body", e);
            }
        }
    }
}