package com.example.englishmaster_be.advice;

import com.example.englishmaster_be.exception.template.RefreshTokenException;
import com.example.englishmaster_be.model.ResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;


@RestControllerAdvice
public class TokenControllerAdvice {

    @ExceptionHandler(value = RefreshTokenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseModel handleTokenRefreshException(RefreshTokenException ex, WebRequest request) {
        return new ResponseModel(
                ex.getMessage(),
                null,
                "false",
                String.valueOf(HttpStatus.FORBIDDEN.value()));
    }
}
