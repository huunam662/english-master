package com.example.englishmaster_be.domain.news.service;

import com.example.englishmaster_be.common.dto.response.FilterResponse;
import com.example.englishmaster_be.common.thread.MessageResponseHolder;
import com.example.englishmaster_be.mapper.NewsMapper;
import com.example.englishmaster_be.domain.news.dto.request.NewsRequest;
import com.example.englishmaster_be.domain.news.dto.request.NewsFilterRequest;
import com.example.englishmaster_be.domain.news.dto.response.NewsResponse;
import com.example.englishmaster_be.model.news.NewsRepository;
import com.example.englishmaster_be.domain.file_storage.service.IFileStorageService;
import com.example.englishmaster_be.model.news.NewsEntity;
import com.example.englishmaster_be.model.news.QNewsEntity;
import com.google.cloud.storage.Blob;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired, @Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewsService implements INewsService {

    JPAQueryFactory queryFactory;

    NewsRepository newsRepository;

    IFileStorageService fileStorageService;


    @Override
    public NewsEntity findNewsById(UUID newsId) {
        return newsRepository.findByNewsId(newsId)
                .orElseThrow(
                        () -> new IllegalArgumentException("NewsEntity not found with ID: " + newsId)
                );
    }


    @Override
    public FilterResponse<?> listNewsOfAdmin(NewsFilterRequest filterRequest) {

        FilterResponse<NewsResponse> filterResponse = FilterResponse.<NewsResponse>builder()
                .pageNumber(filterRequest.getPage())
                .pageSize(filterRequest.getPageSize())
                .offset((long) (filterRequest.getPage() - 1) * filterRequest.getPageSize())
                .content(new ArrayList<>())
                .build();

        BooleanExpression wherePattern = QNewsEntity.newsEntity.isNotNull();

        if(filterRequest.getIsEnable() != null)
            wherePattern = wherePattern.and(QNewsEntity.newsEntity.enable.eq(filterRequest.getIsEnable()));

        if (filterRequest.getSearch() != null && !filterRequest.getSearch().isEmpty()) {

            String likeExpression = "%" + filterRequest.getSearch().trim().toLowerCase().replaceAll("\\s+", "%") + "%";

            wherePattern = wherePattern.and(
                                            QNewsEntity.newsEntity.title.equalsIgnoreCase(likeExpression)
                                            .or(QNewsEntity.newsEntity.content.equalsIgnoreCase(likeExpression))
                                    );
        }

        long totalElements = Optional.ofNullable(
                    queryFactory.select(QNewsEntity.newsEntity.count()).from(QNewsEntity.newsEntity).where(wherePattern).fetchOne()
                ).orElse(0L);
        long totalPages = (long) Math.ceil((float) totalElements / filterResponse.getPageSize());
        filterResponse.setTotalPages(totalPages);

        OrderSpecifier<?> orderSpecifier;

        if(Sort.Direction.DESC.equals(filterRequest.getSortDirection()))
            orderSpecifier = QNewsEntity.newsEntity.updateAt.desc();
        else orderSpecifier = QNewsEntity.newsEntity.updateAt.asc();

        JPAQuery<NewsEntity> query = queryFactory
                                    .selectFrom(QNewsEntity.newsEntity)
                                    .where(wherePattern)
                                    .orderBy(orderSpecifier)
                                    .offset(filterResponse.getOffset())
                                    .limit(filterResponse.getPageSize());

        filterResponse.setContent(
                NewsMapper.INSTANCE.toNewsResponseList(query.fetch())
        );

        return filterResponse;
    }

    @Override
    public List<NewsEntity> listNewsOfUser(NewsFilterRequest filterRequest) {

        OrderSpecifier<?> orderSpecifier = QNewsEntity.newsEntity.updateAt.desc();

        JPAQuery<NewsEntity> query = queryFactory.selectFrom(QNewsEntity.newsEntity)
                .orderBy(orderSpecifier)
                .limit(filterRequest.getPageSize());

        query.where(QNewsEntity.newsEntity.enable.eq(true));

        return query.fetch();
    }

    @Transactional
    @Override
    public NewsEntity saveNews(NewsRequest newsRequest) {

        NewsEntity news;

        if(newsRequest.getNewsId() != null)
            news = findNewsById(newsRequest.getNewsId());

        else news = NewsEntity.builder()
                .createAt(LocalDateTime.now())
                .build();

        NewsMapper.INSTANCE.flowToNewsEntity(newsRequest, news);

        news.setUpdateAt(LocalDateTime.now());

        if(newsRequest.getImage() != null && !newsRequest.getImage().isEmpty()){

            if (news.getImage() != null && !news.getImage().isEmpty())
                fileStorageService.delete(news.getImage());

            Blob blob = fileStorageService.save(newsRequest.getImage());

            String fileName = blob.getName();

            news.setImage(fileName);
        }

        return newsRepository.save(news);
    }

    @Transactional
    @Override
    public void enableNews(UUID newsId, boolean enable) {

        NewsEntity news = findNewsById(newsId);

        news.setEnable(enable);

        if(enable) MessageResponseHolder.setMessage("Enable NewsEntity successfully");
        else MessageResponseHolder.setMessage("Disable NewsEntity successfully");

        newsRepository.save(news);
    }

    @Override
    public void deleteNews(UUID newsId) {

        NewsEntity news = findNewsById(newsId);

        if(news.getImage() != null && !news.getImage().isEmpty())
            fileStorageService.delete(news.getImage());

        newsRepository.delete(news);

    }

    @Override
    public List<NewsEntity> searchByTitle(String title) {

        // Sử dụng queryFactory để tìm kiếm các tin tức có tiêu đề chứa chuỗi "title"

        String likePattern = "%" + title.trim().toLowerCase().replaceAll("\\s+", "%") + "%";

        JPAQuery<NewsEntity> query = queryFactory.selectFrom(QNewsEntity.newsEntity)
                .where(QNewsEntity.newsEntity.title.likeIgnoreCase(likePattern));

        return query.fetch();
    }
}

