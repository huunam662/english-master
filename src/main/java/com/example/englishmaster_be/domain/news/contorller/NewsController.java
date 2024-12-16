package com.example.englishmaster_be.domain.news.contorller;

import com.example.englishmaster_be.common.annotation.DefaultMessage;
import com.example.englishmaster_be.common.dto.response.FilterResponse;

import com.example.englishmaster_be.domain.news.service.INewsService;
import com.example.englishmaster_be.mapper.NewsMapper;
import com.example.englishmaster_be.domain.news.dto.request.NewsFilterRequest;
import com.example.englishmaster_be.domain.news.dto.request.NewsRequest;
import com.example.englishmaster_be.domain.news.dto.response.NewsResponse;
import com.example.englishmaster_be.model.news.NewsEntity;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
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
    @DefaultMessage("List News successfully")
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
    @DefaultMessage("List NewsEntity successfully")
    public List<NewsResponse> listNewsOfUser(
            @RequestParam(value = "size", defaultValue = "5") @Min(1) @Max(100) Integer size
    ){

        NewsFilterRequest newsFilterRequest = NewsFilterRequest.builder()
                .pageSize(size)
                .build();

        List<NewsEntity> newsEntityList = newsService.listNewsOfUser(newsFilterRequest);

        return NewsMapper.INSTANCE.toNewsResponseList(newsEntityList);
    }


    @PostMapping(value = "/createNews" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Create NewsEntity successfully")
    public NewsResponse createNews(
            @ModelAttribute("contentNews") NewsRequest newsRequest
    ){

        NewsEntity news = newsService.saveNews(newsRequest);

        return NewsMapper.INSTANCE.toNewsResponse(news);
    }


    @PatchMapping(value = "/{newsId:.+}/updateNews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Update NewsEntity successfully")
    public NewsResponse updateNews(
            @PathVariable UUID newsId,
            @ModelAttribute("contentNews") NewsRequest newsRequest
    ){

        newsRequest.setNewsId(newsId);

        NewsEntity news = newsService.saveNews(newsRequest);

        return NewsMapper.INSTANCE.toNewsResponse(news);
    }


    @PatchMapping (value = "/{newsId:.+}/enableNews")
    @PreAuthorize("hasRole('ADMIN')")
    public void enableNews(@PathVariable UUID newsId, @RequestParam boolean enable){

        newsService.enableNews(newsId, enable);
    }


    @DeleteMapping(value = "/{newsId:.+}/deleteNews")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Delete NewsEntity successfully")
    public void deleteNews(@PathVariable UUID newsId){

        newsService.deleteNews(newsId);
    }


    @GetMapping(value="/searchByTitle")
    @PreAuthorize("hasRole('ADMIN')")
    @DefaultMessage("Search by title successfully")
    public List<NewsResponse> searchByTitle(@RequestParam("title") String title) {

        List<NewsEntity> newsEntityList = newsService.searchByTitle(title);

        return NewsMapper.INSTANCE.toNewsResponseList(newsEntityList);
    }

}
