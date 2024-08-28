package com.example.englishmaster_be.Exception;

import com.example.englishmaster_be.Model.ResponseModel;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

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

//    @ExceptionHandler(value = MalformedJwtException.class)
//    ResponseEntity<ResponseModel> handlingMalformedJwtException(MalformedJwtException exception) {
//        Error error = Error.INVALID_TOKEN;
//        ResponseModel responseModel = new ResponseModel();
//        responseModel.setStatus("fail");
//        responseModel.setMessage(error.getMessage());
//        responseModel.setViolations(error.getViolation());
//        responseModel.setResponseData(null);
//        return ResponseEntity.status(error.getStatusCode()).body(responseModel);
//    }
//
//    @ExceptionHandler(value = ExpiredJwtException.class)
//    ResponseEntity<ResponseModel> handlingExpiredJwtException(ExpiredJwtException exception) {
//        Error error = Error.EXPIRED_TOKEN;  // Sử dụng enum Error.EXPIRED_TOKEN đã tạo
//        ResponseModel responseModel = new ResponseModel();
//        responseModel.setStatus("fail");
//        responseModel.setMessage(error.getMessage());
//        responseModel.setViolations(error.getViolation());
//        responseModel.setResponseData(null);
//        return ResponseEntity.status(error.getStatusCode()).body(responseModel);
//    }
//
//
//    @ExceptionHandler(value = UnsupportedJwtException.class)
//    ResponseEntity<ResponseModel> handlingUnsupportedJwtException(UnsupportedJwtException exception) {
//        Error error = Error.UNSUPPORTED_TOKEN;
//        ResponseModel responseModel = new ResponseModel();
//        responseModel.setStatus("fail");
//        responseModel.setMessage("JWT token is unsupported: " + exception.getMessage());
//        responseModel.setViolations(error.getViolation());
//        responseModel.setResponseData(null);
//        return ResponseEntity.status(error.getStatusCode()).body(responseModel);
//    }
//
//    @ExceptionHandler(value = IllegalArgumentException.class)
//    ResponseEntity<ResponseModel> handlingIllegalArgumentException(IllegalArgumentException exception) {
//        Error error = Error.UNAUTHENTICATED;
//        ResponseModel responseModel = new ResponseModel();
//        responseModel.setStatus("fail");
//        responseModel.setMessage("JWT claims string is empty: " + exception.getMessage());
//        responseModel.setViolations(error.getViolation());
//        responseModel.setResponseData(null);
//        return ResponseEntity.status(error.getStatusCode()).body(responseModel);
//    }

}
