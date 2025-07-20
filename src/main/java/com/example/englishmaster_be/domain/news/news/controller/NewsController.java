package com.example.englishmaster_be.domain.news.news.controller;

import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.common.dto.res.PageInfoRes;
import com.example.englishmaster_be.domain.news.news.dto.req.UpdateNewsReq;

import com.example.englishmaster_be.domain.news.news.dto.res.NewsPageRes;
import com.example.englishmaster_be.domain.news.news.dto.view.INewsPageView;
import com.example.englishmaster_be.domain.news.news.mapper.NewsMapper;
import com.example.englishmaster_be.domain.news.news.model.NewsEntity;
import com.example.englishmaster_be.domain.news.news.service.INewsService;
import com.example.englishmaster_be.domain.news.news.dto.req.CreateNewsReq;
import com.example.englishmaster_be.domain.news.news.dto.res.NewsRes;
import com.example.englishmaster_be.common.dto.res.ResourceKeyRes;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "News")
@RestController
@RequestMapping("/news")
public class NewsController {

    private final INewsService newsService;

    public NewsController(INewsService newsService) {
        this.newsService = newsService;
    }


    @GetMapping("/{newId}")
    public NewsRes getNewsById(@PathVariable("newId") UUID newId){

        NewsEntity newResult = newsService.getNewsById(newId);

        return NewsMapper.INSTANCE.toNewsResponse(newResult);
    }

    @PostMapping(value = "/createNews")
    @PreAuthorize("hasRole('ADMIN')")
    public ResourceKeyRes createNews(
            @RequestBody CreateNewsReq newsRequest
    ){

        return newsService.createNews(newsRequest);
    }


    @PutMapping(value = "/updateNews")
    @PreAuthorize("hasRole('ADMIN')")
    public ResourceKeyRes updateNews(
            @RequestBody UpdateNewsReq newsRequest
    ){

        return newsService.updateNews(newsRequest);
    }

    @GetMapping("/search")
    public List<NewsRes> searchNewsToTitle(@RequestParam(value = "title") String title){
        List<NewsEntity> newsSearch = newsService.searchToTitle(title);
        return NewsMapper.INSTANCE.toNewsResponseList(newsSearch);
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

    @GetMapping("/page")
    public PageInfoRes<NewsPageRes> getPageNews(@ModelAttribute @Valid PageOptionsReq optionsReq){
        Page<INewsPageView> newsPageViews = newsService.getPageNews(optionsReq);
        List<NewsPageRes> newsPageResList = NewsMapper.INSTANCE.toNewsPageResList(newsPageViews.getContent());
        Page<NewsPageRes> pageResResult = new PageImpl<>(newsPageResList, newsPageViews.getPageable(), newsPageViews.getTotalElements());
        return new PageInfoRes<>(pageResResult);
    }

}
