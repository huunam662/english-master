package com.example.englishmaster_be.Exception.Response;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
