package com.example.englishmaster_be.domain.evaluator_writing.util;

import java.util.ArrayList;
import java.util.List;

public class SystemInstruction {
    private String role;
    private List<Part> parts;

    public SystemInstruction() {
        this.parts = new ArrayList<>();
    }

    public SystemInstruction(String role, List<Part> parts) {
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