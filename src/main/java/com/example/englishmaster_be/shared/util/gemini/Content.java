package com.example.englishmaster_be.shared.util.gemini;

import java.util.ArrayList;
import java.util.List;

public class Content {
    private String role;
    private List<Part> parts;

    public Content() {
        this.parts = new ArrayList<>();
    }

    public Content(String role, List<Part> parts) {
        this.role = role;
        this.parts = parts;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }
}
