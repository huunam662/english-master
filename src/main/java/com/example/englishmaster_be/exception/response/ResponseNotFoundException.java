package com.example.englishmaster_be.exception.response;

public class ResponseNotFoundException extends RuntimeException{
    public ResponseNotFoundException(String message) {
        super(message);
    }

    public ResponseNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
