package com.example.englishmaster_be.domain.news.news.service;

import com.example.englishmaster_be.advice.exception.ApplicationException;
import com.example.englishmaster_be.common.dto.req.PageOptionsReq;
import com.example.englishmaster_be.domain.news.news.dto.req.UpdateNewsReq;
import com.example.englishmaster_be.domain.news.news.dto.view.INewsPageView;
import com.example.englishmaster_be.domain.news.news.repository.NewsDslRepository;
import com.example.englishmaster_be.domain.user.user.service.IUserService;
import com.example.englishmaster_be.domain.user.user.model.UserEntity;
import com.example.englishmaster_be.domain.news.news.mapper.NewsMapper;
import com.example.englishmaster_be.domain.news.news.dto.req.CreateNewsReq;
import com.example.englishmaster_be.domain.news.news.repository.NewsRepository;
import com.example.englishmaster_be.domain.news.news.model.NewsEntity;
import com.example.englishmaster_be.common.dto.res.ResourceKeyRes;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class NewsService implements INewsService {

    private final NewsRepository newsRepository;
    private final IUserService userService;
    private final NewsDslRepository newsDslRepository;

    @Lazy
    public NewsService(NewsDslRepository newsDslRepository, NewsRepository newsRepository, IUserService userService) {
        this.newsRepository = newsRepository;
        this.userService = userService;
        this.newsDslRepository = newsDslRepository;
    }

    @Override
    public NewsEntity getNewsById(UUID newsId) {
        return newsRepository.findByNewsId(newsId)
                .orElseThrow(
                        () -> new ApplicationException(HttpStatus.NOT_FOUND, "News not found with id: " + newsId)
                );
    }

    @Override
    public Page<INewsPageView> getPageNews(PageOptionsReq optionsReq) {
        return newsDslRepository.findPageNews(optionsReq);
    }

    @Transactional
    @Override
    @SneakyThrows
    public ResourceKeyRes createNews(CreateNewsReq newsRequest) {

        UserEntity currentUser = userService.currentUser();

        boolean userIsAdmin = userService.currentUserIsAdmin(currentUser);

        if(!userIsAdmin) throw new ApplicationException(HttpStatus.UNAUTHORIZED, "User is not admin.");

        NewsEntity newsEntity = NewsMapper.INSTANCE.toNewsEntity(newsRequest);

        newsEntity.setEnable(true);
        newsEntity.setUserCreate(currentUser);
        newsEntity.setUserUpdate(currentUser);

        newsEntity = newsRepository.save(newsEntity);

        return new ResourceKeyRes(newsEntity.getNewsId());
    }

    @Override
    public ResourceKeyRes updateNews(UpdateNewsReq newsRequest) {

        UserEntity currentUser = userService.currentUser();

        boolean userIsAdmin = userService.currentUserIsAdmin(currentUser);

        if(!userIsAdmin) throw new ApplicationException(HttpStatus.UNAUTHORIZED, "User is not admin.");

        NewsEntity newsResultFetch = getNewsById(newsRequest.getNewsId());

        NewsMapper.INSTANCE.flowToNewsEntity(newsRequest, newsResultFetch);

        newsResultFetch = newsRepository.save(newsResultFetch);

        return new ResourceKeyRes(newsResultFetch.getNewsId());
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

        newsRepository.delete(news);

    }

    @Override
    public List<NewsEntity> searchToTitle(String title) {
        return newsRepository.findAllEntityByTitle(title, PageRequest.of(0, 10));
    }

}

