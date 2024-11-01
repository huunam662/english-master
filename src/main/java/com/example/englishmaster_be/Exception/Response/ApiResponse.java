package com.example.englishmaster_be.Exception.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiResponse<T>{

    private Integer httpStatus;
    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
    private String path;
    private Long timestamp;

}
