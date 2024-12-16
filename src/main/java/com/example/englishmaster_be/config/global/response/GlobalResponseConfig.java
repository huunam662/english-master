
package com.example.englishmaster_be.config.global.response;

import com.example.englishmaster_be.common.dto.response.ExceptionResponseModel;
import com.example.englishmaster_be.common.dto.response.FilterResponse;
import com.example.englishmaster_be.common.dto.response.ResponseModel;
import com.example.englishmaster_be.common.thread.MessageResponseHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GlobalResponseConfig implements ResponseBodyAdvice<Object> {

    HttpServletRequest httpServletRequest;

    @Override
    public boolean supports(
            @NonNull MethodParameter returnType,
            @NonNull Class<? extends HttpMessageConverter<?>> converterType
    ) {

        // (1) -> Nhận biết hướng đi của request và vị trí của handler response
        String requestURI = httpServletRequest.getRequestURI();
        String packageName = returnType.getContainingClass().getPackageName();
        Class<?> declaringClass = returnType.getDeclaringClass();

        System.out.println("------- supports ResponseBodyAdvice -----------");
        System.out.println("requestURI: " + requestURI);
        System.out.println("packageName: " + packageName);
        System.out.println("declaringClass: " + declaringClass);
        System.out.println("annotation declaringClass: " + declaringClass.getAnnotation(RestController.class));
        System.out.println("------- supports ResponseBodyAdvice -----------");
        // -> end (1)

        // -> Cho phép beforeBodyWrite nhận xử lý nếu thỏa điều kiện dưới đây
        return !requestURI.contains("/v3/api-docs")
                && !packageName.contains("org.springdoc.webmvc")
                && !declaringClass.getPackageName().contains("org.springdoc.webmvc")
                && (
                    declaringClass.getAnnotation(RestController.class) != null
                    ||
                    returnType.getParameterType().equals(ResponseModel.class)
                    ||
                    returnType.getParameterType().equals(ExceptionResponseModel.class)
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
        if(body != null) System.out.println(body.getClass().getSimpleName());
        System.out.println(returnType.getMethod());
        System.out.println("------- ResponseBodyAdvice -----------");


        if(body instanceof ExceptionResponseModel exceptionResponseModel) {

            response.setStatusCode(exceptionResponseModel.getStatus());

            exceptionResponseModel.setSuccess(Boolean.FALSE);
            exceptionResponseModel.setPath(request.getURI().getPath());

            return exceptionResponseModel;
        }

        response.setStatusCode(HttpStatus.OK);

        if(body instanceof Resource) return body;

        if(body instanceof ResponseModel responseModel)
            body = responseModel.getResponseData();

        else if(body instanceof ResponseEntity<?> responseEntity)
            body = responseEntity.getBody();

        else if(body instanceof FilterResponse<?> filterResponse){

            if(filterResponse.getContent() != null)
                filterResponse.setContentLength(filterResponse.getContent().size());

            filterResponse.withPreviousAndNextPage();
        }

        return ResponseModel.builder()
                .success(Boolean.TRUE)
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .message(MessageResponseHolder.getMessage())
                .path(request.getURI().getPath())
                .responseData(body)
                .build();
    }
}
