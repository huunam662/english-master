
package com.example.englishmaster_be.advice.response;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.shared.dto.response.FilterResponse;
import com.example.englishmaster_be.shared.dto.response.ResultApiResponse;

import com.example.englishmaster_be.domain.file_storage.dto.response.ResourceResponse;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.xml.transform.Result;
import java.io.IOException;

@Slf4j(topic = "GLOBAL-OBJECT-RESPONSE-WRAPPER")
@RestControllerAdvice
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GlobalObjectResponseWrapper implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(
            @NonNull MethodParameter returnType,
            @NonNull Class<? extends HttpMessageConverter<?>> MapperType
    ) {

        // (1) -> Nhận biết hướng đi của request và vị trí của handler response
        Class<?> declaringClass = returnType.getDeclaringClass();
        String packageName = declaringClass.getPackageName();
        Class<?> objectReturnType = returnType.getParameterType();
        // -> end (1)

        log.info("Package: {}", packageName);
        log.info("declaringClass: {}", declaringClass);

        // -> Cho phép beforeBodyWrite nhận xử lý nếu thỏa điều kiện dưới đây
        return !packageName.contains("springdoc") &&
                !packageName.contains("swagger") &&
                (
                        declaringClass.getAnnotation(RestController.class) != null ||
                                objectReturnType.equals(ResultApiResponse.ErrorResponse.class)
                ) && !objectReturnType.equals(byte[].class) &&
                !objectReturnType.equals(byte.class);
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            @NonNull MethodParameter returnType,
            @NonNull MediaType selectedContentType,
            @NonNull Class<? extends HttpMessageConverter<?>> selectedMapperType,
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response
    ) {

        if(body instanceof ResultApiResponse.ErrorResponse exceptionResponseModel) {

            response.setStatusCode(exceptionResponseModel.getStatus());

            exceptionResponseModel.setSuccess(Boolean.FALSE);
            exceptionResponseModel.setPath(request.getURI().getPath());

            return exceptionResponseModel;
        }

        response.setStatusCode(HttpStatus.OK);

        if(body instanceof ResourceResponse resourceResponse) {

            String typeLoad = resourceResponse.getTypeLoad().name().toLowerCase();

            response.getHeaders().set(HttpHeaders.CONTENT_TYPE, resourceResponse.getContentType());
            response.getHeaders().set(HttpHeaders.CONTENT_DISPOSITION, typeLoad + "; filename=\"" + resourceResponse.getFileName() + "\"");
            response.getHeaders().setContentLength(resourceResponse.getContentLength());

            try {
                return resourceResponse.getResource().getInputStream().readAllBytes();
            } catch (IOException e) {
                throw new ErrorHolder(Error.SERVER_ERROR);
            }
        }

        if(body instanceof FilterResponse<?> filterResponse){

            if(filterResponse.getContent() != null)
                filterResponse.setContentLength(filterResponse.getContent().size());

            filterResponse.withPreviousAndNextPage();
        }

        DefaultMessage defaultMessage = returnType.getMethodAnnotation(DefaultMessage.class);

        log.info("Result api response with message: {}", defaultMessage);

        return ResultApiResponse.builder()
                .success(Boolean.TRUE)
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .message(defaultMessage != null ? defaultMessage.value() : "")
                .path(request.getURI().getPath())
                .responseData(body)
                .build();
    }
}
