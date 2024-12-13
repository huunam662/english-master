package com.example.englishmaster_be.Exception.handler;

import com.example.englishmaster_be.Common.enums.error.ErrorEnum;
import com.example.englishmaster_be.Exception.template.BadRequestException;
import com.example.englishmaster_be.Exception.template.CustomException;
import com.example.englishmaster_be.Exception.template.RefreshTokenException;
import com.example.englishmaster_be.Exception.template.ResourceNotFoundException;
import com.example.englishmaster_be.Common.dto.response.ExceptionResponseModel;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.file.FileAlreadyExistsException;
import java.rmi.ServerException;
import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GlobalExceptionHandler {


    @ExceptionHandler(CustomException.class)
    public ExceptionResponseModel handleCustomException(CustomException e) {

        ErrorEnum error = e.getError();

        return ExceptionResponseModel.builder()
                .status(error.getStatusCode())
                .code(error.getStatusCode().value())
                .message(error.getMessage())
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
                .status(HttpStatus.NOT_FOUND)
                .code(HttpStatus.NOT_FOUND.value())
                .message(message)
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
                .status(HttpStatus.UNAUTHORIZED)
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(message)
                .build();
    }

    @ExceptionHandler(DisabledException.class)
    public ExceptionResponseModel handleDisabledException(DisabledException ignored) {

        ErrorEnum error = ErrorEnum.ACCOUNT_DISABLED;

        return ExceptionResponseModel.builder()
                .status(error.getStatusCode())
                .code(error.getStatusCode().value())
                .message(error.getMessage())
                .build();
    }


    @ExceptionHandler(HttpClientErrorException.class)
    public ExceptionResponseModel handleHttpClientErrorException(HttpClientErrorException ignored) {

        ErrorEnum error = ErrorEnum.UPLOAD_FILE_FAILURE;

        return ExceptionResponseModel.builder()
                .status(error.getStatusCode())
                .code(error.getStatusCode().value())
                .message(error.getMessage())
                .build();
    }

    @ExceptionHandler(AuthenticationException.class)
    public ExceptionResponseModel handleAuthenticationException(AuthenticationException e) {

        return ExceptionResponseModel.builder()
                .status(HttpStatus.UNAUTHORIZED)
                .code(HttpStatus.UNAUTHORIZED.value())
                .message(e.getMessage())
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
                .status(HttpStatus.BAD_REQUEST)
                .code(HttpStatus.BAD_REQUEST.value())
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ExceptionResponseModel handleConflictException(Exception exception) {

        return ExceptionResponseModel.builder()
                .status(HttpStatus.CONFLICT)
                .code(HttpStatus.CONFLICT.value())
                .message(exception.getMessage())
                .build();
    }


    @ExceptionHandler({
            Exception.class,
            ServerException.class
    })
    public ExceptionResponseModel handleInternalException(Exception exception) {

        return ExceptionResponseModel.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ExceptionResponseModel handleAccessDeniedException(AccessDeniedException exception) {

        ErrorEnum error = ErrorEnum.UNAUTHORIZED;

        return ExceptionResponseModel.builder()
                .status(error.getStatusCode())
                .code(error.getStatusCode().value())
                .message(error.getMessage())
                .build();
    }

    @ExceptionHandler(RefreshTokenException.class)
    public ExceptionResponseModel handleTokenRefreshException(RefreshTokenException ex) {

        return ExceptionResponseModel.builder()
                .message(ex.getMessage())
                .status(HttpStatus.FORBIDDEN)
                .code(HttpStatus.FORBIDDEN.value())
                .build();
    }

}
