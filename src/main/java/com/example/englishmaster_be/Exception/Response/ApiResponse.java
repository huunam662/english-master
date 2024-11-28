package com.example.englishmaster_be.Exception.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiResponse<T>{

    boolean success;

    private Integer httpStatus;

    HttpStatusCode status;

    String message;

    int code;

    private T data;
    private List<String> errors;
    private String path;
    private Long timestamp;

}
