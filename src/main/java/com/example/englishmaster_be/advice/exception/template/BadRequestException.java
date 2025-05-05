package com.example.englishmaster_be.advice.exception.template;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
