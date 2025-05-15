package com.example.englishmaster_be.domain.posts.service;

import com.example.englishmaster_be.domain.post_category.dto.request.PostCategoryRequest;
import com.example.englishmaster_be.domain.posts.dto.request.PostRequest;
import com.example.englishmaster_be.domain.posts.dto.request.FilterPostRequest;
import com.example.englishmaster_be.domain.posts.dto.request.SelectPostRequest;
import com.example.englishmaster_be.domain.posts.dto.request.UpdatePostRequest;
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
import org.springframework.web.util.UriComponentsBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostsService implements IPostsService {

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
            throw new RuntimeException("response body is null");
        }
        return response.getBody();
    }

    @Override
    public Object createPost(PostRequest dto) {
        String createUrl = microserviceValue.getURL() + microserviceValue.getPrefix() + microserviceValue.getCreateEndpoint();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", microserviceValue.getAPI_KEY());
        HttpEntity<PostRequest> requestEntity = new HttpEntity<>(dto, headers);
        Map<String, Object> response = getResponse(createUrl, HttpMethod.POST, requestEntity);
        if (response.containsKey("responseData")) {
            return response.get("responseData");
        }
        throw new RuntimeException("Failed to retrieve responseData from API response");
    }

    @Override
    public Object updatePost(UUID id, UpdatePostRequest dto) {
        String createUrl = microserviceValue.getURL() + microserviceValue.getPrefix() + microserviceValue.getUpdateEndpoint().replace("{id}", id.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", microserviceValue.getAPI_KEY());
        HttpEntity<UpdatePostRequest> requestEntity = new HttpEntity<>(dto, headers);
        Map<String, Object> response = getResponse(createUrl, HttpMethod.PATCH, requestEntity);
        if (response.containsKey("responseData")) {
            return response.get("responseData");
        }
        throw new RuntimeException("Failed to retrieve responseData from API response");
    }

    @Override
    public Object deletePost(UUID id) {
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

    @Override
    public Object getAllPosts(SelectPostRequest dto) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(microserviceValue.getURL() + microserviceValue.getPrefix() + microserviceValue.getGetAllEndpoint());
        String getAllUrl = parseToUrlDynamic(builder, dto);
        getAllUrl = getAllUrl.replace("%20", " ");
        HttpEntity<SelectPostRequest> requestEntity = new HttpEntity<>(new HttpHeaders());
        Map<String, Object> response = getResponse(getAllUrl, HttpMethod.GET, requestEntity);
        if (response.containsKey("responseData")) {
            return response.get("responseData");
        }
        throw new RuntimeException("Failed to retrieve responseData from API response");
    }

    @Override
    public Object getPostByPostCategorySlug(String slug, FilterPostRequest dto) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(microserviceValue.getURL() + microserviceValue.getPrefix() + microserviceValue.getGetByPostCategorySlugEndpoint().replace("{slug}", slug));
        String getByCateSlugUrl = parseToUrlDynamic(builder, dto);
        log.warn(getByCateSlugUrl);
        getByCateSlugUrl = getByCateSlugUrl.replace("%20", " ");
        HttpEntity<FilterPostRequest> requestEntity = new HttpEntity<>(new HttpHeaders());
        Map<String, Object> response = getResponse(getByCateSlugUrl, HttpMethod.GET, requestEntity);
        if (response.containsKey("responseData")) {
            return response.get("responseData");
        }
        throw new RuntimeException("Failed to retrieve responseData from API response");
    }

    @Override
    public Object getPostBySlug(String slug) {
        String getBySlugUrl = microserviceValue.getURL() + microserviceValue.getPrefix() + microserviceValue.getGetBySlugEndpoint().replace("{slug}", slug);
        Map<String, Object> response = getResponse(getBySlugUrl, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()));
        if (response.containsKey("responseData")) {
            if (response.get("responseData") == null) {
                throw new EntityNotFoundException("PostEntity not found");
            }
            return response.get("responseData");
        }
        throw new RuntimeException("Failed to retrieve responseData from API response");
    }

    @Override
    public Object searchPost(FilterPostRequest dto) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(microserviceValue.getURL() + microserviceValue.getPrefix() + microserviceValue.getSearchEndpoint());
        String searchUrl = parseToUrlDynamic(builder, dto);
        searchUrl = searchUrl.replace("%20", " ");
        HttpEntity<FilterPostRequest> requestEntity = new HttpEntity<>(dto, new HttpHeaders());
        Map<String, Object> response = getResponse(searchUrl, HttpMethod.GET, requestEntity);
        if (response.containsKey("responseData")) {
            return response.get("responseData");
        }
        throw new RuntimeException("Failed to retrieve responseData from API response");
    }


    @Override
    public Object getById(UUID id) {
        String getByIdUrl = microserviceValue.getURL() + microserviceValue.getPrefix() + microserviceValue.getGetByIdEndpoint().replace("{id}", id.toString());
        Map<String, Object> response = getResponse(getByIdUrl, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()));
        if (response.containsKey("responseData")) {
            return response.get("responseData");
        }
        throw new RuntimeException("Failed to retrieve responseData from API response");
    }

    private String parseToUrlDynamic(UriComponentsBuilder builder, Object dto) {
        Map<String, Object> params = new HashMap<>();
        if (dto instanceof FilterPostRequest filterDto) {
            params.put("status", filterDto.getStatus());
            params.put("excludeSlug", filterDto.getExcludeSlug());
            params.put("order", filterDto.getOrder());
            params.put("page", filterDto.getPage());
            params.put("take", filterDto.getTake());
            params.put("title", filterDto.getTitle());
            params.put("postCategorySlug", filterDto.getPostCategorySlug());
            params.put("minDate", filterDto.getMinDate());
            params.put("maxDate", filterDto.getMaxDate());
        } else if (dto instanceof SelectPostRequest selectDto) {
            params.put("status", selectDto.getStatus());
            params.put("excludeSlug", selectDto.getExcludeSlug());
            params.put("order", selectDto.getOrder());
            params.put("page", selectDto.getPage());
            params.put("take", selectDto.getTake());
        }
        return parseToUrl(builder, params);
    }

    private String parseToUrl(UriComponentsBuilder builder, Map<String, Object> params) {
        params.forEach((key, value) -> {
            if (value != null) {
                builder.queryParam(key, value);
            }
        });
        return builder.encode().toUriString().replace("%20", " ");
    }
}
