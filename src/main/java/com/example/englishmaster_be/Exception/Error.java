package com.example.englishmaster_be.Exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum Error {
    PART_NOT_FOUND("Not found part by id", "Not found part by id", HttpStatus.NOT_FOUND),
    MOCK_TEST_NOT_FOUND("Mock test not found by id", "Mock test not found by id", HttpStatus.NOT_FOUND),
    ANSWER_NOT_FOUND("Answer not found by id", "Answer not found by id", HttpStatus.NOT_FOUND),
    ANSWER_BY_CORRECT_QUESTION_NOT_FOUND("Question don't have correct answer", "Question don't have correct answer", HttpStatus.NOT_FOUND),
    UNAUTHORIZED("You do not have permission", "You do not have permission", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED("Unauthenticated", "Your token cannot be authenticated ", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("Token Expired", "Your token has expired", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("Invalid Token", "Your token is malformed and cannot be processed", HttpStatus.UNAUTHORIZED),
    UNSUPPORTED_TOKEN("Unsupported Token", "Your token is unsupported", HttpStatus.UNAUTHORIZED),

    ;

    private final String message;
    private final String violation;
    private final HttpStatusCode statusCode;

    Error(String message, String violation, HttpStatusCode statusCode) {
        this.message = message;
        this.violation = violation;
        this.statusCode = statusCode;
    }
}

