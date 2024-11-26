package com.example.englishmaster_be.exception.handler;

import com.example.englishmaster_be.exception.custom.CustomException;
import com.example.englishmaster_be.exception.template.BadRequestException;
import com.example.englishmaster_be.exception.template.ResourceNotFoundException;
import com.example.englishmaster_be.common.response.ExceptionResponseModel;
import com.example.englishmaster_be.exception.enums.Error;
import com.google.api.Http;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.rmi.ServerException;
import java.util.List;

@RestControllerAdvice
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ExceptionResponseModel handleCustomException(CustomException e) {

        Error error = e.getError();

        return ExceptionResponseModel.builder()
                .success(false)
                .status(error.getStatusCode())
                .code(error.getStatusCode().value())
                .message(error.getMessage())
                .violations(error.getViolation())
                .build();
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    public ExceptionResponseModel handlingResourceNotFoundException(ResourceNotFoundException ignored){

        String message = "Resource not found";

        return ExceptionResponseModel.builder()
                .success(false)
                .status(HttpStatus.NOT_FOUND)
                .code(HttpStatus.NOT_FOUND.value())
                .message(message)
                .violations("")
                .build();
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ExceptionResponseModel handleValidationExceptions(MethodArgumentNotValidException exception){

        String message = "Validation failed";

        List<String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        return ExceptionResponseModel.builder()
                .success(false)
                .status(HttpStatus.BAD_REQUEST)
                .code(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .violations("")
                .errors(errors)
                .build();
    }

    @ExceptionHandler({
            UsernameNotFoundException.class,
            BadCredentialsException.class
    })
    public ExceptionResponseModel handleBadCredentialsException(AuthenticationException ignored) {

        String message = "Wrong username or password";

        return ExceptionResponseModel.builder()
                .success(false)
                .status(HttpStatus.UNAUTHORIZED)
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(message)
                .violations("")
                .build();
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ExceptionResponseModel handlingAccessDeniedException(AccessDeniedException ignored) {

        Error error = Error.UNAUTHORIZED;

        return ExceptionResponseModel.builder()
                .success(false)
                .status(error.getStatusCode())
                .code(error.getStatusCode().value())
                .message(error.getMessage())
                .violations(error.getViolation())
                .build();
    }


    @ExceptionHandler(HttpClientErrorException.class)
    public ExceptionResponseModel handleHttpClientErrorException(HttpClientErrorException ignored) {

        Error error = Error.UPLOAD_FILE_FAILURE;

        return ExceptionResponseModel.builder()
                .success(false)
                .status(error.getStatusCode())
                .code(error.getStatusCode().value())
                .message(error.getMessage())
                .violations(error.getViolation())
                .build();
    }


    @ExceptionHandler({
        MessagingException.class,
        BadRequestException.class,
        IllegalArgumentException.class
    })
    public ExceptionResponseModel handleIllegalArgumentException(Exception exception) {

        return ExceptionResponseModel.builder()
                .success(false)
                .status(HttpStatus.BAD_REQUEST)
                .code(HttpStatus.BAD_REQUEST.value())
                .message(exception.getMessage())
                .violations("")
                .build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ExceptionResponseModel handleConflictException(Exception exception) {

        return ExceptionResponseModel.builder()
                .success(false)
                .status(HttpStatus.CONFLICT)
                .code(HttpStatus.CONFLICT.value())
                .message(exception.getMessage())
                .violations("")
                .build();
    }


    @ExceptionHandler({
            Exception.class,
            ServerException.class
    })
    public ExceptionResponseModel handleInternalException(Exception exception) {

        return ExceptionResponseModel.builder()
                .success(false)
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(exception.getMessage())
                .violations("")
                .build();
    }



}
