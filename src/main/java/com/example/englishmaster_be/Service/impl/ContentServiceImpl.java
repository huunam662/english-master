package com.example.englishmaster_be.Service.impl;

import com.example.englishmaster_be.Model.Content;
import com.example.englishmaster_be.Repository.*;
import com.example.englishmaster_be.Service.IContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ContentServiceImpl implements IContentService {
    @Autowired
    private ContentRepository contentRepository;

    @Override
    public void uploadContent(Content content) {
        contentRepository.save(content);
    }

    @Override
    public void delete(Content content) {
        contentRepository.delete(content);
    }

    @Override
    public Content getContentToContentId(UUID contentId) {
        return contentRepository.findByContentId(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Content not found with ID: " + contentId));
    }
}
