package com.example.englishmaster_be.domain.news.controller;

import com.example.englishmaster_be.domain.news.dto.request.UpdateNewsRequest;
import com.example.englishmaster_be.common.dto.response.FilterResponse;

import com.example.englishmaster_be.domain.news.service.INewsService;
import com.example.englishmaster_be.domain.news.mapper.NewsMapper;
import com.example.englishmaster_be.domain.news.dto.request.NewsFilterRequest;
import com.example.englishmaster_be.domain.news.dto.request.CreateNewsRequest;
import com.example.englishmaster_be.domain.news.dto.response.NewsResponse;
import com.example.englishmaster_be.domain.news.model.NewsEntity;
import com.example.englishmaster_be.common.dto.response.ResourceKeyResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "News")
@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewsController {

    INewsService newsService;


    @GetMapping(value = "/listNewsAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public FilterResponse<?> listNewsOfAdmin(
            @RequestParam(value = "page", defaultValue = "1") @Min(1) Integer page,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) Integer size,
            @RequestParam(value = "sortBy", defaultValue = "updateAt") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction sortDirection,
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(value = "enable", defaultValue = "true") boolean isEnable
    ){

        NewsFilterRequest filterRequest = NewsFilterRequest.builder()
                .page(page)
                .search(search)
                .pageSize(size)
                .sortBy(sortBy)
                .isEnable(isEnable)
                .sortDirection(sortDirection)
                .build();

        return newsService.listNewsOfAdmin(filterRequest);
    }


    @GetMapping(value = "/listNewsUser")
    public List<NewsResponse> listNewsOfUser(
            @RequestParam(value = "size", defaultValue = "5") @Min(1) @Max(100) Integer size
    ){

        NewsFilterRequest newsFilterRequest = NewsFilterRequest.builder()
                .pageSize(size)
                .build();

        List<NewsEntity> newsEntityList = newsService.listNewsOfUser(newsFilterRequest);

        return NewsMapper.INSTANCE.toNewsResponseList(newsEntityList);
    }

    @GetMapping("/{newId}")
    public NewsResponse getNewsById(@PathVariable("newId") UUID newId){

        NewsEntity newResult = newsService.getNewsById(newId);

        return NewsMapper.INSTANCE.toNewsResponse(newResult);
    }

    @PostMapping(value = "/createNews")
    @PreAuthorize("hasRole('ADMIN')")
    public ResourceKeyResponse createNews(
            @RequestBody CreateNewsRequest newsRequest
    ){

        return newsService.createNews(newsRequest);
    }


    @PutMapping(value = "/updateNews")
    @PreAuthorize("hasRole('ADMIN')")
    public ResourceKeyResponse updateNews(
            @RequestBody UpdateNewsRequest newsRequest
    ){

        return newsService.updateNews(newsRequest);
    }


    @PatchMapping (value = "/{newsId:.+}/enableNews")
    @PreAuthorize("hasRole('ADMIN')")
    public void enableNews(@PathVariable("newsId") UUID newsId, @RequestParam("enable") boolean enable){

        newsService.enableNews(newsId, enable);
    }


    @DeleteMapping(value = "/{newsId:.+}/deleteNews")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteNews(@PathVariable("newsId") UUID newsId){

        newsService.deleteNews(newsId);
    }


    @GetMapping(value="/searchByTitle")
    @PreAuthorize("hasRole('ADMIN')")
    public List<NewsResponse> searchByTitle(@RequestParam("title") String title) {

        List<NewsEntity> newsEntityList = newsService.searchByTitle(title);

        return NewsMapper.INSTANCE.toNewsResponseList(newsEntityList);
    }

}
