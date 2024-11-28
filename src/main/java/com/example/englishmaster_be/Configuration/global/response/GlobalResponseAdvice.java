
package com.example.englishmaster_be.Configuration.global.response;

import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.Configuration.global.thread.MessageResponseHolder;
import com.example.englishmaster_be.Exception.Response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    HttpServletRequest httpServletRequest;

    @Override
    public boolean supports(
            @NonNull MethodParameter returnType,
            @NonNull Class<? extends HttpMessageConverter<?>> converterType
    ) {

        // (1) -> Nhận biết hướng đi của request và vị trí của Response handler
        String requestURI = httpServletRequest.getRequestURI();
        String packageName = returnType.getContainingClass().getPackageName();
        Class<?> declaringClass = returnType.getDeclaringClass();

        System.out.println("------- supports ResponseBodyAdvice -----------");
        System.out.println("requestURI: " + requestURI);
        System.out.println("packageName: " + packageName);
        System.out.println("declaringClass: " + declaringClass);
        System.out.println("------- supports ResponseBodyAdvice -----------");
        // -> end (1)

        // -> Cho phép beforeBodyWrite nhận xử lý nếu thỏa điều kiện dưới đây
        return !requestURI.contains("/api-docs")
                && !packageName.contains("org.springdoc.webmvc.ui")
                && !declaringClass.getPackageName().contains("org.springdoc.webmvc.api")
                && (
                    returnType.getMethodAnnotation(MessageResponse.class) != null
                    ||
                    returnType.getParameterType().equals(ResponseModel.class)
                    ||
                    returnType.getParameterType().equals(ExceptionResponseModel.class)
                    ||
                    returnType.getParameterType().equals(ApiResponse.class)
                    ||
                    returnType.getParameterType().equals(ResponseEntity.class)
                );
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            @NonNull MethodParameter returnType,
            @NonNull MediaType selectedContentType,
            @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response
    ) {

        System.out.println("------- ResponseBodyAdvice -----------");
        System.out.println("beforeBodyWrite");
        System.out.println(returnType.getContainingClass().getPackageName());
        System.out.println(body.getClass().getSimpleName());
        System.out.println("------- ResponseBodyAdvice -----------");

        if(body instanceof ExceptionResponseModel exceptionResponseModel) {

            response.setStatusCode(exceptionResponseModel.getStatus());
            exceptionResponseModel.setPath(request.getURI().toString());
        }
        else {

            response.setStatusCode(HttpStatus.OK);

            if(body instanceof ResponseModel responseModel){
                responseModel.setSuccess(true);
                responseModel.setStatus(HttpStatus.OK);
                responseModel.setCode(HttpStatus.OK.value());
                responseModel.setPath(request.getURI().toString());
            }
            else if(body instanceof ApiResponse<?> apiResponse){
                apiResponse.setSuccess(true);
                apiResponse.setStatus(HttpStatus.OK);
                apiResponse.setCode(HttpStatus.OK.value());
                apiResponse.setPath(request.getURI().toString());
            }
            else if(body instanceof ResponseEntity<?> responseEntity){
                Object bodyEntity = responseEntity.getBody();

                if(bodyEntity instanceof ExceptionResponseModel exceptionResponseModel){
                    response.setStatusCode(exceptionResponseModel.getStatus());
                    exceptionResponseModel.setCode(exceptionResponseModel.getStatus().value());
                    exceptionResponseModel.setPath(request.getURI().toString());
                    exceptionResponseModel.setSuccess(Boolean.FALSE);
                }
                else if(bodyEntity instanceof ResponseModel responseModel){
                    responseModel.setSuccess(true);
                    responseModel.setStatus(HttpStatus.OK);
                    responseModel.setCode(HttpStatus.OK.value());
                    responseModel.setPath(request.getURI().toString());
                }

                body = bodyEntity;
            }
            else body = ResponseModel.builder()
                    .success(true)
                    .status(HttpStatus.OK)
                    .code(HttpStatus.OK.value())
                    .message(MessageResponseHolder.getMessage())
                    .path(request.getURI().toString())
                    .responseData(body)
                    .build();
        }
        return body;
    }
}
