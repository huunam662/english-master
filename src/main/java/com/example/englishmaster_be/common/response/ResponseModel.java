package com.example.englishmaster_be.common.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseModel {

    boolean success;

    String message;

    String violations;

    String path;

    HttpStatusCode status;

    int code;

    @Builder.Default
    @Setter(AccessLevel.NONE)
    long timestamp = System.currentTimeMillis();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Object responseData;

}
