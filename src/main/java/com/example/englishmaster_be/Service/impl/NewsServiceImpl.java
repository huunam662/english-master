package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Model.*;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.INewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NewsServiceImpl implements INewsService {
    @Autowired
    private NewsRepository newsRepository;
    @Override
    public void save(News news) {
        newsRepository.save(news);
    }

    @Override
    public void delete(News news) {
        newsRepository.delete(news);
    }

    @Override
    public News findNewsById(UUID newsId) {
        return newsRepository.findByNewsId(newsId)
                .orElseThrow(() -> new IllegalArgumentException("News not found with ID: " + newsId));
    }
}

