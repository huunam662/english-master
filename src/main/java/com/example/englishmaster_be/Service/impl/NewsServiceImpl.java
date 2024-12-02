package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Common.dto.response.FilterResponse;
import com.example.englishmaster_be.Configuration.global.thread.MessageResponseHolder;
import com.example.englishmaster_be.DTO.News.CreateNewsDTO;
import com.example.englishmaster_be.DTO.News.NewsFilterRequest;
import com.example.englishmaster_be.DTO.News.UpdateNewsDTO;
import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Model.Response.ExceptionResponseModel;
import com.example.englishmaster_be.Model.Response.NewsResponse;
import com.example.englishmaster_be.Model.Response.ResponseModel;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.IFileStorageService;
import com.example.englishmaster_be.Service.INewsService;
import com.google.cloud.storage.Blob;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewsServiceImpl implements INewsService {

    NewsRepository newsRepository;

    JPAQueryFactory queryFactory;

    IFileStorageService fileStorageService;


    @Override
    public News findNewsById(UUID newsId) {
        return newsRepository.findByNewsId(newsId)
                .orElseThrow(
                        () -> new IllegalArgumentException("News not found with ID: " + newsId)
                );
    }


    @Override
    public FilterResponse<?> listNewsOfAdmin(NewsFilterRequest filterRequest) {

        FilterResponse<NewsResponse> filterResponse = FilterResponse.<NewsResponse>builder()
                .pageNumber(filterRequest.getPage())
                .pageSize(filterRequest.getSize())
                .offset((long) (filterRequest.getPage() - 1) * filterRequest.getSize())
                .content(new ArrayList<>())
                .build();

        JPAQuery<News> query = queryFactory.selectFrom(QNews.news);

        OrderSpecifier<?> orderSpecifier;

        if(filterRequest.getIsEnable() != null)
            query.where(QNews.news.enable.eq(filterRequest.getIsEnable()));

        if (filterRequest.getSearch() != null && !filterRequest.getSearch().isEmpty()) {

            String likeExpression = "%" + filterRequest.getSearch().trim().toLowerCase().replaceAll("\\s+", "%") + "%";

            BooleanExpression queryConditionPattern = QNews.news.title.equalsIgnoreCase(likeExpression)
                            .or(QNews.news.content.equalsIgnoreCase(likeExpression));

            query.where(queryConditionPattern);
        }

        long totalElements = Optional.ofNullable(query.select(QNews.news.count()).fetchOne()).orElse(0L);
        long totalPages = (long) Math.ceil((float) totalElements / filterResponse.getPageSize());
        filterResponse.setTotalElements(totalElements);
        filterResponse.setTotalPages(totalPages);
        filterResponse.withPreviousAndNextPage();

        if(Sort.Direction.DESC.equals(filterRequest.getSortDirection()))
            orderSpecifier = QNews.news.updateAt.desc();
        else orderSpecifier = QNews.news.updateAt.asc();

        query.orderBy(orderSpecifier)
            .offset(filterResponse.getOffset())
            .limit(filterResponse.getPageSize());

        filterResponse.setContent(
                query.fetch().stream().map(NewsResponse::new).toList()
        );

        return filterResponse;
    }

    @Override
    public List<NewsResponse> listNewsOfUser(NewsFilterRequest filterRequest) {

        OrderSpecifier<?> orderSpecifier = QNews.news.updateAt.desc();

        JPAQuery<News> query = queryFactory.selectFrom(QNews.news)
                .orderBy(orderSpecifier)
                .limit(filterRequest.getSize());

        query.where(QNews.news.enable.eq(true));

        return query.fetch().stream().map(NewsResponse::new).toList();
    }

    @Transactional
    @Override
    public NewsResponse saveNews(CreateNewsDTO newsDTO) {

        News news;

        if(newsDTO instanceof UpdateNewsDTO updateNewsDTO){

            news = findNewsById(updateNewsDTO.getNewsId());
            news.setTitle(updateNewsDTO.getTitle());
            news.setContent(updateNewsDTO.getContent());
        }
        else news = News.builder()
                .title(newsDTO.getTitle())
                .content(newsDTO.getContent())
                .build();

        if(newsDTO.getImage() != null && !newsDTO.getImage().isEmpty()){

            if (news.getImage() != null && !news.getImage().isEmpty())
                fileStorageService.delete(news.getImage());

            Blob blob = fileStorageService.save(newsDTO.getImage());

            String fileName = blob.getName();

            news.setImage(fileName);
        }

        news = newsRepository.save(news);

        return new NewsResponse(news);
    }

    @Transactional
    @Override
    public void enableNews(UUID newsId, boolean enable) {

        News news = findNewsById(newsId);

        news.setEnable(enable);

        if(enable) MessageResponseHolder.setMessage("Enable News successfully");
        else MessageResponseHolder.setMessage("Disable News successfully");

        newsRepository.save(news);
    }

    @Override
    public void deleteNews(UUID newsId) {

        News news = findNewsById(newsId);

        if(news.getImage() != null && !news.getImage().isEmpty())
            fileStorageService.delete(news.getImage());

        newsRepository.delete(news);

    }

    @Override
    public List<NewsResponse> searchByTitle(String title) {

        // Sử dụng queryFactory để tìm kiếm các tin tức có tiêu đề chứa chuỗi "title"

        String likePattern = "%" + title.trim().toLowerCase().replaceAll("\\s+", "%") + "%";

        JPAQuery<News> query = queryFactory.selectFrom(QNews.news)
                .where(QNews.news.title.likeIgnoreCase(likePattern));

        return query.fetch().stream().map(NewsResponse::new).toList();
    }
}

