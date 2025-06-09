package com.example.englishmaster_be.domain.news.service;

import com.example.englishmaster_be.advice.exception.template.ErrorHolder;
import com.example.englishmaster_be.common.constant.error.Error;
import com.example.englishmaster_be.domain.news.dto.request.UpdateNewsRequest;
import com.example.englishmaster_be.domain.news.model.QNewsEntity;
import com.example.englishmaster_be.domain.user.service.IUserService;
import com.example.englishmaster_be.domain.comment.repository.jdbc.CommentJdbcRepository;
import com.example.englishmaster_be.domain.comment.repository.jpa.CommentRepository;
import com.example.englishmaster_be.domain.user.model.UserEntity;
import com.example.englishmaster_be.shared.dto.response.FilterResponse;
import com.example.englishmaster_be.domain.news.mapper.NewsMapper;
import com.example.englishmaster_be.domain.news.dto.request.CreateNewsRequest;
import com.example.englishmaster_be.domain.news.dto.request.NewsFilterRequest;
import com.example.englishmaster_be.domain.news.dto.response.NewsResponse;
import com.example.englishmaster_be.domain.news.repository.NewsRepository;
import com.example.englishmaster_be.domain.news.model.NewsEntity;
import com.example.englishmaster_be.shared.dto.response.ResourceKeyResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;




@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NewsService implements INewsService {

    JPAQueryFactory queryFactory;

    NewsRepository newsRepository;

    CommentRepository commentRepository;

    CommentJdbcRepository commentJdbcRepository;

    IUserService userService;


    @Override
    public NewsEntity getNewsById(UUID newsId) {
        return newsRepository.findByNewsId(newsId)
                .orElseThrow(
                        () -> new ErrorHolder(Error.RESOURCE_NOT_FOUND, "NewsEntity not found with ID: " + newsId, false)
                );
    }


    @Override
    public FilterResponse<?> listNewsOfAdmin(NewsFilterRequest filterRequest) {

        FilterResponse<NewsResponse> filterResponse = FilterResponse.<NewsResponse>builder()
                .pageNumber(filterRequest.getPage())
                .pageSize(filterRequest.getPageSize())
                .offset((long) (filterRequest.getPage() - 1) * filterRequest.getPageSize())
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
    @SneakyThrows
    public ResourceKeyResponse createNews(CreateNewsRequest newsRequest) {

        UserEntity currentUser = userService.currentUser();

        boolean userIsAdmin = userService.currentUserIsAdmin(currentUser);

        if(!userIsAdmin) throw new ErrorHolder(Error.UNAUTHORIZED);

        NewsEntity newsEntity = NewsMapper.INSTANCE.toNewsEntity(newsRequest);

        newsEntity.setEnable(true);
        newsEntity.setUserCreate(currentUser);
        newsEntity.setUserUpdate(currentUser);

        newsEntity = newsRepository.save(newsEntity);

        return ResourceKeyResponse.builder()
                .resourceId(newsEntity.getNewsId())
                .build();
    }

    @Override
    public ResourceKeyResponse updateNews(UpdateNewsRequest newsRequest) {

        UserEntity currentUser = userService.currentUser();

        boolean userIsAdmin = userService.currentUserIsAdmin(currentUser);

        if(!userIsAdmin) throw new ErrorHolder(Error.UNAUTHORIZED);

        NewsEntity newsResultFetch = getNewsById(newsRequest.getNewsId());

        NewsMapper.INSTANCE.flowToNewsEntity(newsRequest, newsResultFetch);

        newsResultFetch = newsRepository.save(newsResultFetch);

        return ResourceKeyResponse.builder()
                .resourceId(newsResultFetch.getNewsId())
                .build();
    }

    @Transactional
    @Override
    public void enableNews(UUID newsId, boolean enable) {

        NewsEntity news = getNewsById(newsId);

        news.setEnable(enable);

        newsRepository.save(news);
    }

    @Override
    public void deleteNews(UUID newsId) {

        NewsEntity news = getNewsById(newsId);

//        if(news.getImage() != null && !news.getImage().isEmpty())
//            fileStorageService.delete(news.getImage());

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

