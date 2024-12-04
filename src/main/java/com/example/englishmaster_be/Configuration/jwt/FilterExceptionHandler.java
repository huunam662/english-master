package com.example.englishmaster_be.Configuration.jwt;

import com.example.englishmaster_be.Exception.Error;
import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

@Configuration
public class FilterExceptionHandler implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(FilterExceptionHandler.class);

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) {

        writeExceptionBodyResponse(request, response, Error.UNAUTHENTICATED);
    }


    @SneakyThrows
    private void writeExceptionBodyResponse(HttpServletRequest request, HttpServletResponse response, Error error){

        response.setStatus(error.getStatusCode().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                Json.pretty(
                        ExceptionResponseModel.builder()
                                .success(false)
                                .status(error.getStatusCode())
                                .code(error.getStatusCode().value())
                                .message(error.getMessage())
                                .violations(error.getViolation())
                                .build()
                )
        );
    }
}
