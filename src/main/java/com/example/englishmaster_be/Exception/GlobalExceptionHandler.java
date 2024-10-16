package com.example.englishmaster_be.Exception;

import com.example.englishmaster_be.Model.ResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
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
