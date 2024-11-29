package com.example.englishmaster_be.Configuration.jwt;

import com.example.englishmaster_be.Exception.Error;
import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
public class FilterExceptionHandler implements AccessDeniedHandler, AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(FilterExceptionHandler.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {

        Error error = Error.UNAUTHORIZED;
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                Json.pretty(
                        ExceptionResponseModel.builder()
                                .success(false)
                                .status(error.getStatusCode())
                                .code(error.getStatusCode().value())
                                .message(error.getMessage())
                                .path(request.getRequestURI())
                                .build()
                )
        );
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                Json.pretty(
                        ExceptionResponseModel.builder()
                                .success(false)
                                .status(HttpStatus.FORBIDDEN)
                                .code(HttpStatus.FORBIDDEN.value())
                                .message(HttpStatus.FORBIDDEN.getReasonPhrase())
                                .path(request.getRequestURI())
                                .build()
                )
        );
    }
}
