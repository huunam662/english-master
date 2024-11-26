package com.example.englishmaster_be.Exception;

import com.example.englishmaster_be.Exception.Response.ApiResponse;
import com.example.englishmaster_be.Exception.Response.ResourceNotFoundException;
import com.example.englishmaster_be.Exception.Response.ResponseUtil;
import com.example.englishmaster_be.Model.ResponseModel;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = CustomException.class)
    ResponseEntity<ResponseModel> handleCustomException(CustomException e) {
        Error error = e.getError();
        ResponseModel responseModel = new ResponseModel();
        responseModel.setStatus("fail");
        responseModel.setMessage(error.getMessage());
        responseModel.setViolations(error.getViolation());
        responseModel.setResponseData(null);
        return ResponseEntity.status(error.getStatusCode()).body(responseModel);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    ApiResponse<Object> handlingResourceNotFoundException(ResourceNotFoundException exception, HttpServletRequest request){
        return ResponseUtil.error(404, "Resource not found",exception.getMessage(),request.getRequestURI());
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ApiResponse<Object> handleValidationExceptions(MethodArgumentNotValidException exception,HttpServletRequest request){

        List<String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());

        return ResponseUtil.error(HttpStatus.BAD_REQUEST.value(),
                "Validation failed", errors, request.getRequestURI());
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ResponseModel> handlingAccessDeniedException(AccessDeniedException exception) {
        Error error = Error.UNAUTHORIZED;
        ResponseModel responseModel = new ResponseModel();
        responseModel.setStatus("fail");
        responseModel.setMessage(error.getMessage());
        responseModel.setViolations(error.getViolation());
        responseModel.setResponseData(null);
        return ResponseEntity.status(error.getStatusCode()).body(responseModel);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    ResponseEntity<ResponseModel> handleHttpClientErrorException(HttpClientErrorException exception) {
        Error error = Error.UPLOAD_FILE_FAILURE;
        ResponseModel responseModel = new ResponseModel();
        responseModel.setStatus("fail");
        responseModel.setMessage(error.getMessage());
        responseModel.setViolations(error.getViolation());
        responseModel.setResponseData(null);
        return ResponseEntity.status(error.getStatusCode()).body(responseModel);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ResponseModel> handleIllegalArgumentException(IllegalArgumentException exception) {
        ResponseModel responseModel = new ResponseModel();
        responseModel.setStatus("fail");
        responseModel.setMessage(exception.getMessage());
        responseModel.setViolations(exception.getMessage());
        responseModel.setResponseData(null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseModel);
    }

}
