package com.example.englishmaster_be.common.dto.response;


import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.error.Error;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResultApiResponse {

    Boolean success;

    String message;

    String path;

    HttpStatus status;

    Integer code;

    Object responseData;

    @Builder.ObtainVia
    @Setter(AccessLevel.NONE)
    final Long timestamp = System.currentTimeMillis();

    @Getter
    @Setter
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ErrorResponse extends ResultApiResponse {

        Object errors;

        public static ErrorResponse build(Error error){

            return ErrorResponse.builder()
                    .status(error.getStatusCode())
                    .code(error.getStatusCode().value())
                    .message(error.getMessage())
                    .build();
        }

        public static ErrorResponse build(Error error, Object errors){

            return ErrorResponse.builder()
                    .status(error.getStatusCode())
                    .code(error.getStatusCode().value())
                    .message(error.getMessage())
                    .errors(errors)
                    .build();
        }

        public static ErrorResponse build(ErrorHolder errorHolder){

            Error error = errorHolder.getError();

            return ErrorResponse.builder()
                    .status(error.getStatusCode())
                    .code(error.getStatusCode().value())
                    .message(errorHolder.getToClient() ? errorHolder.getMessage() : error.getMessage())
                    .build();
        }

        public ErrorResponse(Exception ex, HttpServletRequest request, HttpServletResponse response, HttpStatus status){
            response.setStatus(status.value());
            this.setSuccess(false);
            this.setPath(request.getRequestURI());
            this.setStatus(status);
            this.setCode(status.value());
            this.setMessage(ex.getMessage());
            this.setErrors(Map.of(
                "field", ex.getCause().getMessage(),
                "message", ex.getMessage()
            ));
        }
    }

}
