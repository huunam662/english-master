package com.example.englishmaster_be.value;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class MicroserviceValue {

    @Value("${microservice.news.api-key}")
    private String API_KEY;

    @Value("${microservice.news.url}")
    private String URL;

    @Value("${microservice.news.prefix}")
    private String prefix;

    @Value("${microservice.news.endpoint.post.create}")
    private String createEndpoint;

    @Value("${microservice.news.endpoint.post.get-all}")
    private String getAllEndpoint;

    @Value("${microservice.news.endpoint.post.get-by-id}")
    private String getByIdEndpoint;

    @Value("${microservice.news.endpoint.post.update}")
    private String updateEndpoint;

    @Value("${microservice.news.endpoint.post.get-by-slug}")
    private String getBySlugEndpoint;

    @Value("${microservice.news.endpoint.post.get-by-post-category}")
    private String getByPostCategorySlugEndpoint;

    @Value("${microservice.news.endpoint.post.search-post}")
    private String searchEndpoint;

    @Value("${microservice.news.endpoint.post.delete}")
    private String deleteEndpoint;

}
