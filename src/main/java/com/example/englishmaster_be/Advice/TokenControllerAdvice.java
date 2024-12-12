package com.example.englishmaster_be.Advice;

import com.example.englishmaster_be.Exception.RefreshTokenException;
import com.example.englishmaster_be.Common.dto.response.ExceptionResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;


@RestControllerAdvice
public class TokenControllerAdvice {

    @ExceptionHandler(value = RefreshTokenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionResponseModel handleTokenRefreshException(RefreshTokenException ex, WebRequest request) {
        return ExceptionResponseModel.builder()
                .success(Boolean.FALSE)
                .message(ex.getMessage())
                .status(HttpStatus.FORBIDDEN)
                .code(HttpStatus.FORBIDDEN.value())
                .build();
    }
}
