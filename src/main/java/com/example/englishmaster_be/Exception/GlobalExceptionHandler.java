package com.example.englishmaster_be.Exception;

import com.example.englishmaster_be.Exception.Response.BadRequestException;
import com.example.englishmaster_be.Exception.Response.ResourceNotFoundException;
import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.FileAlreadyExistsException;
import java.rmi.ServerException;
import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GlobalExceptionHandler {


    @ExceptionHandler(CustomException.class)
    public ExceptionResponseModel handleCustomException(CustomException e) {

        Error error = e.getError();

        return ExceptionResponseModel.builder()
                .success(Boolean.FALSE)
                .status(error.getStatusCode())
                .code(error.getStatusCode().value())
                .message(error.getMessage())
                .violations(error.getViolation())
                .build();
    }


    @ExceptionHandler({
            ResourceNotFoundException.class,
            NoSuchElementException.class
    })
    public ExceptionResponseModel handlingResourceNotFoundException(ResourceNotFoundException ignored){

        String message = "Resource not found";

        if(ignored.getMessage() != null || ignored.getMessage().isEmpty())
            message = ignored.getMessage();

        return ExceptionResponseModel.builder()
                .success(Boolean.FALSE)
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
                .success(Boolean.FALSE)
                .status(HttpStatus.BAD_REQUEST)
                .code(HttpStatus.BAD_REQUEST.value())
                .message(message)
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
                .success(Boolean.FALSE)
                .status(HttpStatus.UNAUTHORIZED)
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(message)
                .violations("")
                .build();
    }


    @ExceptionHandler(HttpClientErrorException.class)
    public ExceptionResponseModel handleHttpClientErrorException(HttpClientErrorException ignored) {

        Error error = Error.UPLOAD_FILE_FAILURE;

        return ExceptionResponseModel.builder()
                .success(Boolean.FALSE)
                .status(error.getStatusCode())
                .code(error.getStatusCode().value())
                .message(error.getMessage())
                .violations(error.getViolation())
                .build();
    }


    @ExceptionHandler({
        MessagingException.class,
        BadRequestException.class,
        IllegalArgumentException.class,
        FileAlreadyExistsException.class
    })
    public ExceptionResponseModel handleIllegalArgumentException(Exception exception) {

        return ExceptionResponseModel.builder()
                .success(Boolean.FALSE)
                .status(HttpStatus.BAD_REQUEST)
                .code(HttpStatus.BAD_REQUEST.value())
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ExceptionResponseModel handleConflictException(Exception exception) {

        return ExceptionResponseModel.builder()
                .success(Boolean.FALSE)
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
                .success(Boolean.FALSE)
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(exception.getMessage())
                .violations("")
                .build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ExceptionResponseModel handleAccessDeniedException(AccessDeniedException exception) {

        Error error = Error.UNAUTHORIZED;

        return ExceptionResponseModel.builder()
                .success(false)
                .status(error.getStatusCode())
                .code(error.getStatusCode().value())
                .message(error.getMessage())
                .build();
    }


}
