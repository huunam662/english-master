package com.example.englishmaster_be.Exception.Response;

import org.springframework.http.HttpStatus;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
public class ResponseUtil {

    public static <T> ApiResponse <T> success(T data, String message,String path){
        return ApiResponse.<T>builder()
                .httpStatus(HttpStatus.OK.value())
                .success(true)
                .message(message)
                .data(data)
                .path(path)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static <T> ApiResponse <T>  error(Integer httpStatus,String message,List<String> errors,String path){
        return ApiResponse.<T>builder()
                .httpStatus(httpStatus)
                .success(false)
                .message(message)
                .errors(errors)
                .path(path)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static <T> ApiResponse <T> error(Integer httpStatus,String message,String errors,String path){
        return error(httpStatus,message, Collections.singletonList(errors),path);
    }
}