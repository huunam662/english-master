package com.example.englishmaster_be.shared.dto.response;


import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.error.Error;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

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

    @JsonInclude(JsonInclude.Include.NON_NULL)
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

        @JsonInclude(JsonInclude.Include.NON_NULL)
        Object errors = null;

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
                    .message(errorHolder.getMessage())
                    .build();
        }

    }

}
