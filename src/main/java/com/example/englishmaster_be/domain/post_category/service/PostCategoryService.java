package com.example.englishmaster_be.domain.post_category.service;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.domain.post_category.dto.request.PostCategoryRequest;
import com.example.englishmaster_be.value.MicroserviceValue;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostCategoryService implements IPostCategoryService {

    RestTemplate restTemplate;

    MicroserviceValue microserviceValue;


    private Map<String, Object> getResponse(String url, HttpMethod method, HttpEntity<?> requestEntity) {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                method,
                requestEntity,
                new ParameterizedTypeReference<>() {
                }
        );
        if (response.getBody() == null) {
            throw new ErrorHolder(Error.SERVER_ERROR, "response body is null");
        }
        return response.getBody();
    }

    @Override
    public Object createPostCategory(PostCategoryRequest dto) {
        String createUrl = microserviceValue.getURL() + microserviceValue.getPrefix() + microserviceValue.getCreateEndpoint();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", microserviceValue.getAPI_KEY());
        HttpEntity<PostCategoryRequest> requestEntity = new HttpEntity<>(dto, headers);
        Map<String, Object> response = getResponse(createUrl, HttpMethod.POST, requestEntity);
        if (response.containsKey("responseData")) {
            return response.get("responseData");
        }
        throw new RuntimeException("Failed to retrieve responseData from API response");
    }

    @Override
    public Object getAllPostCategory() {
        String getAllUrl = microserviceValue.getURL() + microserviceValue.getPrefix() + microserviceValue.getGetAllEndpoint();
        HttpEntity<?> requestEntity = new HttpEntity<>(new HttpHeaders());
        Map<String, Object> response = getResponse(getAllUrl, HttpMethod.GET, requestEntity);
        if (response.containsKey("responseData")) {
            return response.get("responseData");
        }
        throw new RuntimeException("Failed to retrieve responseData from API response");
    }

    @Override
    public Object getPostCategoryById(UUID id) {
        String getByIdUrl = microserviceValue.getURL() + microserviceValue.getPrefix() + microserviceValue.getGetByIdEndpoint().replace("{id}", id.toString());
        HttpEntity<?> requestEntity = new HttpEntity<>(new HttpHeaders());
        Map<String, Object> response = getResponse(getByIdUrl, HttpMethod.GET, requestEntity);
        if (response.containsKey("responseData")) {
            if (response.get("responseData") == null) {
                throw new EntityNotFoundException("PostEntity category not found");
            }
            return response.get("responseData");
        }
        throw new RuntimeException("Failed to retrieve responseData from API response");
    }

    @Override
    public Object updatePostCategory(PostCategoryRequest dto) {
        String createUrl = microserviceValue.getURL() + microserviceValue.getPrefix() + microserviceValue.getUpdateEndpoint().replace("{id}", dto.getId().toString());
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", microserviceValue.getAPI_KEY());
        HttpEntity<PostCategoryRequest> requestEntity = new HttpEntity<>(dto, headers);
        Map<String, Object> response = getResponse(createUrl, HttpMethod.PATCH, requestEntity);
        if (response.containsKey("responseData")) {
            return response.get("responseData");
        }
        throw new RuntimeException("Failed to retrieve responseData from API response");
    }

    @Override
    public Object getPostCategoryBySlug(String slug) {
        String getByIdUrl = microserviceValue.getURL() + microserviceValue.getPrefix() + microserviceValue.getGetBySlugEndpoint().replace("{slug}", slug);
        HttpEntity<?> requestEntity = new HttpEntity<>(new HttpHeaders());
        Map<String, Object> response = getResponse(getByIdUrl, HttpMethod.GET, requestEntity);
        if (response.containsKey("responseData")) {
            if (response.get("responseData") == null) {
                throw new EntityNotFoundException("PostEntity category not found");
            }
            return response.get("responseData");
        }
        throw new RuntimeException("Failed to retrieve responseData from API response");
    }

    @Override
    public Object deletePostCategory(UUID id) {
        String deleteUrl = microserviceValue.getURL() + microserviceValue.getPrefix() + microserviceValue.getDeleteEndpoint().replace("{id}", id.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", microserviceValue.getAPI_KEY());
        HttpEntity<PostCategoryRequest> requestEntity = new HttpEntity<>(headers);
        Map<String, Object> response = getResponse(deleteUrl, HttpMethod.DELETE, requestEntity);
        if (response.containsKey("responseData")) {
            return response.get("responseData");
        }
        throw new RuntimeException("Failed to retrieve responseData from API response");
    }

}
