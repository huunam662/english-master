package com.example.englishmaster_be.value;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MicroserviceValue {

    @Value("${microservice.news.api-key}")
    String API_KEY;

    @Value("${microservice.news.url}")
    String URL;

    @Value("${microservice.news.prefix}")
    String prefix;

    @Value("${microservice.news.endpoint.post.create}")
    String createEndpoint;

    @Value("${microservice.news.endpoint.post.get-all}")
    String getAllEndpoint;

    @Value("${microservice.news.endpoint.post.get-by-id}")
    String getByIdEndpoint;

    @Value("${microservice.news.endpoint.post.update}")
    String updateEndpoint;

    @Value("${microservice.news.endpoint.post.get-by-slug}")
    String getBySlugEndpoint;

    @Value("${microservice.news.endpoint.post.get-by-post-category}")
    String getByPostCategorySlugEndpoint;

    @Value("${microservice.news.endpoint.post.search-post}")
    String searchEndpoint;

    @Value("${microservice.news.endpoint.post.delete}")
    String deleteEndpoint;

}
