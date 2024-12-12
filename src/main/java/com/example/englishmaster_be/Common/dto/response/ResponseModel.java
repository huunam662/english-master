package com.example.englishmaster_be.Common.dto.response;


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
public class ResponseModel {

    Boolean success;

    String message;

    String path;

    HttpStatus status;

    Integer code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String violations;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Object responseData;

    @Builder.ObtainVia
    @Setter(AccessLevel.NONE)
    final Long timestamp = System.currentTimeMillis();

}
