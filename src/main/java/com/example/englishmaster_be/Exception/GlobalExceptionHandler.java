package com.example.englishmaster_be.Exception;

import com.example.englishmaster_be.Model.ResponseModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
}
