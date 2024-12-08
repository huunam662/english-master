package com.example.englishmaster_be.Controller;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.Configuration.global.annotation.MessageResponse;
import com.example.englishmaster_be.DTO.News.NewsFilterRequest;
import com.example.englishmaster_be.DTO.News.UpdateNewsDTO;
import com.example.englishmaster_be.DTO.News.SaveNewsDTO;
import com.example.englishmaster_be.Model.Response.*;
import com.example.englishmaster_be.Service.*;
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
@RequestMapping("/api/news")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewsController {

    INewsService newsService;


    @GetMapping(value = "/listNewsAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("List News successfully")
    public FilterResponse<?> listNewsOfAdmin(
            @RequestParam(value = "page", defaultValue = "0") @Min(0) Integer page,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) Integer size,
            @RequestParam(value = "sortBy", defaultValue = "updateAt") String sortBy,
            @RequestParam(value = "direction", defaultValue = "DESC") Sort.Direction sortDirection,
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(value = "enable", defaultValue = "true") boolean isEnable
    ){

        NewsFilterRequest filterRequest = NewsFilterRequest.builder()
                .page(page)
                .search(search)
                .size(size)
                .sortBy(sortBy)
                .isEnable(isEnable)
                .sortDirection(sortDirection)
                .build();

        return newsService.listNewsOfAdmin(filterRequest);
    }


    @GetMapping(value = "/listNewsUser")
    @MessageResponse("List News successfully")
    public List<NewsResponse> listNewsOfUser(
            @RequestParam(value = "size", defaultValue = "5") @Min(1) @Max(100) Integer size
    ){

        NewsFilterRequest newsFilterRequest = NewsFilterRequest.builder()
                .size(size)
                .build();

        return newsService.listNewsOfUser(newsFilterRequest);
    }


    @PostMapping(value = "/createNews" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Create News successfully")
    public NewsResponse createNews(
            @ModelAttribute("contentNews") SaveNewsDTO createNewsDTO
    ){

        return newsService.saveNews(createNewsDTO);
    }


    @PatchMapping(value = "/{newsId:.+}/updateNews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Update News successfully")
    public NewsResponse updateNews(
            @PathVariable UUID newsId,
            @ModelAttribute("contentNews") UpdateNewsDTO updateNewsDTO
    ){

        updateNewsDTO.setNewsId(newsId);

        return newsService.saveNews(updateNewsDTO);
    }


    @PatchMapping (value = "/{newsId:.+}/enableNews")
    @PreAuthorize("hasRole('ADMIN')")
    public void enableNews(@PathVariable UUID newsId, @RequestParam boolean enable){

        newsService.enableNews(newsId, enable);
    }


    @DeleteMapping(value = "/{newsId:.+}/deleteNews")
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Delete News successfully")
    public void deleteNews(@PathVariable UUID newsId){

        newsService.deleteNews(newsId);
    }


    @GetMapping(value="/searchByTitle")
    @PreAuthorize("hasRole('ADMIN')")
    @MessageResponse("Search by title successfully")
    public List<NewsResponse> searchByTitle(@RequestParam("title") String title) {

        return newsService.searchByTitle(title);
    }

}
