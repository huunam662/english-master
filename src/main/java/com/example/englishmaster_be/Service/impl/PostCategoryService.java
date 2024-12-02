package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.DTO.PostCategory.CreatePostCategoryDto;
import com.example.englishmaster_be.DTO.PostCategory.UpdatePostCategoryDto;
import com.example.englishmaster_be.Service.IPostCategoryService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Service
public class PostCategoryService implements IPostCategoryService {

    private static final Logger log = LoggerFactory.getLogger(PostCategoryService.class);
    private final RestTemplate restTemplate;

    @Value("${microservice.news.api-key}")
    private String API_KEY;

    @Value("${microservice.news.url}")
    private String URL;

    @Value("${microservice.news.prefix}")
    private String prefix;

    @Value("${microservice.news.endpoint.post-category.create}")
    private String createEndpoint;

    @Value("${microservice.news.endpoint.post-category.get-all}")
    private String getAllEndpoint;

    @Value("${microservice.news.endpoint.post-category.get-by-id}")
    private String getByIdEndpoint;

    @Value("${microservice.news.endpoint.post-category.update}")
    private String updateEndpoint;

    @Value("${microservice.news.endpoint.post-category.get-by-slug}")
    private String getBySlugEndpoint;

    @Value("${microservice.news.endpoint.post-category.delete}")
    private String deleteEndpoint;

    public PostCategoryService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private Map<String, Object> getResponse(String url, HttpMethod method, HttpEntity<?> requestEntity) {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                method,
                requestEntity,
                new ParameterizedTypeReference<>() {
                }
        );
        if (response.getBody() == null) {
            throw new RuntimeException("response body is null");
        }
        return response.getBody();
    }

    @Override
    public Object createPostCategory(CreatePostCategoryDto dto) {
        String createUrl = URL + prefix + createEndpoint;
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", API_KEY);
        HttpEntity<CreatePostCategoryDto> requestEntity = new HttpEntity<>(dto, headers);
        Map<String, Object> response = getResponse(createUrl, HttpMethod.POST, requestEntity);
        if (response.containsKey("responseData")) {
            return response.get("responseData");
        }
        throw new RuntimeException("Failed to retrieve responseData from API response");
    }

    @Override
    public Object getAllPostCategory() {
        String getAllUrl = URL + prefix + getAllEndpoint;
        HttpEntity<?> requestEntity = new HttpEntity<>(new HttpHeaders());
        Map<String, Object> response = getResponse(getAllUrl, HttpMethod.GET, requestEntity);
        if (response.containsKey("responseData")) {
            return response.get("responseData");
        }
        throw new RuntimeException("Failed to retrieve responseData from API response");
    }

    @Override
    public Object getPostCategoryById(UUID id) {
        String getByIdUrl = URL + prefix + getByIdEndpoint.replace("{id}", id.toString());
        HttpEntity<?> requestEntity = new HttpEntity<>(new HttpHeaders());
        Map<String, Object> response = getResponse(getByIdUrl, HttpMethod.GET, requestEntity);
        if (response.containsKey("responseData")) {
            if (response.get("responseData") == null) {
                throw new EntityNotFoundException("Post category not found");
            }
            return response.get("responseData");
        }
        throw new RuntimeException("Failed to retrieve responseData from API response");
    }

    @Override
    public Object updatePostCategory(UUID id, UpdatePostCategoryDto dto) {
        String createUrl = URL + prefix + updateEndpoint.replace("{id}", id.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", API_KEY);
        HttpEntity<CreatePostCategoryDto> requestEntity = new HttpEntity<>(dto, headers);
        Map<String, Object> response = getResponse(createUrl, HttpMethod.PATCH, requestEntity);
        if (response.containsKey("responseData")) {
            return response.get("responseData");
        }
        throw new RuntimeException("Failed to retrieve responseData from API response");
    }

    @Override
    public Object getPostCategoryBySlug(String slug) {
        String getByIdUrl = URL + prefix + getBySlugEndpoint.replace("{slug}", slug);
        HttpEntity<?> requestEntity = new HttpEntity<>(new HttpHeaders());
        Map<String, Object> response = getResponse(getByIdUrl, HttpMethod.GET, requestEntity);
        if (response.containsKey("responseData")) {
            if (response.get("responseData") == null) {
                throw new EntityNotFoundException("Post category not found");
            }
            return response.get("responseData");
        }
        throw new RuntimeException("Failed to retrieve responseData from API response");
    }

    @Override
    public Object deletePostCategory(UUID id) {
        String deleteUrl = URL + prefix + deleteEndpoint.replace("{id}", id.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", API_KEY);
        HttpEntity<CreatePostCategoryDto> requestEntity = new HttpEntity<>(headers);
        Map<String, Object> response = getResponse(deleteUrl, HttpMethod.DELETE, requestEntity);
        if (response.containsKey("responseData")) {
            return response.get("responseData");
        }
        throw new RuntimeException("Failed to retrieve responseData from API response");
    }

}
