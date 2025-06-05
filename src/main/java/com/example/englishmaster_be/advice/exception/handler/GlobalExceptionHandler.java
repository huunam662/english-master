package com.example.englishmaster_be.advice.exception.handler;

import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.shared.dto.response.ResultApiResponse;
import com.example.englishmaster_be.value.AppValue;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.swagger.v3.core.util.Json;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.rmi.ServerException;
import java.util.*;

@Slf4j(topic = "GLOBAL-EXCEPTION-HANDLER")
@RestControllerAdvice
public class GlobalExceptionHandler implements AccessDeniedHandler, AuthenticationEntryPoint {

    private void logError(Error error, Exception ex){

        log.error("{} -> code {}", ex.getMessage(), error.getStatusCode().value());
    }

    public void printError(Error error, HttpServletResponse response) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(error.getStatusCode().value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(
                Json.pretty(
                        ResultApiResponse.ErrorResponse.build(error)
                )
        );
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        Error error = Error.UNAUTHENTICATED;

        logError(error, authException);

        this.printError(error, response);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        Error error = Error.UNAUTHORIZED;

        logError(error, accessDeniedException);

        this.printError(error, response);
    }

    @ExceptionHandler(ErrorHolder.class)
    public ResultApiResponse.ErrorResponse handleCustomException(ErrorHolder e) {

        Error error = e.getError();

        logError(error, e);

        return ResultApiResponse.ErrorResponse.build(e);
    }

    @ExceptionHandler({
            NoResourceFoundException.class,
            HttpRequestMethodNotSupportedException.class
    })
    public ModelAndView noResourceFoundHandler(Exception ex) {

        Error error = Error.RESOURCE_NOT_FOUND;

        logError(error, ex);

        ModelAndView mav = new ModelAndView("404/endpoint.404");

        mav.setStatus(error.getStatusCode());

        return mav;
    }

    @ExceptionHandler({
            NoSuchElementException.class
    })
    public ResultApiResponse.ErrorResponse handlingResourceNotFoundException(NoSuchElementException e){

        Error error = Error.RESOURCE_NOT_FOUND;

        logError(error, e);

        return ResultApiResponse.ErrorResponse.build(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResultApiResponse.ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e){

        Error error = Error.BAD_REQUEST;

        logError(error, e);

        Map<String, Object> errors = new HashMap<>();

        errors.put("param", e.getName());
        errors.put("invalid", e.getValue());
        errors.put("expected", e.getRequiredType().getSimpleName());

        return ResultApiResponse.ErrorResponse.build(error, errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResultApiResponse.ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {

        Error error = Error.BAD_REQUEST;

        logError(error, e);

        Throwable cause = e.getCause();

        Map<String, Object> errors = null;

        if(cause instanceof InvalidFormatException invalidFormatException){

            String fieldName = invalidFormatException.getPath().get(0).getFieldName();

            String invalidValue = invalidFormatException.getValue().toString();

            String expectedType = invalidFormatException.getTargetType().getSimpleName();

            errors = Map.of(
                    "fieldName", fieldName,
                    "invalidValue", invalidValue,
                    "type", String.format("%s value not type of %s", invalidValue, expectedType)
            );
        }
        else if(cause instanceof JsonParseException jsonParseException){

            JsonLocation location = jsonParseException.getLocation();

            int line = location.getLineNr();
            int column = location.getColumnNr();

            String messageResponse = String.format("Line: %d, column: %d -> %s", line, column, "can not parse by JSON.");

            errors = Map.of(
                    "parser", messageResponse
            );
        }

        return ResultApiResponse.ErrorResponse.build(error, errors);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultApiResponse.ErrorResponse handleValidationExceptions(MethodArgumentNotValidException exception) throws NoSuchFieldException {

        Error error = Error.BAD_REQUEST;

        logError(error, exception);

        BindingResult bindingResult = exception.getBindingResult();

        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        Map<String, String> errors = null;

        if(!fieldErrors.isEmpty()) {

            int lastIndex = fieldErrors.size() - 1;

            errors = new HashMap<>();

            Object target = bindingResult.getTarget();

            String fieldName = fieldErrors.get(lastIndex).getField();

            Field field = target.getClass().getDeclaredField(fieldName);

            errors.put(fieldName, "Must be type: "+field.getType().getSimpleName());
        }
        return ResultApiResponse.ErrorResponse.build(error, errors);
    }

    @ExceptionHandler({
            UsernameNotFoundException.class,
            BadCredentialsException.class,
            InternalAuthenticationServiceException.class
    })
    public ResultApiResponse.ErrorResponse handleBadCredentialsException(AuthenticationException e) {

        Error error = Error.BAD_CREDENTIALS;

        logError(error, e);

        return ResultApiResponse.ErrorResponse.build(error);
    }

    @ExceptionHandler(DisabledException.class)
    public ResultApiResponse.ErrorResponse handleDisabledException(DisabledException e) {

        Error error = Error.ACCOUNT_DISABLED;

        logError(error, e);

        return ResultApiResponse.ErrorResponse.build(error);
    }


    @ExceptionHandler(HttpClientErrorException.class)
    public ResultApiResponse.ErrorResponse handleHttpClientErrorException(HttpClientErrorException e) {

        Error error = Error.UPLOAD_FILE_FAILURE;

        logError(error, e);

        return ResultApiResponse.ErrorResponse.build(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResultApiResponse.ErrorResponse handleAuthenticationException(AuthenticationException e) {

        Error error = Error.UNAUTHENTICATED;

        logError(error, e);

        return ResultApiResponse.ErrorResponse.build(error);
    }


    @ExceptionHandler({
            MessagingException.class,
            IllegalArgumentException.class,
            FileAlreadyExistsException.class,
            UnsupportedOperationException.class
    })
    public ResultApiResponse.ErrorResponse handleIllegalArgumentException(Exception exception) {

        Error error = Error.BAD_REQUEST;

        logError(error, exception);

        return ResultApiResponse.ErrorResponse.build(error);
    }

    @ExceptionHandler({
            Exception.class,
            ServerException.class,
            DataIntegrityViolationException.class
    })
    public ResultApiResponse.ErrorResponse handleInternalException(Exception exception) {

        Error error = Error.SERVER_ERROR;

        logError(error, exception);

        return ResultApiResponse.ErrorResponse.build(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResultApiResponse.ErrorResponse handleAccessDeniedException(AccessDeniedException e) {

        Error error = Error.UNAUTHORIZED;

        logError(error, e);

        return ResultApiResponse.ErrorResponse.build(error);
    }

}
