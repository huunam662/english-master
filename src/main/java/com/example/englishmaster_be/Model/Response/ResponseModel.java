package com.example.englishmaster_be.Model.Response;


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

    String path;

    HttpStatus status;

    Object data;
    int code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String violations;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Object responseData;

    @Builder.ObtainVia
    @Setter(AccessLevel.NONE)
    final long timestamp = System.currentTimeMillis();

}
