package com.example.englishmaster_be.domain.mock_test.evaluator_writing.util;

import java.util.Map;

public class ResponseSchema {
    private String type;
    private Map<String, Object> properties;

    public ResponseSchema() {
    }

    public ResponseSchema(String type, Map<String, Object> properties) {
        this.type = type;
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}